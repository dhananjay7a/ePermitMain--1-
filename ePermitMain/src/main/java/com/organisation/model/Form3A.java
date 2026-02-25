package com.organisation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.organisation.constants.OrgConstants;
import com.organisation.repository.RegisterAdditionalDetailsTempRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;
import com.register.model.RegisterAdditionalDetailsTemp;

@Service("form3AService")
public class Form3A {

    @Value("${file.formThreeA.pdf.dir:./formThreeA-pdf/}")
    private String pdfStoragePath;

    private PdfFont font = null;
    private static final Logger log = LoggerFactory.getLogger(Form3A.class);

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserMstrRepository userMstrRepository;

    @Autowired
    private RegisterAdditionalDetailsTempRepository additionalDetailsTempRepo;

    public Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph)
                .setFontSize(11)
                .setPadding(2);
        if (!isBorder) {
            cell.setBorder(Border.NO_BORDER);
        } else {
            cell.setBorder(new DoubleBorder(1));
        }
        cell.setTextAlignment(TextAlignment.LEFT);
        if (8 == (border & 8)) {
            cell.setBorderRight(new SolidBorder(1));
            cell.setBorderBottom(new SolidBorder(1));
        }
        if (4 == (border & 4)) {
            cell.setBorderLeft(new SolidBorder(1));
        }
        if (2 == (border & 2)) {
            cell.setBorderBottom(new SolidBorder(1));
        }
        if (1 == (border & 1)) {
            cell.setBorderTop(new SolidBorder(1));
        }
        return cell;
    }

    public String createPdf(RegistrationMstr regMstr) {
        Document document = null;
        PdfDocument pdf = null;
        File file = null;
        String filePath = null;

        try {
            log.info("Inside Form3A->createPdf()");

            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return null;
            }

            UserMstr user = userMstrRepository.findByOrgId(regMstr.getOrgId());
            RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo
                    .findByOrgId(regMstr.getOrgId().toUpperCase());

            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            String orgId = regMstr.getOrgId().toUpperCase();
            String folderPath = pdfStoragePath + orgId;

            file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            if (OrgUtil.isNeitherNullNorEmpty(regMstr.getIsProfileUpdated())
                    && OrgConstants.YES.equals(regMstr.getIsProfileUpdated())) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File oldFile : files) {
                        oldFile.delete();
                    }
                }
            }

            filePath = folderPath + File.separator + "formThreeA.pdf";
            file = new File(filePath);

            if (file.exists()) {
                FileInputStream fi = null;
                try {
                    fi = new FileInputStream(file);
                    PdfReader pdfreader = new PdfReader(fi);
                    if (pdfreader != null) {
                        PdfDocument pdfDoc = new PdfDocument(pdfreader);
                        log.info("No of Pages in {} {}", file.getName(), pdfDoc.getNumberOfPages());
                        pdfDoc.close();
                        pdfreader.close();
                        fi.close();
                        return filePath;
                    }
                } catch (Exception e) {
                    if (fi != null) {
                        fi.close();
                    }
                    log.info("File Corrupt ::{}", filePath);
                    FileUtils.forceDelete(file);
                    if (file.exists()) {
                        log.info("Response : {}", OrgConstants.PDF_RESPONSE.ERROR);
                        return filePath;
                    } else {
                        log.info("Response : {}", OrgConstants.PDF_RESPONSE.ERROR);
                        log.info("File Corrupt Deleted ::{}", filePath);
                    }
                }
            } else {
                log.info("New File Created ::{}", filePath);
            }

            pdf = new PdfDocument(new PdfWriter(filePath));
            document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            String licenseFeesDetails = "";
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String regFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION,
                    regMstr.getOrgCategory());
            String pmaFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.private_market_application);
            try {
                if (regMstr.isLicenseExists()) {
                    licenseFeesDetails = "AS PER MANUAL RECORDS";
                } else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PRIVATE_MARKET_APPLICATION
                        .equals(regMstr.getOrgCategory())) {
                    licenseFeesDetails = "Rs. " + safe(pmaFees) + " | Receipt No. " + safe(regMstr.getRegReceiptNo())
                            + " | Date : "
                            + dateFormat.format(OrgUtil.isNeitherNullNorEmpty(regMstr.getRegistrationSubmitDate())
                                    ? regMstr.getRegistrationSubmitDate()
                                    : new Date());
                } else {
                    licenseFeesDetails = "Rs. " + safe(regFees) + " | Receipt No. " + safe(regMstr.getRegReceiptNo())
                            + " | Date : "
                            + dateFormat.format(OrgUtil.isNeitherNullNorEmpty(regMstr.getRegistrationSubmitDate())
                                    ? regMstr.getRegistrationSubmitDate()
                                    : new Date());
                }
            } catch (Exception e) {
                log.info("Exception :- {}", e.getMessage());
            }

            Paragraph para = new Paragraph().setFont(font)
                    .setFontSize(11);
            para.add(new Text("FORM 3A").setBold())
                    .add("\n")
                    .add("\n")
                    .add(new Text("[See rule 4(A)]").setBold())
                    .add("\n")
                    .add("\n")
                    .add(new Text("Application for licence under section 13A (1) of the West Bengal Agricultural ")
                            .setBold())
                    .add("\n")
                    .add(new Text(
                            "Produce Marketing (Regulation) Act, 1972 and as amended thereof, for the purposes of ")
                            .setBold())
                    .add("\n")
                    .add(new Text("setting up of ").setBold())
                    .add("\n")
                    .add(new Text("Private market Yard within a market area or in more than one market areas ")
                            .setBold())
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);

            Paragraph para1 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para1.add(new Text("To"))
                    .add("\n");
            if (!regMstr.isUnifiedLicense()) {
                para1.add("The ")
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())
                                ? regMstr.getOrgBaseMarket().toUpperCase()
                                : "...............................").setBold().setUnderline())
                        .add(new Text(" Regulated Market Committee"));
            } else {
                para1.add("Chief Executive Officer, West Bengal State Agricultural Marketing Board");
            }

            para1.add("\n")
                    .add("\n")
                    .add("Sir,")
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Paragraph para3 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para3.add(new Text("I beg to apply for a licence under section 13A (1) of the West Bengal Agricultural "))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(55)
                    .setWordSpacing(5)
                    .setMultipliedLeading(1.2f);

            Paragraph para4 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para4.add(new Text("Produce Marketing (Regulation) Act,1972. Necessary particulars are given below:"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Table table = new Table(UnitValue.createPercentArray(new float[] { 12, 88 }));
            table.setWidth(UnitValue.createPercentValue(100));

            Paragraph para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            para5.add(new Text("1.(a)"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            para5.add(new Text("Name of the applicant for which licence applied for - "))
                    .add(new Text(
                            OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgName()) ? regMstr.getOrgName().toUpperCase()
                                    : " ")
                            .setUnderline().setBold())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            para5.add(new Text("(b)"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f)
                    .setPaddingLeft(8);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);
            String indSop = (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgType())
                    && ("SOP".equals(regMstr.getOrgType()) || "IND".equals(regMstr.getOrgType()))) ? "Yes" : "No";
            boolean isIndSOP = "Yes".equals(indSop);
            para5.add(new Text("Whether applicant is an individual/proprietorship or not - "))
                    .add(new Text(indSop).setUnderline().setBold())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            para5.add(new Text("(c)"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f)
                    .setPaddingLeft(8);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            String fullAddress = safe(regMstr.getOrgAddress()) + ", "
                    + safe(regMstr.getOrgPoliceStation()) + ", "
                    + safe(regMstr.getOrgPostOffice()) + ", "
                    + safe(regMstr.getOrgDist()) + ", "
                    + safe(regMstr.getOrgPin());
            para5.add(new Text("Business Address in full (including P.S./P.O./District/Pin Code) - "))
                    .add("\n")
                    .add(new Text(
                            OrgUtil.isNeitherNullNorEmpty(fullAddress) ? fullAddress.toUpperCase().replaceAll("\n", " ")
                                    : " ")
                            .setUnderline().setBold())
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            para5.add(new Text("(d)"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f)
                    .setPaddingLeft(8);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0));

            para5 = new Paragraph().setFont(font)
                    .setFontSize(11);

            String mobile = user != null ? safe(user.getUserMobile()) : "";
            String email = user != null ? safe(user.getUserEmail()) : "";
            para5.add(new Text("Contact No. and e-mail ID, if any person associated with "))
                    .add("\n")
                    .add(new Text("[Along with address proof document]"))
                    .add("\n")
                    .add(new Text(documentType("AD") + "  " + mobile + " " + email).setUnderline().setBold())
                    .add("\n")
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("2.(a)").setFont(font).setFontSize(11), 1, 1, 0, false).setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Constitution of the farm/company/society - "))
                    .add(new Text(isIndSOP ? "NA" : getConstitution(regMstr.getOrgType())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(b)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Registered with whom? - "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getRegisteredWith()
                            : regMstr.getRegisteredWith())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(c)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Particulars of registration - "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getRegistrationDetails()
                            : regMstr.getRegistrationDetails())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(d)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Name of the Managing Director or Manager or Principal Officer who will actually conduct the business - "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getOrgContactPerson()
                            : regMstr.getOrgContactPerson())).setUnderline().setBold())
                    .add("\n\n")
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("3.(a)").setFont(font).setFontSize(11), 1, 1, 0, false).setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Whether the application is for setting up of a private market yard in one market area or private market yards in more than one market areas "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getPrivateMarketYardArea() : ""))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(b)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Name or style under which the private market yard / yards will be setup "))
                    .add(new Text(
                            value(additionalDetails != null ? additionalDetails.getStyleForPrivateMarketYard() : ""))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(c)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Situation of the private market yard / yards as the case may be with particulars as "))
                    .add("\n")
                    .add(new Text(
                            "to the market area / market areas, police station / village / town / premises number / plot number / area boundary of premises (with a site map and plan) "))
                    .add(new Text(
                            value(additionalDetails != null ? additionalDetails.getSituationPrivateMarketYard() : ""))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(d)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Nature of the applicant's interest on the land and premises "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getNatureOfInterestOnLand() : ""))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(e)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            String marketYear = "";
            String marketYearRaw = additionalDetails != null ? additionalDetails.getMarketYearApp()
                    : regMstr.getMarketYearApp();
            if (OrgUtil.isNeitherNullNorEmpty(marketYearRaw) && marketYearRaw.contains("-")) {
                String[] arr = marketYearRaw.split("-");
                if (arr.length == 2) {
                    marketYear = "20" + arr[0] + "-20" + arr[1];
                }
            }
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Market year or part thereof for which licence is applied for -"))
                    .add(new Text(marketYear).setUnderline().setBold())
                    .add("\n\n")
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("4.(a)").setFont(font).setFontSize(11), 1, 1, 0, false).setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Has the applicant any bank account? If so the bank detail should be furnished "))
                    .add(new Text(value(regMstr.getOrgBankName())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(b)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Details of applicant's past experience in the business "))
                    .add(new Text(value(
                            additionalDetails != null ? additionalDetails.getPrevExpRemarks() : regMstr.getPrevExp()))
                            .setUnderline().setBold())
                    .add("\n\n")
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(c)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Whether the applicants holds any licence from this or any Market Committee with full particulars thereof "))
                    .add(new Text(yesNo(
                            additionalDetails != null ? additionalDetails.getHadLicense() : regMstr.getPrevLicense()))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(d)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Whether the applicants previously hold any licence from any Market Committee with full particulars thereof "))
                    .add(new Text(yesNo(additionalDetails != null ? additionalDetails.getHadMarketLicense()
                            : regMstr.getPrevmarketLicense())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(e)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Details of water supply, drainage, sanitation, shelter, parking, storage and other amenities made or proposed to be made by the applicant in the place of business for which the licence is applied for "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getAmenitiesDetails()
                            : regMstr.getCommodityCode())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(f)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Details regarding frequency (date on which held), usual hours of transaction, nature and volume of commodities to be handled, number of persons assembling "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getTransactionDetails()
                            : regMstr.getGodownDetails())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(g)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "Whether the applicant is prepared to furnish cash security or bank guaranty as may be required by the Market Committee "))
                    .add(new Text(yesNo(
                            additionalDetails != null ? additionalDetails.getIsBGReady() : regMstr.getIsBGReady()))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(h)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Details of employees / assistant / agents "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getEmployeeDetails()
                            : regMstr.getEmployeeDetails())).setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(i)").setFont(font).setFontSize(11).setPaddingLeft(8), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text(
                            "If the applicant had ever been held guilty of professional misconduct, details of convictions "))
                    .add(new Text(value(
                            additionalDetails != null ? additionalDetails.getGuiltyRemarks() : regMstr.getGuiltyDoc()))
                            .setUnderline().setBold())
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("5.").setFont(font).setFontSize(11).setPaddingLeft(10), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Amount of licence fee deposited with particulars of receipt number and date. "))
                    .add(new Text(licenseFeesDetails).setUnderline().setBold())
                    .add("\n\n")
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            Paragraph decl = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("6. Declaration -\n"))
                    .add(new Text("I certify that the facts stated above are true to the best of my knowledge.\n"))
                    .add(new Text(
                            "I hereby undertake to abide by the conditions of licence, the provisions of the West Bengal Agricultural Produce Marketing (Regulation) Act, 1972 and as amended thereof and the rules framed there under and the byelaws made there under by the Regulated Market Committee.\n"))
                    .add(new Text(
                            "I shall be responsible for all acts, omissions and commissions of my employees, if it so happens."))
                    .setMarginTop(8)
                    .setMultipliedLeading(1.2f);

            Table table2 = new Table(UnitValue.createPercentArray(new float[] { 45, 10, 45 }));
            table2.setWidth(UnitValue.createPercentValue(100));
            table2.setMarginTop(16);

            Paragraph left = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Full address of the applicant\n"))
                    .add(new Text(value(regMstr.getOrgAddress())).setBold());

            Paragraph right = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Yours faithfully\n\nSignature (in full) of the applicant\n\nDate:"));

            table2.addCell(createCell(left, 1, 1, 0, false));
            table2.addCell(new Cell().setBorder(Border.NO_BORDER));
            table2.addCell(createCell(right, 1, 1, 0, false));

            document.add(para);
            document.add(para1);
            document.add(para3);
            document.add(para4);
            document.add(table);
            document.add(decl);
            document.add(table2);

        } catch (IOException e) {
            log.error("Error creating Form 3A PDF", e);
        } catch (Exception e) {
            log.error("Unhandled exception in Form3A.createPdf", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
        log.info("Response : {} | {}", filePath, OrgConstants.PDF_RESPONSE.SUCCESS);
        return filePath;
    }

    public void deletePdf(RegistrationMstr regMstr) {
        String filePath = null;
        try {
            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return;
            }
            String orgId = regMstr.getOrgId().toUpperCase();
            filePath = pdfStoragePath + orgId + File.separator + "formThreeA.pdf";
            File file = new File(filePath);
            if (file.exists()) {
                log.info("Deleting file: {}", filePath);
                file.delete();
            }
        } catch (Exception e) {
            log.error("Error deleting Form 3A PDF at {}", filePath, e);
        }
    }

    private String documentType(String doc) {
        switch (doc) {
            case "AD":
                return "AADHAR CARD";
            case "AG":
                return "GOVT. ID OF AUTHORISED PERSON";
            case "AP":
                return "ID OF AUTHORISED PERSON";
            case "CN":
                return "CIN";
            case "CR":
                return "COOPERATIVE REGISTRATION CERTIFICATE";
            case "DI":
                return "DATE OF INCORPORATION";
            case "DL":
                return "DRIVING LICENSE";
            case "PC":
                return "PAN CARD";
            case "PD":
                return "REG PARTNERSHIP DEED";
            case "PN":
                return "PAN CARD";
            case "PS":
                return "PASSPORT";
            case "RG":
                return "LICENCEE FEE";
            case "TD":
                return "TRADER LICENSE";
            case "VD":
                return "VOTER ID";
            default:
                return "AADHAR CARD";
        }
    }

    private String getConstitution(String con) {
        if (!OrgUtil.isNeitherNullNorEmpty(con)) {
            return "NA";
        }
        switch (con) {
            case "GOV":
                return "GOVERNMENT";
            case "PAR":
                return "PARTNERSHIP";
            case "PVT":
                return "PVT LTD";
            case "LTL":
                return "LIMITED- LISTED";
            case "LTU":
                return "LIMITED- UNLISTED";
            case "STB":
                return "STATUTORY BODY";
            case "CRP":
                return "CORPORATION";
            case "COS":
                return "CO-OPERATIVE SOCIETY";
            case "SOP":
                return "SOLE PROPRIETORSHIP";
            case "IND":
                return "INDIVIDUAL";
            default:
                return "NA";
        }
    }

    private String yesNo(String value) {
        if (!OrgUtil.isNeitherNullNorEmpty(value)) {
            return "NO";
        }
        if ("Y".equalsIgnoreCase(value) || "YES".equalsIgnoreCase(value) || "TRUE".equalsIgnoreCase(value)) {
            return "YES";
        }
        return "NO";
    }

    private String value(String text) {
        if (!OrgUtil.isNeitherNullNorEmpty(text)) {
            return "";
        }
        return text.toUpperCase().replaceAll("\n", " ");
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}

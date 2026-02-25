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
import com.organisation.util.OrgUtil;
import com.register.model.RegisterAdditionalDetailsTemp;

@Service("form3BService")
public class Form3B {

    @Value("${file.formThreeB.pdf.dir:./formThreeB-pdf/}")
    private String pdfStoragePath;

    private PdfFont font = null;
    private static final Logger log = LoggerFactory.getLogger(Form3B.class);

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
            log.info("Inside Form3B->createPdf()");

            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return null;
            }

            UserMstr user = userMstrRepository.findByOrgId(regMstr.getOrgId());
            RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo
                    .findByOrgId(regMstr.getOrgId().toUpperCase());

            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String orgId = regMstr.getOrgId().toUpperCase();
            String folderPath = pdfStoragePath + orgId;

            file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            filePath = folderPath + File.separator + "formThreeB.pdf";
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

            Paragraph para = new Paragraph().setFont(font)
                    .setFontSize(11);
            para.add(new Text("Form 3B").setBold())
                    .add("\n")
                    .add("\n")
                    .add(new Text("[See rule 4A]"))
                    .add("\n")
                    .add("\n")
                    .add(new Text("Application for renewal of licence under section 13A(1) of the West Bengal")
                            .setBold())
                    .add("\n")
                    .add(new Text("Agricultural Produce Marketing (Regulation) Act, 1972").setBold())
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);

            String rupees = "Rs. 1000 ( Rupees One thousand Only )" + " | " + "Date : "
                    + dateFormat
                            .format(OrgUtil.isNeitherNullNorEmpty(regMstr.getRenewalDate()) ? regMstr.getRenewalDate()
                                    : new Date());

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

            Paragraph para2 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para2.add(new Text("I request for the renewal of the Licence No "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId()) ? regMstr.getOrgId().toUpperCase()
                            : "..............................................").setBold().setUnderline())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(60)
                    .setMultipliedLeading(1.2f);

            Paragraph para3 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para3.add(new Text("of which was granted to me on "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgValidFrom())
                            ? dateFormat.format(regMstr.getOrgValidFrom())
                            : "............................").setBold().setUnderline())
                    .add(new Text(" for carrying on the business of "))
                    .add(new Text(value(regMstr.getCommodityCode())).setBold().setUnderline())
                    .add(new Text(" in "))
                    .add(new Text(value(regMstr.getOrgBaseMarket())).setBold().setUnderline())
                    .add(new Text(" market within "))
                    .add(new Text(value(regMstr.getOrgDist())).setBold().setUnderline())
                    .add(new Text(" market area in "))
                    .add(new Text(value(regMstr.getCommodityCode())).setBold().setUnderline())
                    .add(new Text(" (the name of the agricultural produce)."))
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Paragraph para4 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para4.add(new Text("1. The necessary particulars are furnished below:"))
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Table table = new Table(UnitValue.createPercentArray(new float[] { 12, 88 }));
            table.setWidth(UnitValue.createPercentValue(100));

            Paragraph para5 = new Paragraph().setFont(font).setFontSize(11);
            para5.add(new Text("(i)(a)"));
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0));

            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Name of the applicant "))
                    .add(new Text(value(regMstr.getOrgName())).setBold().setUnderline());
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(b)").setFont(font).setFontSize(11).setPaddingLeft(13), 1, 1, 0, false)
                            .setPadding(0));
            String mobile = user != null ? safe(user.getUserMobile()) : "";
            String email = user != null ? safe(user.getUserEmail()) : "";
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Contact No and e-mail ID - "))
                    .add(new Text(documentType("AD") + "  " + mobile + " " + email).setUnderline().setBold())
                    .add("\n");
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(c)").setFont(font).setFontSize(11).setPaddingLeft(13), 1, 1, 0, false)
                            .setPadding(0));
            String fullAddress = safe(regMstr.getOrgAddress()) + ", "
                    + safe(regMstr.getOrgPoliceStation()) + ", "
                    + safe(regMstr.getOrgPostOffice()) + ", "
                    + safe(regMstr.getOrgDist()) + ", "
                    + safe(regMstr.getOrgPin());
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Business Address in full - "))
                    .add(new Text(
                            OrgUtil.isNeitherNullNorEmpty(fullAddress) ? fullAddress.toUpperCase().replaceAll("\n", " ")
                                    : " ")
                            .setUnderline().setBold())
                    .add("\n");
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(d)").setFont(font).setFontSize(11).setPaddingLeft(13), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Place/places of business "))
                    .add(new Text(value(regMstr.getOrgAddress())).setBold().setUnderline())
                    .add(new Text(" within "))
                    .add(new Text(value(regMstr.getOrgDist())).setBold().setUnderline())
                    .add(new Text(" market area/areas."));
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(20));

            table.addCell(
                    createCell(new Paragraph("(ii)").setFont(font).setFontSize(11), 1, 1, 0, false).setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Name of the agricultural produce which he wants to deal in "))
                    .add(new Text(value(regMstr.getCommodityCode())).setBold().setUnderline());
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(15));

            table.addCell(
                    createCell(new Paragraph("(iii)").setFont(font).setFontSize(11), 1, 1, 0, false).setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11).add(new Text("Past experience "));
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(15));

            table.addCell(
                    createCell(new Paragraph("(a)").setFont(font).setFontSize(11).setPaddingLeft(13), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Whether the terms and conditions of the licence were duly observed by the\n"))
                    .add(new Text("applicant "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getPrevLicenseRemarks()
                            : regMstr.getPrevLicense())).setBold().setUnderline());
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(15));

            table.addCell(
                    createCell(new Paragraph("(b)").setFont(font).setFontSize(11).setPaddingLeft(13), 1, 1, 0, false)
                            .setPadding(0));
            para5 = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Whether any action was taken against him by the Market Committee for any\n"))
                    .add(new Text("breach of the terms and condition "))
                    .add(new Text(value(additionalDetails != null ? additionalDetails.getPrevMarketLicenseRemarks()
                            : regMstr.getPrevmarketLicense())).setBold().setUnderline());
            table.addCell(createCell(para5, 1, 1, 0, false).setPadding(0).setPaddingLeft(15));

            Paragraph para6 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para6.add(new Text("2. The amount of fee deposited with particulars of receipt number and date."))
                    .add(new Text(rupees).setBold().setUnderline())
                    .add("\n")
                    .add("\n")
                    .add(new Text(
                            "3. I certify that the facts stated in the application above are true to the best of my knowledge."))
                    .add("\n")
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Paragraph para7 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para7.add(new Text("I hereby undertake to abide by the conditions of licence, the provision of the West "))
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(60)
                    .setMultipliedLeading(1.2f);

            Paragraph para8 = new Paragraph().setFont(font)
                    .setFontSize(11);
            para8.add(new Text(
                    "Bengal Agricultural Produce Marketing (Regulation) Act, 1972 and the rules and bye-laws made"))
                    .add("\n")
                    .add(new Text("thereunder."))
                    .add("\n")
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(25)
                    .setMultipliedLeading(1.2f);

            Table table2 = new Table(UnitValue.createPercentArray(new float[] { 45, 10, 45 }));
            table2.setWidth(UnitValue.createPercentValue(100));
            table2.setMarginTop(10);

            Paragraph left = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Full address of the applicant\n"))
                    .add(new Text(value(regMstr.getOrgAddress())).setBold())
                    .add("\n")
                    .add(new Text("Place:"));

            Paragraph right = new Paragraph().setFont(font).setFontSize(11)
                    .add(new Text("Yours faithfully\n\nSignature (in full) of the applicant\n\nDate:"));

            table2.addCell(createCell(left, 1, 1, 0, false));
            table2.addCell(new Cell().setBorder(Border.NO_BORDER));
            table2.addCell(createCell(right, 1, 1, 0, false));

            document.add(para);
            document.add(para1);
            document.add(para2);
            document.add(para3);
            document.add(para4);
            document.add(table);
            document.add(para6);
            document.add(para7);
            document.add(para8);
            document.add(table2);

        } catch (IOException e) {
            log.error("Error creating Form 3B PDF", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }
        log.info("Response : {} | {}", filePath, OrgConstants.PDF_RESPONSE.SUCCESS);
        return filePath;
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

    private String value(String text) {
        if (!OrgUtil.isNeitherNullNorEmpty(text)) {
            return ".........................................";
        }
        return text.toUpperCase().replaceAll("\n", " ");
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }
}

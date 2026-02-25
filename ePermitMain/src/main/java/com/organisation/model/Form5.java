package com.organisation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;
import com.register.model.RegisterAdditionalDetailsTemp;

@Service("form5Service")
public class Form5 {

    @Value("${file.formFive.pdf.dir:${file.unsigned.pdf.dir:./unsigned-pdf/}}")
    private String pdfStoragePath;

    private static final Logger log = LoggerFactory.getLogger(Form5.class);

    @Autowired
    private CommonService commonService;

    @Autowired
    private RegisterAdditionalDetailsTempRepository additionalDetailsTempRepo;

    private Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph)
                .setFontSize(12)
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

    private String pw(int number, String suffix) {
        String[] one = { " ", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
                " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
                " Nineteen" };
        String[] ten = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty",
                " Ninety" };

        String result;
        if (number > 19) {
            result = ten[number / 10] + one[number % 10];
        } else {
            result = one[number];
        }
        return number > 0 ? result + suffix : result;
    }

    private String getWords(int number) {
        return pw((number / 1000000000), " Hundred")
                + pw((number / 10000000) % 100, " Crore")
                + pw(((number / 100000) % 100), " Lakh")
                + pw(((number / 1000) % 100), " Thousand")
                + pw(((number / 100) % 10), " Hundred")
                + pw((number % 100), " ");
    }

    public String createPdf(RegistrationMstr regMstr) {
        Document document = null;
        PdfDocument pdf = null;
        File file = null;
        String filePath = null;

        try {
            log.info("Inside Form5->createPdf()");

            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return null;
            }

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo
                    .findByOrgId(regMstr.getOrgId().toUpperCase());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            String orgId = regMstr.getOrgId().toUpperCase();
            String folderPath = pdfStoragePath + orgId + File.separator + "form5";

            file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            File[] files = file.listFiles();
            if (files != null) {
                for (File form5File : files) {
                    form5File.delete();
                }
            }

            filePath = folderPath + File.separator + "Form5.pdf";
            file = new File(filePath);

            if (file.exists()) {
                FileInputStream fi = null;
                try {
                    fi = new FileInputStream(file);
                    PdfReader pdfreader = new PdfReader(fi);
                    PdfDocument pdfDoc = new PdfDocument(pdfreader);
                    log.info("No of Pages in {} {}", file.getName(), pdfDoc.getNumberOfPages());
                    pdfDoc.close();
                    pdfreader.close();
                    fi.close();
                    return filePath;
                } catch (Exception e) {
                    if (fi != null) {
                        fi.close();
                    }
                    log.info("File Corrupt ::{}", filePath);
                    FileUtils.forceDelete(file);
                }
            } else {
                log.info("New File Created ::{}", filePath);
            }

            pdf = new PdfDocument(new PdfWriter(filePath));
            document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);

            String licenseFeesDetails;
            String regFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION,
                    regMstr.getOrgCategory());
            String renewalFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.RENEWAL,
                    regMstr.getOrgCategory());
            String pmaFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.private_market_application);

            if (regMstr.isLicenseExists()) {
                licenseFeesDetails = "AS PER MANUAL RECORDS";
            } else if (OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PRIVATE_MARKET_APPLICATION
                    .equals(regMstr.getOrgCategory())) {
                int amount = parseIntSafe(pmaFees);
                licenseFeesDetails = "Rs. " + pmaFees + "/- (Rupees " + getWords(amount) + "Only)";
            } else {
                int amount = parseIntSafe(regFees);
                licenseFeesDetails = "Rs. " + regFees + "/- (Rupees " + getWords(amount) + "Only)";
            }

            if (OrgConstants.RENEWAL_STATUS.APPLICABLE.equals(regMstr.getOrgRenewalStatus())) {
                int amount = parseIntSafe(renewalFees);
                licenseFeesDetails = "Rs. " + renewalFees + " (Rupees " + getWords(amount) + ")";
            }

            Paragraph header = new Paragraph().setFont(font).setFontSize(13).setMultipliedLeading(1.2f)
                    .add(new Text("FORM 5").setBold())
                    .add("\n")
                    .add(new Text("[See rule 6(2)]"))
                    .add("\n")
                    .add(new Text("Licence for purposes of setting up of ").setBold())
                    .add("\n")
                    .add(new Text("Private market Yard within a market area or in more than one market areas")
                            .setBold())
                    .setTextAlignment(TextAlignment.CENTER);

            Paragraph para1 = new Paragraph().setFont(font).setFontSize(12).setMultipliedLeading(1.2f)
                    .add(new Text("The "));

            if (!regMstr.isUnifiedLicense()) {
                para1.add(new Text(value(regMstr.getOrgBaseMarket())).setBold().setUnderline())
                        .add(new Text(" Regulated Market Committee"));
            } else {
                para1.add(new Text("West Bengal State Agricultural Marketing Board"));
            }

            para1.add("\n")
                    .add(new Text("Licence No "))
                    .add(new Text(value(regMstr.getOrgId())).setBold().setUnderline())
                    .add("\n")
                    .add(new Text("Licence is hereby granted to "))
                    .add(new Text(value(regMstr.getOrgName())).setBold().setUnderline())
                    .add("\n")
                    .add(new Text("of "))
                    .add(new Text(value(regMstr.getOrgAddress())).setBold().setUnderline())
                    .add(new Text(" Police Station "))
                    .add(new Text(value(regMstr.getOrgPoliceStation())).setBold().setUnderline())
                    .add(new Text(" Post office "))
                    .add(new Text(value(regMstr.getOrgPostOffice())).setBold().setUnderline())
                    .add(new Text(" District "))
                    .add(new Text(value(regMstr.getOrgDist())).setBold().setUnderline())
                    .add("\n")
                    .add(new Text("(hereinafter referred to as the licensee) on payment of fee "))
                    .add(new Text(licenseFeesDetails).setBold().setUnderline())
                    .add(new Text(
                            " to operate as to specified below subject to the provision of the West Bengal Architectural Produce Marketing (Regulation) Act, 1972 and as amended thereof and the bye-laws made under the provisions of the Act and the following conditions, namely : "))
                    .add("\n");

            Table conditionsTop = new Table(UnitValue.createPercentArray(new float[] { 8, 6, 86 }));
            conditionsTop.setWidth(UnitValue.createPercentValue(100));

            addConditionRow(conditionsTop, font, "1.",
                    "The licence takes effect from " + resolveFromDate(regMstr, dateFormat));
            addConditionRow(conditionsTop, font, "2.",
                    "The licence shall be valid upto " + resolveToDate(regMstr, dateFormat));
            addConditionRow(conditionsTop, font, "3.", "The licence is not transferable.");
            addConditionRow(conditionsTop, font, "4.",
                    "The licence is subject to the conditions set forth in the West Bengal Agricultural Produce Marketing (Regulation) Rules, 1982, particularly in rule 7 thereof.");

            String privateMarketArea = additionalDetails != null ? additionalDetails.getPrivateMarketYardArea() : "";
            addConditionRow(conditionsTop, font, "5.", "Particulars of the premises / places covered by this licence "
                    + (OrgUtil.isNeitherNullNorEmpty(privateMarketArea) ? privateMarketArea.toUpperCase()
                            : "..........................")
                    + " (One Market Area / Market Areas within West Bengal) (Strike out which is not applicable)");

            Table conditionsBottom = new Table(UnitValue.createPercentArray(new float[] { 8, 6, 86 }));
            conditionsBottom.setWidth(UnitValue.createPercentValue(100));

            addConditionRow(conditionsBottom, font, "6.",
                    "The licence shall abide by the West Bengal Agricultural Produce Marketing (Regulation) Act, 1972, and amendments thereof.");
            addConditionRow(conditionsBottom, font, "7.",
                    "The licence may be suspended or cancelled for breach of any of the provisions of the said Act and the rules made there under or for violation of or non-compliance with any of the terms and conditions of the licence set forth herein and the said Act and the rules.");
            addConditionRow(conditionsBottom, font, "8.",
                    "The licensee shall not engage the services of any assistant / agent except the following person / persons in connection with the marketing of agricultural produce, namely: -");

            Table officials = new Table(UnitValue.createPercentArray(new float[] { 8, 6, 86 }));
            officials.setWidth(UnitValue.createPercentValue(100));

            addConditionRow(officials, font, "1.",
                    formatOfficial(additionalDetails != null ? additionalDetails.getOfficial1() : null,
                            additionalDetails != null ? additionalDetails.getOfficial1Father() : null));
            addConditionRow(officials, font, "2.",
                    formatOfficial(additionalDetails != null ? additionalDetails.getOfficial2() : null,
                            additionalDetails != null ? additionalDetails.getOfficial2Father() : null));
            addConditionRow(officials, font, "3.",
                    formatOfficial(additionalDetails != null ? additionalDetails.getOfficial3() : null,
                            additionalDetails != null ? additionalDetails.getOfficial3Father() : null));

            Paragraph note = new Paragraph().setFont(font).setFontSize(12)
                    .add(new Text("(The name or names of assistants / agents engaged by licensee to be entered here)"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .add("\n\n");

            Table signTable = new Table(UnitValue.createPercentArray(new float[] { 40, 20, 40 }));
            signTable.setWidth(UnitValue.createPercentValue(100));
            signTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            signTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            signTable.addCell(createCell(
                    new Paragraph("................................\nSigned / digitally signed by\nSecretary, "
                            + value(regMstr.getOrgBaseMarket()) + " / Chief Executive Officer Board")
                            .setFont(font).setFontSize(12).setTextAlignment(TextAlignment.RIGHT),
                    1, 1, 0, false));

            signTable.addCell(createCell(new Paragraph("Place:").setFont(font).setFontSize(12), 1, 1, 0, false));
            signTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            signTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            signTable.addCell(createCell(new Paragraph("Date:").setFont(font).setFontSize(12), 1, 1, 0, false));
            signTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            signTable.addCell(
                    createCell(new Paragraph("(Seal of Market Committee / Board)").setFont(font).setFontSize(12)
                            .setTextAlignment(TextAlignment.RIGHT), 1, 1, 0, false));

            document.add(header);
            document.add(para1);
            document.add(conditionsTop);
            document.add(conditionsBottom);
            document.add(officials);
            document.add(note);
            document.add(signTable);

        } catch (IOException e) {
            log.error("Error creating Form 5 PDF", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }

        log.info("Response : {} | {}", filePath, OrgConstants.PDF_RESPONSE.SUCCESS);
        return filePath;
    }

    private void addConditionRow(Table table, PdfFont font, String number, String text) {
        Paragraph num = new Paragraph(number).setFont(font).setFontSize(12);
        table.addCell(createCell(num, 1, 1, 0, false));
        table.addCell(new Cell().setPaddingLeft(30).setBorder(Border.NO_BORDER));
        Paragraph body = new Paragraph(text).setFont(font).setFontSize(12).setMultipliedLeading(1.2f)
                .setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.MIDDLE);
        table.addCell(createCell(body, 1, 1, 0, false));
    }

    private String resolveFromDate(RegistrationMstr regMstr, SimpleDateFormat dateFormat) {
        if (OrgConstants.RENEWAL_STATUS.NOT_APPLICABLE.equals(regMstr.getOrgRenewalStatus())) {
            return formatDate(regMstr.getOrgValidFrom(), dateFormat);
        }
        return formatDate(regMstr.getRequestStartDate(), dateFormat);
    }

    private String resolveToDate(RegistrationMstr regMstr, SimpleDateFormat dateFormat) {
        if (OrgConstants.RENEWAL_STATUS.NOT_APPLICABLE.equals(regMstr.getOrgRenewalStatus())) {
            return formatDate(regMstr.getRegFeeValidity(), dateFormat);
        }
        if (OrgConstants.RENEWAL_STATUS.APPLICABLE.equals(regMstr.getOrgRenewalStatus())) {
            return formatDate(regMstr.getRequestEndDate(), dateFormat);
        }
        return formatDate(regMstr.getRegFeeValidity(), dateFormat);
    }

    private String formatOfficial(String official, String father) {
        String officialValue = OrgUtil.isNeitherNullNorEmpty(official) ? official.toUpperCase()
                : "..........................";
        String fatherValue = OrgUtil.isNeitherNullNorEmpty(father) ? father.toUpperCase()
                : "..........................";
        return "Shri " + officialValue + " son/wife/daughter of " + fatherValue;
    }

    private String formatDate(java.util.Date date, SimpleDateFormat format) {
        return date != null ? format.format(date) : "............................";
    }

    private int parseIntSafe(String value) {
        try {
            if (!OrgUtil.isNeitherNullNorEmpty(value)) {
                return 0;
            }
            return (int) Double.parseDouble(value);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String value(String text) {
        if (!OrgUtil.isNeitherNullNorEmpty(text)) {
            return "...............................";
        }
        return text.toUpperCase().replaceAll("\n", " ");
    }
}

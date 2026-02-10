package com.organisation.approvalworkflow.service.impl;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DoubleBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Text;

import com.organisation.approvalworkflow.exception.ApprovalWorkflowException;
import com.organisation.approvalworkflow.service.PDFGenerationService;
import com.organisation.approvalworkflow.util.CrunchifyQRCode;
import com.organisation.constants.OrgConstants;
import com.organisation.constants.OrgConstants.RENEWAL_STATUS;
import com.organisation.model.RegistrationMstr;
import com.organisation.repository.RegisterAdditionalDetailsRepository;
import com.organisation.repository.RegisterAdditionalDetailsTempRepository;
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;
import com.register.model.RegisterAdditionalDetailsTemp;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Slf4j
public class PDFGenerationServiceImpl implements PDFGenerationService {

    @Value("${file.unsigned.pdf.dir:./unsigned-pdf/}")
    private String pdfStoragePath;

    @Autowired
    private RegisterAdditionalDetailsTempRepository additionalDetailsTempRepo;

    private static final String FORM_FOUR_FILENAME = "Form4.pdf";
    private static final String DEFAULT_PLACEHOLDER = "............................";

    private Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph).setFontSize(12).setPadding(2);
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

    private String pw(int n, String ch) {
        String result = "";
        String one[] = { "", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
                " Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen", " Sixteen", " Seventeen", " Eighteen",
                " Nineteen" };

        String ten[] = { " ", " ", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty",
                " Ninety" };

        if (n > 19) {
            result = ten[n / 10] + "" + one[n % 10];
        } else {
            result = one[n];
        }
        if (n > 0)
            return result + ch;
        else
            return result;
    }

    private String getWords(int n) {
        return pw((n / 1000000000), " Hundred") + pw((n / 10000000) % 100, " Crore") + pw(((n / 100000) % 100), " Lakh")
                + pw(((n / 1000) % 100), " Thousand") + pw(((n / 100) % 10), " Hundred") + pw((n % 100), " ");
    }

    // Helper method to safely get string value with default
    private String safeString(String value, String defaultValue) {
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    // Helper method to safely format date
    private String safeFormatDate(Date date, SimpleDateFormat format, String defaultValue) {
        return (date != null) ? format.format(date) : defaultValue;
    }

    @Override
    public String generateFormFour(RegistrationMstr regMstr) {

        // Null check for regMstr
        if (regMstr == null) {
            throw new ApprovalWorkflowException("FORM4_ERROR", "Registration master data is null");
        }

        String orgId = safeString(regMstr.getOrgId(), "UNKNOWN");
        String basePath = pdfStoragePath + orgId;
        String filePath = basePath + "/" + FORM_FOUR_FILENAME;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        // Null check for additionalDetails
        RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo.findByOrgId(orgId);
        if (additionalDetails == null) {
            log.warn("Additional details not found for orgId: {}, creating empty object", orgId);
            additionalDetails = new RegisterAdditionalDetailsTemp();
        }

        PdfWriter writer = null;
        PdfDocument pdf = null;
        Document doc = null;

        try {
            File dir = new File(basePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            writer = new PdfWriter(filePath);
            pdf = new PdfDocument(writer);
            doc = new Document(pdf, PageSize.A4);
            doc.setMargins(30, 40, 30, 40); // Top, Right, Bottom, Left margins

            CrunchifyQRCode QRCode = new CrunchifyQRCode();
            String filePath2 = QRCode.generateQRCode(regMstr);

            File qrFile = new File(filePath2);
            if (!qrFile.exists() || qrFile.length() == 0) {
                throw new RuntimeException("QR image invalid: " + filePath2);
            }

            Image qrImage = new Image(ImageDataFactory.create(filePath2));
            qrImage.setWidth(80);
            qrImage.setHeight(80);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header Table with QR Code
            Table headerTable = new Table(new float[] { 1, 3, 1 });
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Empty left cell
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("")));

            // Center header text
            Paragraph headerPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);
            headerPara.add(new Text("FORM 4").setBold()).add("\n");
            headerPara.add(new Text("[See rule 6(1)]").setFontSize(11)).add("\n");

            String category = safeString(this.decodeCategory(regMstr.getOrgCategory()), "Trader");
            headerPara.add(new Text("Licence for purposes to carry on business or act as a " + category)
                    .setBold()
                    .setFontSize(12));

            headerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(headerPara)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            // Right QR Code
            headerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(qrImage)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            doc.add(headerTable);
            doc.add(new Paragraph("\n").setFontSize(6));

            // License Fee Details
            String licenseFeesDetails = "";
            String regFees = "1000";
            String regRenewalFees = "1000";

            boolean isLicenseExists = regMstr.isLicenseExists();
            String renewalStatus = safeString(regMstr.getOrgRenewalStatus(), RENEWAL_STATUS.NOT_APPLICABLE);

            if (isLicenseExists) {
                licenseFeesDetails = "AS PER MANUAL RECORDS";
            } else {
                licenseFeesDetails = "Rs." + regFees + " (Rupees" + getWords((int) Double.parseDouble(regFees)) + ")";
            }

            if (renewalStatus.equals(OrgConstants.RENEWAL_STATUS.APPLICABLE)) {
                licenseFeesDetails = "Rs." + regRenewalFees + " (Rupees"
                        + getWords((int) Double.parseDouble(regRenewalFees)) + ")";
            }

            // Main narration paragraph
            Paragraph mainPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.JUSTIFIED)
                    .setMultipliedLeading(1.3f);

            // Market name with null check
            String marketName = DEFAULT_PLACEHOLDER;
            if (regMstr.isUnifiedLicense()) {
                marketName = "West Bengal State Agricultural Marketing Board";
            } else if (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())) {
                marketName = regMstr.getOrgBaseMarket().toUpperCase();
            }

            mainPara.add(new Text("The "))
                    .add(new Text(marketName).setBold().setUnderline())
                    .add(" ");

            mainPara.add("Licence No ")
                    .add(new Text(safeString(regMstr.getOrgId(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("Licence is hereby granted to ")
                    .add(new Text(safeString(regMstr.getOrgName(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("of ")
                    .add(new Text(safeString(regMstr.getOrgAddress(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("Police Station ")
                    .add(new Text(safeString(regMstr.getOrgPoliceStation(), DEFAULT_PLACEHOLDER).toUpperCase())
                            .setBold().setUnderline())
                    .add(" ");

            mainPara.add("Post office ")
                    .add(new Text(safeString(regMstr.getOrgPostOffice(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("District ")
                    .add(new Text(safeString(regMstr.getOrgDist(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("Block Name ")
                    .add(new Text(safeString(regMstr.getOrgBlockName(), DEFAULT_PLACEHOLDER).toUpperCase()).setBold()
                            .setUnderline())
                    .add(" ");

            mainPara.add("(hereinafter referred to as the licensee) on payment of fee ")
                    .add(new Text(licenseFeesDetails).setBold().setUnderline())
                    .add(" ");

            mainPara.add(
                    "to operate as to specified below subject to the provision of the West Bengal Agricultural Produce ")
                    .add("Marketing (Regulation) Act, 1972 and as amended thereof and the bye-laws made under the provisions of the Act and");

            doc.add(mainPara);
            doc.add(new Paragraph("\n").setFontSize(4));

            /* Conditions Table */
            Table conditionsTable = new Table(new float[] { 0.5f, 9.5f });
            conditionsTable.setWidth(UnitValue.createPercentValue(100));

            // Condition 1
            String value = DEFAULT_PLACEHOLDER;
            if (renewalStatus.equals(RENEWAL_STATUS.NOT_APPLICABLE)) {
                value = safeFormatDate(regMstr.getOrgValidFrom(), dateFormat, DEFAULT_PLACEHOLDER);
            } else {
                value = safeFormatDate(regMstr.getRequestStartDate(), dateFormat, DEFAULT_PLACEHOLDER);
            }

            addConditionRow(conditionsTable, font, "1.",
                    "The licence takes effect from " + value + ".");

            // Condition 2
            String value2 = DEFAULT_PLACEHOLDER;
            if (renewalStatus.equals(RENEWAL_STATUS.NOT_APPLICABLE)) {
                value2 = safeFormatDate(regMstr.getRegFeeValidity(), dateFormat, DEFAULT_PLACEHOLDER);
            } else if (renewalStatus.equals(RENEWAL_STATUS.APPLICABLE)) {
                value2 = safeFormatDate(regMstr.getRequestEndDate(), dateFormat, DEFAULT_PLACEHOLDER);
            } else {
                value2 = safeFormatDate(regMstr.getRegFeeValidity(), dateFormat, DEFAULT_PLACEHOLDER);
            }

            addConditionRow(conditionsTable, font, "2.",
                    "The licence shall be valid upto " + value2 + ".");

            // Condition 3
            addConditionRow(conditionsTable, font, "3.",
                    "The licence is not transferable.");

            // Condition 4
            addConditionRow(conditionsTable, font, "4.",
                    "The licence is subject to the conditions set forth in the West Bengal Agricultural Produce Marketing (Regulation) Rules, 1982, particularly in rule 7 thereof.");

            // Condition 5
            String premisesInfo = "The State of West Bengal";
            if (!regMstr.isUnifiedLicense()) {
                premisesInfo = safeString(regMstr.getOrgBaseMarket(), DEFAULT_PLACEHOLDER).toUpperCase();
            }

            String marketAreaText = !regMstr.isUnifiedLicense()
                    ? " REGULATED MARKET COMMITTEE One Market Area within West Bengal."
                    : " Market Areas within West Bengal.";

            addConditionRow(conditionsTable, font, "5.",
                    "Particulars of the premises / place covered by this licence " +
                            premisesInfo + marketAreaText);

            // Condition 6
            addConditionRow(conditionsTable, font, "6.",
                    "The license shall abide by the West Bengal Agricultural Produce Marketing (Regulation) Act 1972 and as amended thereof");

            // Condition 7
            addConditionRow(conditionsTable, font, "7.",
                    "The licence may be suspended or cancelled for breach of any of the provisions of the said Act and the rules made thereunder or for violation of or non-compliance with any of the terms and conditions of the licence of set forth herein and the said Act and the rules.");

            // Condition 8
            addConditionRow(conditionsTable, font, "8.",
                    "The licensee shall not engage the services of any assistant / agent except the following person / persons in connection with the marketing of agricultural produce, namely: -");

            doc.add(conditionsTable);
            doc.add(new Paragraph("\n").setFontSize(4));

            /* Assistants/Agents Table */
            Table assistantsTable = new Table(new float[] { 0.5f, 9.5f });
            assistantsTable.setWidth(UnitValue.createPercentValue(100));

            // Official 1
            String official1 = safeString(additionalDetails.getOfficial1(), DEFAULT_PLACEHOLDER);
            String father1 = safeString(additionalDetails.getOfficial1Father(), DEFAULT_PLACEHOLDER);

            String official1Text = "Shri/Smt " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial1()) ? official1.toUpperCase()
                            : official1)
                    +
                    " son/wife/daughter of " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial1Father()) ? father1.toUpperCase()
                            : father1);

            addConditionRow(assistantsTable, font, "1.", official1Text);

            // Official 2
            String official2 = safeString(additionalDetails.getOfficial2(), DEFAULT_PLACEHOLDER);
            String father2 = safeString(additionalDetails.getOfficial2Father(), DEFAULT_PLACEHOLDER);

            String official2Text = "Shri/Smt " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial2()) ? official2.toUpperCase()
                            : official2)
                    +
                    " son/wife/daughter of " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial2Father()) ? father2.toUpperCase()
                            : father2);

            addConditionRow(assistantsTable, font, "2.", official2Text);

            // Official 3
            String official3 = safeString(additionalDetails.getOfficial3(), DEFAULT_PLACEHOLDER);
            String father3 = safeString(additionalDetails.getOfficial3Father(), DEFAULT_PLACEHOLDER);

            String official3Text = "Shri/Smt " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial3()) ? official3.toUpperCase()
                            : official3)
                    +
                    " son/wife/daughter of " +
                    (OrgUtil.isNeitherNullNorEmpty(additionalDetails.getOfficial3Father()) ? father3.toUpperCase()
                            : father3);

            addConditionRow(assistantsTable, font, "3.", official3Text);

            doc.add(assistantsTable);
            doc.add(new Paragraph("\n").setFontSize(6));

            // Footer note
            Paragraph footerPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .add("(The name or names of assistants / agents engaged by licensee to be entered here)");

            doc.add(footerPara);

            doc.close();
            pdf.close();
            writer.close();

            // After closing the document, verify the PDF
            File pdfFile = new File(filePath);
            if (!pdfFile.exists() || pdfFile.length() == 0) {
                throw new RuntimeException("Generated PDF is invalid - file does not exist or is empty");
            }

            // Optional: Try to open it with PdfReader to verify integrity
            try (PdfReader reader = new PdfReader(filePath);
                    PdfDocument testPdf = new PdfDocument(reader)) {
                if (testPdf.getNumberOfPages() < 1) {
                    throw new RuntimeException("PDF has no pages");
                }
                log.info("PDF validation successful - {} pages generated", testPdf.getNumberOfPages());
            } catch (Exception e) {
                log.error("PDF validation failed", e);
                throw new RuntimeException("Generated PDF is corrupted: " + e.getMessage(), e);
            }

            log.info("Form-4 generated successfully : {}", filePath);
            return filePath;

        } catch (Exception e) {
            log.error("Form-4 generation failed", e);
            // Clean up resources manually on error
            try {
                if (doc != null)
                    doc.close();
                if (pdf != null)
                    pdf.close();
                if (writer != null)
                    writer.close();
            } catch (Exception closeEx) {
                log.error("Error closing PDF resources", closeEx);
            }

            throw new ApprovalWorkflowException("FORM4_ERROR", "Form-4 generation failed: " + e.getMessage());
        }
    }

    /**
     * Helper method to add a condition row to the table
     */
    private void addConditionRow(Table table, PdfFont font, String number, String text) {
        // Number cell
        Paragraph numPara = new Paragraph(number)
                .setFont(font)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT);

        Cell numCell = new Cell()
                .add(numPara)
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(30)
                .setVerticalAlignment(VerticalAlignment.TOP);

        // Text cell
        Paragraph textPara = new Paragraph(text)
                .setFont(font)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setMultipliedLeading(1.3f);

        Cell textCell = new Cell()
                .add(textPara)
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5)
                .setVerticalAlignment(VerticalAlignment.TOP);

        table.addCell(numCell);
        table.addCell(textCell);
    }

    private String decodeCategory(String category) {
        if (category == null)
            return "Trader"; // Default value instead of empty string
        switch (category) {
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRADER:
                return "Trader";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCESSOR:
                return "Processor";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SURVEYOR:
                return "Surveyor";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WEIGHMAN:
                return "Weighman";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MEASURER:
                return "Measurer";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_WAREHOUSEMAN:
                return "Warehouseman";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_PROCURER_PRESERVER:
                return "Procurer Preserver";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_SELLER_PURCHASER:
                return "Seller Purchaser";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_TRANSPOTER:
                return "Transporter";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_STORAGE:
                return "Storage";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_BAZAR:
                return "Bazar";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MELA:
                return "Mela";
            case OrgConstants.ORG_CATEGORY.ORG_CATEGORY_MILLER:
                return "Miller";
            default:
                return category;
        }
    }

    private Image generateQRCodeImage(RegistrationMstr regMstr) throws Exception {
        if (regMstr == null) {
            throw new IllegalArgumentException("Registration master cannot be null for QR code generation");
        }

        CrunchifyQRCode qr = new CrunchifyQRCode();
        String qrPath = qr.generateQRCode(regMstr);

        if (qrPath == null || qrPath.trim().isEmpty()) {
            throw new RuntimeException("QR code path is null or empty");
        }

        File qrFile = new File(qrPath);
        if (!qrFile.exists() || qrFile.length() == 0) {
            throw new RuntimeException("Invalid QR file: " + qrPath);
        }

        Image qrImage = new Image(ImageDataFactory.create(qrPath));
        qrImage.setWidth(80);
        qrImage.setHeight(80);

        return qrImage;
    }
}
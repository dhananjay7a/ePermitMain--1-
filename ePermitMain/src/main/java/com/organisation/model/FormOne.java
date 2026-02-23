package com.organisation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.organisation.repository.UserMstrRepository;
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;

@Service("form1Service")
public class FormOne {

        @Value("${file.formOne.pdf.dir:./formOne-pdf/}")
        private String pdfStoragePath;

        private static final Logger log = LoggerFactory.getLogger(FormOne.class);

        private static final String FORM_ONE_FOLDER_NAME = "form1";
        private static final String FORM_ONE_FILENAME = "Form1.pdf";

        // FIX: Consistent font size constants — 14pt was too large for a dense form
        private static final float BODY_FONT_SIZE = 11f;
        private static final float HEADING_FONT_SIZE = 13f;

        @Autowired
        CommonService commonService;

        @Autowired
        UserMstrRepository uMstrRepository;

        /**
         * FIX: createCell now has proper padding so rows don't crush each other.
         * The old version used setPadding(0) everywhere which caused congestion.
         * isBorder=false cells get 3pt top/bottom padding for breathing room.
         * isBorder=true cells keep the double border and 4pt padding.
         */
        public Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
                Cell cell = new Cell(rowspan, colspan).add(paragraph)
                                .setFontSize(BODY_FONT_SIZE)
                                // FIX: was 2, now 4 — gives vertical breathing room between rows
                                .setPaddingTop(4)
                                .setPaddingBottom(4)
                                .setPaddingLeft(4)
                                .setPaddingRight(4);

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

        /**
         * Helper: create a labelled paragraph (e.g. "1.(a)", "(b)", "5.") for the
         * left column of the main table.
         */
        private Paragraph labelPara(PdfFont font, String label, float leftPad) {
                // FIX: multipliedLeading=1.3 instead of 0 or 1 — prevents overlap
                return new Paragraph()
                                .setFont(font)
                                .setFontSize(BODY_FONT_SIZE)
                                .add(new Text(label))
                                .setVerticalAlignment(VerticalAlignment.TOP)
                                .setTextAlignment(TextAlignment.LEFT)
                                .setMultipliedLeading(1.3f)
                                .setPaddingLeft(leftPad);
        }

        /**
         * Helper: create a content paragraph for the right column of the main table.
         * FIX: multipliedLeading=1.3 everywhere (was 0 or 1, causing line overlap).
         */
        private Paragraph contentPara(PdfFont font) {
                return new Paragraph()
                                .setFont(font)
                                .setFontSize(BODY_FONT_SIZE)
                                .setVerticalAlignment(VerticalAlignment.TOP)
                                .setTextAlignment(TextAlignment.LEFT)
                                .setMultipliedLeading(1.3f); // FIX: was 0/1 — now proper line height
        }

        public String createPdf(RegistrationMstr regMstr) {
                Document document = null;
                PdfDocument pdf = null;
                File file = null;
                String filePath = null;

                try {
                        UserMstr user = uMstrRepository.findByOrgId(regMstr.getOrgId());
                        log.info("User Details for Form1 PDF Generation : " + user);
                        log.info("Inside Form1->createPdf()");

                        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

                        String orgId = regMstr.getOrgId().toUpperCase();
                        String folderPath = pdfStoragePath + orgId;

                        log.info("Folder Path : " + folderPath);

                        File directory = new File(folderPath);
                        if (!directory.exists()) {
                                directory.mkdirs();
                        }
                        if (OrgUtil.isNeitherNullNorEmpty(regMstr.getIsProfileUpdated()) &&
                                        regMstr.getIsProfileUpdated().equals(OrgConstants.YES)) {
                                File[] files = directory.listFiles();
                                if (files != null) {
                                        for (File form1File : files) {
                                                form1File.delete();
                                        }
                                }
                        }

                        filePath = folderPath + File.separator + "formOne.pdf";
                        log.info("Final PDF Path : {}", filePath);

                        file = new File(filePath);

                        if (file.exists()) {
                                FileInputStream fi = null;
                                try {
                                        fi = new FileInputStream(new File(filePath));
                                        PdfReader pdfreader = new PdfReader(fi);
                                        if (pdfreader != null) {
                                                PdfDocument pdfDoc = new PdfDocument(pdfreader);
                                                log.info("No of Pages in " + file.getName() + " "
                                                                + pdfDoc.getNumberOfPages());
                                                pdfDoc.close();
                                                pdfreader.close();
                                                fi.close();
                                                return filePath;
                                        }
                                } catch (Exception e) {
                                        if (fi != null)
                                                fi.close();
                                        log.info("File Corrupt ::" + filePath);
                                        FileUtils.forceDelete(file);
                                        if (file.exists()) {
                                                log.info("Response : " + OrgConstants.PDF_RESPONSE.ERROR);
                                                return filePath;
                                        } else {
                                                log.info("Response : " + OrgConstants.PDF_RESPONSE.ERROR);
                                                log.info("File Corrupt Deleted ::" + filePath);
                                        }
                                }
                        } else {
                                log.info("New File Created ::" + filePath);
                        }

                        pdf = new PdfDocument(new PdfWriter(filePath));
                        // FIX: Add proper margins to the document so content doesn't run to the edge
                        document = new Document(pdf, PageSize.A4);
                        document.setMargins(36, 36, 36, 36); // 36pt ≈ 0.5 inch on all sides

                        String licenseFeesDetails = "";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                        String regFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION,
                                        regMstr.getOrgCategory());
                        try {
                                if (regMstr.isLicenseExists()) {
                                        licenseFeesDetails = "AS PER MANUAL RECORDS";
                                } else {
                                        licenseFeesDetails = "Rs. " + regFees + " | Receipt No. "
                                                        + regMstr.getRegReceiptNo()
                                                        + " | Date : "
                                                        + dateFormat.format(OrgUtil.isNeitherNullNorEmpty(
                                                                        regMstr.getRegistrationSubmitDate())
                                                                                        ? regMstr.getRegistrationSubmitDate()
                                                                                        : new Date());
                                }
                        } catch (Exception e) {
                                log.info("Exception :- " + e.getMessage());
                                e.printStackTrace();
                        }

                        // ── TITLE BLOCK ──────────────────────────────────────────────────────────────
                        // FIX: multipliedLeading=1.4 for title; was mixing 0 and 1 causing overlap
                        Paragraph para0 = new Paragraph()
                                        .setFont(fontBold)
                                        .setFontSize(HEADING_FONT_SIZE)
                                        .add(new Text("FORM 1"))
                                        .add("\n\n")
                                        .add(new Text("[See rule 3]"))
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMultipliedLeading(1.4f) // FIX: was 1
                                        .setMarginBottom(8);

                        // ── APPLICATION HEADER PARAGRAPH ─────────────────────────────────────────────
                        Paragraph para = new Paragraph()
                                        .setFont(fontBold)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text(
                                                        "Application for licence under section 13(2) of the West Bengal Agricultural Produce Marketing (Regulation) Act, "
                                                                        + "1972 and as amended thereof, for purpose to carry on business or act as a trader, buyer, exporter, importer, engaged "
                                                                        + "in e-trading, broker, commission agent , market functionary, weigh man, warehouse man, surveyor or sell or purchase "
                                                                        + "of agricultural produce or engaged in processing or preservation of agricultural produce (Application to the Market "
                                                                        + "Committee for license within a market area or Application to the Board for single license in more than one market areas)"))
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setMultipliedLeading(1.3f) // FIX: was 1
                                        .setMarginBottom(10);

                        // ── ADDRESSEE
                        // ─────────────────────────────────────────────────────────────────
                        Paragraph para1 = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("To\n"))
                                        .add(new Text("The ")
                                                        .setFont(font))
                                        .add(new Text(!regMstr.isUnifiedLicense()
                                                        ? (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())
                                                                        ? regMstr.getOrgBaseMarket().toUpperCase()
                                                                                        + " REGULATED MARKET COMMITTEE"
                                                                        : "...............................")
                                                        : "Chief Executive Officer, West Bengal State Agricultural Marketing Board")
                                                        .setBold()
                                                        .setUnderline())
                                        .add("\n")
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setMultipliedLeading(1.4f) // FIX: was 1
                                        .setMarginBottom(6);

                        // ── SIR + OPENING BODY
                        // ────────────────────────────────────────────────────────
                        // FIX: Previously these were 3 separate paragraphs with leading=0 causing them
                        // to overlap. Merged into one paragraph with proper spacing.
                        Paragraph para2 = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Sir,\n"))
                                        .add(new Text(
                                                        "I beg to apply for a licence under section 13(2) of the West Bengal Agricultural Produce "
                                                                        + "Marketing (Regulation) Act, 1972 and as amended thereof. Necessary particulars are given below:"))
                                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setMultipliedLeading(1.4f) // FIX: was 0
                                        .setMarginBottom(10);

                        // ── MAIN DETAILS TABLE
                        // ────────────────────────────────────────────────────────
                        // FIX: Use percent widths so table fills the page correctly.
                        // Old code used setMarginLeft(55) without a width, making it narrow.
                        Table table = new Table(UnitValue.createPercentArray(new float[] { 12, 88 }));
                        table.setWidth(UnitValue.createPercentValue(100));
                        table.setMarginBottom(10);

                        // ── Row 1.(a): Name
                        // ───────────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "1.(a)", 0), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Name of the applicant for which licence applied for - "))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgName())
                                                                ? regMstr.getOrgName().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 1.(b): Individual/Proprietorship
                        // ──────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(b)", 8), 1, 1, 0, false));
                        {
                                String INDSOP = (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgType())
                                                && (regMstr.getOrgType().equals("SOP")
                                                                || regMstr.getOrgType().equals("IND")))
                                                                                ? "Yes"
                                                                                : "No";
                                Paragraph p = contentPara(font)
                                                .add(new Text("Whether applicant is an individual/proprietorship or not - "))
                                                .add(new Text(INDSOP).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 1.(c): Business Address
                        // ───────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(c)", 8), 1, 1, 0, false));
                        {
                                String fullAddress = safe(regMstr.getOrgAddress()) + ", "
                                                + safe(regMstr.getOrgPoliceStation()) + ", "
                                                + safe(regMstr.getOrgPostOffice()) + ", "
                                                + safe(regMstr.getOrgDist()) + ", "
                                                + safe(regMstr.getOrgBlockName()) + ", "
                                                + safe(regMstr.getOrgPin());
                                Paragraph p = contentPara(font)
                                                .add(new Text("Business Address in full -\n"))
                                                .add(new Text(fullAddress.toUpperCase().replaceAll("\n", " "))
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 1.(d): Contact
                        // ────────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(d)", 8), 1, 1, 0, false));
                        {
                                String contact = documentType("AD") + "  "
                                                + (OrgUtil.isNeitherNullNorEmpty(user.getUserMobile())
                                                                ? user.getUserMobile()
                                                                : " ")
                                                + "  "
                                                + (OrgUtil.isNeitherNullNorEmpty(user.getUserEmail())
                                                                ? user.getUserEmail()
                                                                : " ");
                                Paragraph p = contentPara(font)
                                                .add(new Text("Contact No and e-Mail Id -\n"))
                                                .add(new Text(contact).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 2.(a): Constitution
                        // ───────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "2.(a)", 0), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Constitution of farm/company/society - "))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgType())
                                                                ? getConstitution(regMstr.getOrgType())
                                                                : "NA")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 2.(b): Registered with
                        // ────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(b)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("If registered with whom? - "))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getRegisteredWith())
                                                                ? regMstr.getRegisteredWith().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 2.(c): Particulars of registration
                        // ────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(c)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Particulars of registration - "))
                                                .add(new Text(OrgUtil
                                                                .isNeitherNullNorEmpty(regMstr.getRegistrationDetails())
                                                                                ? regMstr.getRegistrationDetails()
                                                                                                .toUpperCase()
                                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 2.(d): Managing Director
                        // ──────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(d)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Name of the Managing Director or Manager or Principal Officer who will actually conduct the business - "))
                                                .add(new Text(OrgUtil
                                                                .isNeitherNullNorEmpty(regMstr.getOrgContactPerson())
                                                                                ? regMstr.getOrgContactPerson()
                                                                                                .toUpperCase()
                                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 3.(a): Business particulars
                        // ───────────────────────────────────────────
                        // Normalise category code first
                        String orgCat = normaliseCategory(regMstr.getOrgCategory());

                        table.addCell(createCell(labelPara(font, "3.(a)", 0), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text(
                                                                "Particulars of the business for which the licence is required "
                                                                                + "(Broker / Commission Agent / Seller or purchaser of agricultural produce / "
                                                                                + "Warehouseman / Trader / Measurer / Surveyor / person engaged in procuring "
                                                                                + "and preservation of agricultural produce / Importer / Exporter) - "))
                                                .add(new Text(orgCat.toUpperCase()).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 3.(b): Market area
                        // ────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(b)", 8), 1, 1, 0, false));
                        {
                                String marketArea = regMstr.isUnifiedLicense() ? "West Bengal"
                                                : regMstr.getOrgBaseMarket();
                                Paragraph p = contentPara(font)
                                                .add(new Text("Situation of the applicant's place of business with particulars as to the Market Area / areas - "))
                                                .add(new Text(safe(marketArea)).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 3.(c): Market year
                        // ────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(c)", 8), 1, 1, 0, false));
                        {
                                String marketYear = "";
                                if (OrgUtil.isNeitherNullNorEmpty(regMstr.getMarketYearApp())) {
                                        String[] arr = regMstr.getMarketYearApp().split("-");
                                        marketYear = "20" + arr[0] + "-20" + arr[1];
                                }
                                Paragraph p = contentPara(font)
                                                .add(new Text("Market year or part thereof for which licence is applied for - "))
                                                .add(new Text(marketYear).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 3.(d): Agricultural produce ──────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(d)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("The name / names of the agricultural produce he intends to deal in - "))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getCommodityCode())
                                                                ? regMstr.getCommodityCode().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(a): Bank account
                        // ───────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "4.(a)", 0), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Has the applicant any bank account? If so, bank details should be furnished. -\n"))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBankName())
                                                                ? regMstr.getOrgBankName().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(b): Experience
                        // ─────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(b)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Whether the applicant has any experience in the business of the nature for which the licence is applied for. If so, the particulars should be furnished -\n"))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getPrevExp())
                                                                ? regMstr.getPrevExp().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(c): Previous licence
                        // ───────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(c)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Whether the applicant has any licence for any kind of market from this Market Committee. If so, full particular should be stated. -\n"))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getPrevLicense())
                                                                ? regMstr.getPrevLicense().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(d): Previous market licence ───────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(d)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Did the applicant hold a licence from the Market Committee on any previous occasion? If so, give particulars including its suspension or cancellation, if any -\n"))
                                                .add(new Text(OrgUtil
                                                                .isNeitherNullNorEmpty(regMstr.getPrevmarketLicense())
                                                                                ? regMstr.getPrevmarketLicense()
                                                                                                .toUpperCase()
                                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(e): Godown details
                        // ─────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(e)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Details godowns and other storage premises-particulars, capacity and location -\n"))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getGodownDetails())
                                                                ? regMstr.getGodownDetails().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(f): Employees
                        // ──────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(f)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Details of employees / assistant / agents - "))
                                                .add(new Text(OrgUtil
                                                                .isNeitherNullNorEmpty(regMstr.getEmployeeDetails())
                                                                                ? regMstr.getEmployeeDetails()
                                                                                                .toUpperCase()
                                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(g): Import/Export
                        // ──────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(g)", 8), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("If importer or exporter, particulars about the commodities he is dealing in their volume and usual export destination and usual place of import. -\n"))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getImpExp())
                                                                ? regMstr.getImpExp()
                                                                : "").setUnderline().setBold())
                                                .add(new Text(OrgUtil
                                                                .isNeitherNullNorEmpty(regMstr.getImpExpCommodity())
                                                                                ? " - " + regMstr.getImpExpCommodity()
                                                                                                .toUpperCase()
                                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 4.(h): Bank guarantee
                        // ─────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "(h)", 8), 1, 1, 0, false));
                        {
                                String isBGReady = "";
                                if (OrgUtil.isNeitherNullNorEmpty(regMstr.getIsBGReady())) {
                                        isBGReady = regMstr.getIsBGReady().equals("Y") ? "YES" : "NO";
                                }
                                Paragraph p = contentPara(font)
                                                .add(new Text("Whether the applicant is prepared to furnish cash security or bank guarantee as may be required by the market committee - "))
                                                .add(new Text(isBGReady.toUpperCase()).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 5: Misconduct
                        // ─────────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "5.", 10), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Has the applicant ever been found guilty of professional misconduct? If so, the details should be furnished. - "))
                                                .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getGuiltyDoc())
                                                                ? regMstr.getGuiltyDoc().toUpperCase()
                                                                : "")
                                                                .setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── Row 6: Licence fee
                        // ────────────────────────────────────────────────────────
                        table.addCell(createCell(labelPara(font, "6.", 10), 1, 1, 0, false));
                        {
                                Paragraph p = contentPara(font)
                                                .add(new Text("Amount of licence fee deposited with particulars of receipt number and date. - "))
                                                .add(new Text(licenseFeesDetails).setUnderline().setBold());
                                table.addCell(createCell(p, 1, 1, 0, false));
                        }

                        // ── DECLARATION
                        // ───────────────────────────────────────────────────────────────
                        // FIX: Declaration is now a single well-spaced paragraph block, not 4 separate
                        // paragraphs with leading=0 that were overlapping each other.
                        Paragraph declHeading = new Paragraph()
                                        .setFont(fontBold)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Declaration"))
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMultipliedLeading(1.4f)
                                        .setMarginTop(12)
                                        .setMarginBottom(6);

                        Paragraph declBody = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text(
                                                        "I certify that the facts stated above are true to the best of my knowledge.\n\n"
                                                                        + "I hereby undertake to abide by the conditions of licence, the provisions of the West Bengal Agricultural "
                                                                        + "Produce Marketing (Regulation) Act, 1972 and the rules framed there under and the by laws made there under "
                                                                        + "by the Regulated Market Committee.\n\n"
                                                                        + "I shall be responsible for all acts, omissions and commissions of my employees, if it so happens."))
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setMultipliedLeading(1.4f) // FIX: was split across 4 paragraphs with leading=0
                                        .setMarginBottom(16);

                        // ── SIGNATURE TABLE
                        // ───────────────────────────────────────────────────────────
                        // FIX: Use percent widths so columns are balanced and readable.
                        // Old table1 was incomplete (only 1 empty cell added then abandoned).
                        Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 45, 10, 45 }));
                        signatureTable.setWidth(UnitValue.createPercentValue(100));

                        Paragraph addrPara = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Full address of the applicant\n"))
                                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgAddress())
                                                        ? regMstr.getOrgAddress().toUpperCase().replaceAll("\n", " ")
                                                        : " ")
                                                        .setBold())
                                        .setMultipliedLeading(1.3f);

                        signatureTable.addCell(createCell(addrPara, 1, 1, 0, false));
                        signatureTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

                        Paragraph sigPara = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Signature (in full) of the applicant\n\n\n")) // extra newlines
                                                                                                     // for signing
                                                                                                     // space
                                        .setMultipliedLeading(1.3f);
                        signatureTable.addCell(createCell(sigPara, 1, 1, 0, false));

                        Paragraph placePara = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Place: ___________________"))
                                        .setMultipliedLeading(1.3f);
                        signatureTable.addCell(createCell(placePara, 1, 1, 0, false));
                        signatureTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

                        Paragraph datePara = new Paragraph()
                                        .setFont(font)
                                        .setFontSize(BODY_FONT_SIZE)
                                        .add(new Text("Date: ___________________"))
                                        .setMultipliedLeading(1.3f);
                        signatureTable.addCell(createCell(datePara, 1, 1, 0, false));

                        // ── ADD ALL ELEMENTS TO DOCUMENT
                        // ──────────────────────────────────────────────
                        document.add(para0);
                        document.add(para);
                        document.add(para1);
                        document.add(para2);
                        // FIX: Removed para3 and para4 (merged into para2 above)
                        document.add(table);
                        // FIX: Removed stray document.add(para5) — old code added last para5 (empty)
                        document.add(declHeading);
                        document.add(declBody);
                        // FIX: Removed orphaned table1 + para11 + para14; replaced with signatureTable
                        document.add(signatureTable);

                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        if (document != null) {
                                document.close();
                        }
                }
                log.info("Response : " + filePath + " | " + OrgConstants.PDF_RESPONSE.SUCCESS);
                return filePath;
        }

        // ── HELPERS
        // ───────────────────────────────────────────────────────────────────

        /** Null-safe string getter */
        private String safe(String s) {
                return s != null ? s : "";
        }

        /** Normalise org category code to display label */
        private String normaliseCategory(String cat) {
                if (!OrgUtil.isNeitherNullNorEmpty(cat))
                        return "";
                switch (cat.trim()) {
                        case "TRD":
                                return "TRADER";
                        case "PRS":
                                return "PROCESSOR";
                        case "BKR":
                                return "BROKER";
                        case "SVR":
                                return "SURVEYOR";
                        case "WEI":
                                return "WEIGHMAN";
                        case "MSR":
                                return "MEASURER";
                        case "WHM":
                                return "WAREHOUSEMAN";
                        case "PRV":
                                return "PROCURER OR PRESERVER OF AGRICULTURAL PRODUCE";
                        case "SLR":
                                return "SELLER OR PURCHASER OF AGRICULTURAL PRODUCE";
                        case "COM":
                                return "COMMISSION AGENT";
                        case "TRS":
                                return "TRANSPORTER";
                        default:
                                return cat;
                }
        }

        public void deletePdf(RegistrationMstr regMstr) {
                File file = null;
                String filePath = null;
                try {
                        log.info("Inside Form1->deletePdf()");
                        String orgId = regMstr.getOrgId().toUpperCase();
                        String basePath = pdfStoragePath + orgId + "/" + FORM_ONE_FOLDER_NAME;
                        filePath = basePath + "/" + FORM_ONE_FILENAME;
                        log.info("File Path : " + filePath);
                        file = new File(filePath);
                        if (file.exists()) {
                                log.info("Deleting file: " + filePath);
                                file.delete();
                        }
                } catch (Exception e) {
                        log.info("Error deleting file: " + e.getMessage());
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
                                return "REG PARTNERSHIP DEED"; // FIX: old code was missing break here — fell through to
                                                               // PN
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
                        case "SOCIETY":
                                return "SOCIETY";
                        default:
                                return "NA";
                }
        }
}
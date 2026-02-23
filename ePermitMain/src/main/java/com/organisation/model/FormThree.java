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
import com.organisation.repository.RegisterAdditionalDetailsTempRepository;
import com.organisation.repository.UserMstrRepository;
import com.organisation.service.CommonService;
import com.organisation.util.OrgUtil;
import com.register.model.RegisterAdditionalDetailsTemp;

@Service("form3Service")
public class FormThree {

    @Value("${file.formThree.pdf.dir:./formThree-pdf/}")
    private String pdfStoragePath;

    private static final String FORM_THREE_FOLDER_NAME = "form3";
    private static final String FORM_THREE_FILENAME = "Form3.pdf";

    // FIX: instance font field (was static, which can cause concurrency issues)
    private PdfFont font = null;
    private PdfFont fontBold = null;

    private static final Logger log = LoggerFactory.getLogger(FormThree.class);

    // FIX: Consistent font size constants — 14pt was too large for a dense form
    private static final float BODY_FONT_SIZE = 11f;
    private static final float HEADING_FONT_SIZE = 13f;

    @Autowired
    CommonService commonService;

    @Autowired
    UserMstrRepository uMstrRepository;

    @Autowired
    private RegisterAdditionalDetailsTempRepository additionalDetailsTempRepo;

    /**
     * FIX: createCell now has proper padding so rows don't crush each other.
     * The old version used setPadding(0) everywhere — now 4pt top/bottom.
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

    /** Helper: label paragraph for left column (e.g. "(i)(a)", "(b)", "(ii)") */
    private Paragraph labelPara(String label, float leftPad) {
        return new Paragraph()
                .setFont(font)
                .setFontSize(BODY_FONT_SIZE)
                .add(new Text(label))
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(1.3f) // FIX: was 0/1 — prevents line overlap
                .setPaddingLeft(leftPad);
    }

    /** Helper: content paragraph for right column */
    private Paragraph contentPara() {
        return new Paragraph()
                .setFont(font)
                .setFontSize(BODY_FONT_SIZE)
                .setVerticalAlignment(VerticalAlignment.TOP)
                .setTextAlignment(TextAlignment.LEFT)
                .setMultipliedLeading(1.3f); // FIX: was 0/1
    }

    public String createPdf(RegistrationMstr regMstr) {
        Document document = null;
        PdfDocument pdf = null;
        File file = null;
        String filePath = null;

        try {
            log.info("Inside Form3->createPdf()");
            UserMstr user = uMstrRepository.findByOrgId(regMstr.getOrgId());
            log.info("Fetched UserMstr for OrgId {}: {}", regMstr.getOrgId(), user);

            String orgId = regMstr.getOrgId().toUpperCase();
            RegisterAdditionalDetailsTemp additionalDetails = additionalDetailsTempRepo.findByOrgId(orgId);
            log.info("Fetched RegisterAdditionalDetailsTemp for OrgId {}: {}", orgId, additionalDetails);

            // LicenseKey.loadLicenseFile(ContextData.getResourceBundle("LICENSE_PATH"));

            this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            this.fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yy");

            String folderPath = pdfStoragePath + orgId;

            log.info("Folder Path : " + folderPath);

            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String currYear = dateFormat1.format(regMstr.getRegFeeValidity());
            int year = Integer.parseInt(currYear);
            int prevYear = year - 1;
            String regFeeValidityYear = String.valueOf(prevYear) + "-" + String.valueOf(year);

            filePath = folderPath + File.separator + "formThree.pdf";
            log.info("Final PDF Path : {}", filePath);

            file = new File(filePath);

            if (file.exists()) {
                FileInputStream fi = null;
                try {
                    fi = new FileInputStream(new File(filePath));
                    PdfReader pdfreader = new PdfReader(fi);
                    if (pdfreader != null) {
                        PdfDocument pdfDoc = new PdfDocument(pdfreader);
                        log.info("No of Pages in " + file.getName() + " " + pdfDoc.getNumberOfPages());
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
            // FIX: Add document margins so content doesn't run to the edge
            document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36); // 36pt ≈ 0.5 inch

            String rupees = " Rs. 1000 ( Rupees One thousand Only ) ";

            // ── TITLE BLOCK
            // ───────────────────────────────────────────────────────────────
            // FIX: multipliedLeading=1.4f; was 1 (too tight for heading block)
            Paragraph para = new Paragraph()
                    .setFont(font)
                    .setFontSize(HEADING_FONT_SIZE)
                    .add(new Text("Form 3").setBold())
                    .add("\n\n")
                    .add(new Text("[See rule 3 and 4]"))
                    .add("\n\n")
                    .add(new Text("Application for renewal of licence under section 13(4) of the West Bengal")
                            .setBold())
                    .add("\n")
                    .add(new Text("Agricultural Produce Marketing (Regulation) Act, 1972").setBold())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.4f) // FIX: was 1
                    .setMarginBottom(10);

            // ── ADDRESSEE
            // ─────────────────────────────────────────────────────────────────
            String market;
            if (!regMstr.isUnifiedLicense()) {
                market = OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())
                        ? regMstr.getOrgBaseMarket().toUpperCase()
                        : "...............................";
            } else {
                market = "Chief Executive Officer, West Bengal State Agricultural Marketing Board";
            }

            Paragraph para1 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("To\n"))
                    .add(new Text(market).setBold().setUnderline())
                    .add("\n\n")
                    .add(new Text("Sir,"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was 1
                    .setMarginBottom(8);

            // ── OPENING BODY: "I request for renewal..."
            // ──────────────────────────────────
            // FIX: para2 had setMultipliedLeading(0) causing it to overlap para3.
            // Merged para2 + para3 into one paragraph so leading works correctly.
            Paragraph para2 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("I request for the renewal of the Licence No "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())
                            ? regMstr.getOrgId().toUpperCase()
                            : "..............................................").setBold().setUnderline())
                    .add(new Text(" of which was granted to me on "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgValidFrom())
                            ? dateFormat.format(regMstr.getOrgValidFrom())
                            : "............................").setBold().setUnderline())
                    .add(new Text(" for carrying on the business of "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getCommodityCode())
                            ? regMstr.getCommodityCode().toUpperCase()
                            : "..............................................").setBold().setUnderline())
                    .add(new Text(" in "))
                    .add(new Text(!regMstr.isUnifiedLicense()
                            ? (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())
                                    ? regMstr.getOrgBaseMarket().toUpperCase() + " market"
                                    : "...............................")
                            : "The State of West Bengal").setBold().setUnderline())
                    .add(new Text(" within "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgDist())
                            ? regMstr.getOrgDist().toUpperCase()
                            : "...............................").setBold().setUnderline())
                    .add(new Text(" market area in "))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getCommodityCode())
                            ? regMstr.getCommodityCode().toUpperCase()
                            : "..............................................").setBold().setUnderline())
                    .add(new Text(" (the name of the agricultural produce)."))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: old para2 had leading=0, causing overlap with para3
                    .setMarginBottom(8);

            // ── "1. The necessary particulars..."
            // ─────────────────────────────────────────
            Paragraph para4 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("1. The necessary particulars are furnished below:"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.3f)
                    .setMarginBottom(6);

            // ── MAIN DETAILS TABLE
            // ────────────────────────────────────────────────────────
            // FIX: Use percent widths so table fills the page correctly.
            // Old code used setMarginLeft(55) without a width, making it narrow.
            Table table = new Table(UnitValue.createPercentArray(new float[] { 15, 85 }));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(10);

            // ── Row (i)(a): Name
            // ──────────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(i)(a)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Name of the applicant "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgName())
                                ? regMstr.getOrgName().toUpperCase()
                                : ".........................................").setBold().setUnderline());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (b): Contact
            // ──────────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 13), 1, 1, 0, false));
            {
                String contact = (OrgUtil.isNeitherNullNorEmpty(/* regMstr.getDocType() */"AD")
                        ? documentType(/* regMstr.getDocType() */"AD")
                        : " ")
                        + "  "
                        + (OrgUtil.isNeitherNullNorEmpty((user.getUserMobile()))
                                ? user.getUserMobile().toUpperCase()
                                : " ")
                        + "  "
                        + (OrgUtil.isNeitherNullNorEmpty(user.getUserEmail())
                                ? user.getUserEmail().toUpperCase()
                                : " ");
                Paragraph p = contentPara()
                        .add(new Text("Contact No and e-Mail Id - "))
                        .add(new Text(contact).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (c): Business Address
            // ─────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(c)", 15), 1, 1, 0, false));
            {
                String fullAddress = safe(regMstr.getOrgAddress()) + ", "
                        + safe(regMstr.getOrgPoliceStation()) + ", "
                        + safe(regMstr.getOrgPostOffice()) + ", "
                        + safe(regMstr.getOrgDist()) + ", "
                        + safe(regMstr.getOrgBlockName()) + ", "
                        + safe(regMstr.getOrgPin());
                Paragraph p = contentPara()
                        .add(new Text("Business Address in full - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(fullAddress)
                                ? fullAddress.toUpperCase().replaceAll("\n", " ")
                                : " ").setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (d): Place of business
            // ────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(d)", 15), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Place/places of business  "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgAddress())
                                ? regMstr.getOrgAddress().toUpperCase()
                                : ".........................................").setBold().setUnderline())
                        .add(new Text(" within "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgDist())
                                ? regMstr.getOrgDist().toUpperCase()
                                : "...............................").setBold().setUnderline())
                        .add(new Text("  market area/ market areas."));
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (e): Agricultural produce
            // ─────────────────────────────────────────────
            table.addCell(createCell(labelPara("(e)", 15), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Name of the agricultural produce which he wants to deal in "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getCommodityCode())
                                ? regMstr.getCommodityCode().toUpperCase()
                                : ".........................................").setBold().setUnderline());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (ii): Past experience header
            // ──────────────────────────────────────────
            table.addCell(createCell(labelPara("(ii)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara().add(new Text("Past experience"));
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (a): Licence terms observed
            // ───────────────────────────────────────────
            table.addCell(createCell(labelPara("(a)", 15), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether the terms and conditions of the licence were duly observed by the applicant "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(
                                /* regMstr.getRegAddtnInfo().getLicenseTerms() */"Licence Terms will fetch from db")
                                        ? /* regMstr.getRegAddtnInfo().getLicenseTerms() */"Licence Terms will fetch from db"
                                        : ".........................................")
                                .setBold().setUnderline());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row (b): Action by Market Committee
            // ───────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 15), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether any action was taken against him by the Market Committee for any breach of the terms and condition "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(
                                /* regMstr.getRegAddtnInfo().getBreachTerms() */"Breach Terms will fetch from db")
                                        ? /* regMstr.getRegAddtnInfo().getBreachTerms() */"Breach Terms will fetch from db"
                                        : ".........................................")
                                .setBold().setUnderline());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── FEE PARAGRAPH
            // ─────────────────────────────────────────────────────────────
            // FIX: multipliedLeading=1.4f; was 1 (same congestion fix as other paragraphs)
            Paragraph para6 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("2. The amount of fee deposited with particulars of receipt number and date. "))
                    .add(new Text(rupees).setBold().setUnderline())
                    .add(new Text(" | Receipt Number : "))
                    .add(new Text(OrgUtil
                            .isNeitherNullNorEmpty(/* regMstr.getDepositRefNo() */"D0847293")
                                    ? /* regMstr.getDepositRefNo() */"D0847293"
                                    : "")
                            .setBold().setUnderline())
                    .add(new Text(" | Date : "))
                    .add(new Text(OrgUtil
                            .isNeitherNullNorEmpty(/* regMstr.getRegFeeReceiptDate() */"01-01-2024")
                                    ? /* dateFormat.format(regMstr.getRegFeeReceiptDate()) */"01-01-2024"
                                    : "")
                            .setBold().setUnderline())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was 1
                    .setMarginTop(10)
                    .setMarginBottom(10);

            // ── DECLARATION
            // ───────────────────────────────────────────────────────────────
            // FIX: para10 + para7 + para8 were 3 separate paragraphs causing uneven
            // spacing.
            // Merged into one well-spaced block, same fix as Form1.
            Paragraph declHeading = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Declaration -").setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.4f)
                    .setMarginBottom(6);

            Paragraph declBody = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text(
                            "I certify that the facts stated above are true to the best of my knowledge.\n\n"
                                    + "I hereby undertake to abide by the conditions of licence, the provisions of the West "
                                    + "Bengal Agricultural Produce Marketing (Regulation) Act, 1972 and the rules and by-laws made "
                                    + "there under."))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was split across multiple paragraphs with leading=1
                    .setMarginBottom(16);

            // ── SIGNATURE TABLE
            // ───────────────────────────────────────────────────────────
            // FIX: Old table2 used a 3-col fixed table with setPaddingLeft(150) spacer
            // cells,
            // which made the layout unpredictable. Replaced with percent-width columns.
            Table table2 = new Table(UnitValue.createPercentArray(new float[] { 45, 10, 45 }));
            table2.setWidth(UnitValue.createPercentValue(100));
            table2.setMarginTop(10);

            Paragraph addrPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Full address of the applicant"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            table2.addCell(createCell(addrPara, 1, 1, 0, false));
            table2.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

            Paragraph sigPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Yours Faithfully\n\nSignature (in full) of the applicant"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.CENTER);
            table2.addCell(createCell(sigPara, 1, 1, 0, false));

            Paragraph placePara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Place: ___________________"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            table2.addCell(createCell(placePara, 1, 1, 0, false));
            table2.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

            Paragraph datePara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Date: ___________________"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            table2.addCell(createCell(datePara, 1, 1, 0, false));

            // ── ADD ALL ELEMENTS TO DOCUMENT
            // ──────────────────────────────────────────────
            document.add(para);
            document.add(para1);
            document.add(para2); // FIX: merged old para2+para3 — leading=0 overlap fixed
            // FIX: removed para3 (merged into para2 above)
            document.add(para4);
            document.add(table);
            document.add(para6);
            document.add(declHeading);
            document.add(declBody); // FIX: merged old para10+para7+para8
            document.add(table2);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error generating Form Three PDF for OrgId {}: {}", regMstr.getOrgId(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("error in createPdf for OrgId {}: {}", regMstr.getOrgId(), e.getMessage());
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
                return "REG PARTNERSHIP DEED"; // FIX: old code was missing break — fell through to PN
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
}
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

@Service("form2Service")
public class FormTwo {

    @Value("${file.formTwo.pdf.dir:./formTwo-pdf/}")
    private String pdfStoragePath;

    private static final Logger log = LoggerFactory.getLogger(FormTwo.class);

    private PdfFont font = null;
    private PdfFont fontBold = null;

    private static final float BODY_FONT_SIZE = 11f;
    private static final float HEADING_FONT_SIZE = 13f;

    @Autowired
    CommonService commonService;

    @Autowired
    UserMstrRepository uMstrRepository;

    public Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph)
                .setFontSize(BODY_FONT_SIZE)
                .setPaddingTop(4) // was 0/2 — gives vertical breathing room
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

    /** Helper: label paragraph for left column */
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
            log.info("Inside Form2->createPdf()");

            // FIX: assign to instance fields so labelPara()/contentPara() can use them
            this.font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            this.fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            UserMstr user = uMstrRepository.findByOrgId(regMstr.getOrgId());
            log.info("Fetched UserMstr for OrgId {}: {}", regMstr.getOrgId(), user);

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
                    for (File form2File : files) {
                        form2File.delete();
                    }
                }
            }

            filePath = folderPath + File.separator + "formTwo.pdf";
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
            // FIX: add document margins so content doesn't run to the edge
            document = new Document(pdf, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            // ── LICENSE FEE DETAILS
            // ───────────────────────────────────────────────────────
            String licenseFeesDetails = "";
            String regFees = commonService.fetchConfiguration(OrgConstants.DEPOSIT_PURPOSE.REGISTRATION,
                    regMstr.getOrgCategory());
            try {
                if (regMstr.isLicenseExists()) {
                    licenseFeesDetails = "AS PER MANUAL RECORDS";
                } else {
                    licenseFeesDetails = "Rs. " + regFees
                            + " | Receipt No. " + regMstr.getRegReceiptNo()
                            + " | Date : " + dateFormat.format(
                                    OrgUtil.isNeitherNullNorEmpty(regMstr.getRegistrationSubmitDate())
                                            ? regMstr.getRegistrationSubmitDate()
                                            : new Date());
                }
            } catch (Exception e) {
                log.info("Exception :- " + e.getMessage());
                e.printStackTrace();
            }

            // ── TITLE BLOCK
            // ───────────────────────────────────────────────────────────────
            // FIX: multipliedLeading=1.4f; was 1 (too tight for heading block)
            Paragraph para = new Paragraph()
                    .setFont(font)
                    .setFontSize(HEADING_FONT_SIZE)
                    .add(new Text("FORM 2").setBold())
                    .add("\n\n")
                    .add(new Text("[See rule 4]").setBold())
                    .add("\n\n")
                    .add(new Text("Application for licence under section 13(2) of the West Bengal Agricultural ")
                            .setBold())
                    .add("\n")
                    .add(new Text(
                            "Produce Marketing (Regulation) Act, 1972 and as amended thereof, for purposes of setting ")
                            .setBold())
                    .add("\n")
                    .add(new Text("up, establishing or continuing a place for storage/hat/bazaar/mela").setBold())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.4f) // FIX: was 1
                    .setMarginBottom(10);

            // ── ADDRESSEE
            // ─────────────────────────────────────────────────────────────────
            Paragraph para1 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("To\n").setBold())
                    .add(new Text("The ").setBold())
                    .add(new Text(!regMstr.isUnifiedLicense()
                            ? (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBaseMarket())
                                    ? regMstr.getOrgBaseMarket().toUpperCase() + " REGULATED MARKET COMMITTEE"
                                    : "...............................")
                            : "Chief Executive Officer, West Bengal State Agricultural Marketing Board")
                            .setBold().setUnderline())
                    .add("\n")
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was 1
                    .setMarginBottom(8);

            // ── SIR + OPENING BODY
            // ────────────────────────────────────────────────────────
            // FIX: old para2+para3+para4 had leading=0 causing overlaps — merged into one
            Paragraph para2 = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Sir,\n"))
                    .add(new Text("I beg to apply for a licence under section 13(2) of the West Bengal Agricultural "
                            + "Produce Marketing (Regulation) Act, 1972. Necessary particulars are given below:"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was 0
                    .setMarginBottom(10);

            // ── NORMALISE ORG CATEGORY
            // ────────────────────────────────────────────────────
            String orgCategory = "";
            if (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgCategory())) {
                switch (regMstr.getOrgCategory().trim()) {
                    case "HAT":
                        orgCategory = "HAT";
                        break;
                    case "MEL":
                        orgCategory = "MELA";
                        break;
                    case "BAZ":
                        orgCategory = "BAZAR";
                        break;
                    case "OTH":
                        orgCategory = "ANY OTHER PLACE FOR SALE OR PURCHASE";
                        break;
                    default:
                        orgCategory = regMstr.getOrgCategory();
                        break;
                }
            }

            // ── INDIVIDUAL/PROPRIETORSHIP FLAG
            // ────────────────────────────────────────────
            String INDSOP = (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgType())
                    && (regMstr.getOrgType().equals("SOP") || regMstr.getOrgType().equals("IND")))
                            ? "Yes"
                            : "No";
            boolean isIndSOP = INDSOP.equals("Yes");

            // ── IS REGISTERED NORMALISE
            // ───────────────────────────────────────────────────
            if (OrgUtil.isNeitherNullNorEmpty(regMstr.getIsRegistered())) {
                regMstr.setIsRegistered(regMstr.getIsRegistered().equals("Y") ? "YES" : "NO");
            } else {
                regMstr.setIsRegistered("");
            }

            // ── IS BG READY NORMALISE
            // ─────────────────────────────────────────────────────
            String isBGReady = "";
            if (OrgUtil.isNeitherNullNorEmpty(regMstr.getIsBGReady())) {
                isBGReady = regMstr.getIsBGReady().equals("Y") ? "YES" : "NO";
            }

            // ── MARKET AREA
            // ───────────────────────────────────────────────────────────────
            String marketArea = regMstr.isUnifiedLicense() ? "West Bengal"
                    : safe(regMstr.getOrgBaseMarket());

            // ── MARKET YEAR
            // ───────────────────────────────────────────────────────────────
            String marketYear = "";
            if (OrgUtil.isNeitherNullNorEmpty(regMstr.getMarketYearApp())) {
                String[] arr = regMstr.getMarketYearApp().split("-");
                marketYear = "20" + arr[0] + "-20" + arr[1];
            }

            // ── FULL ADDRESS
            // ──────────────────────────────────────────────────────────────
            String fullAddress = safe(regMstr.getOrgAddress()) + ", "
                    + safe(regMstr.getOrgPoliceStation()) + ", "
                    + safe(regMstr.getOrgPostOffice()) + ", "
                    + safe(regMstr.getOrgDist()) + ", "
                    + safe(regMstr.getOrgBlockName()) + ", "
                    + safe(regMstr.getOrgPin());

            // ── MAIN DETAILS TABLE
            // ────────────────────────────────────────────────────────
            // FIX: percent widths so table fills the page; old code had setMarginLeft(55)
            // with no width — table was narrow and unpredictable
            Table table = new Table(UnitValue.createPercentArray(new float[] { 12, 88 }));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setMarginBottom(10);

            // ── Row 1.(a): Name
            // ───────────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("1.(a)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Name of the applicant for which licence applied for - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgName())
                                ? regMstr.getOrgName().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 1.(b): Individual/Proprietorship
            // ──────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Whether applicant is an individual/proprietorship or not - "))
                        .add(new Text(INDSOP).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 1.(c): Business Address
            // ───────────────────────────────────────────────
            table.addCell(createCell(labelPara("(c)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Business Address in full -\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(fullAddress)
                                ? fullAddress.toUpperCase().replaceAll("\n", " ")
                                : " ")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 1.(d): Contact
            // ────────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(d)", 8), 1, 1, 0, false));
            {
                String userMobile = (user != null && OrgUtil.isNeitherNullNorEmpty(user.getUserMobile()))
                        ? user.getUserMobile().toUpperCase()
                        : " ";
                String userEmail = (user != null && OrgUtil.isNeitherNullNorEmpty(user.getUserEmail()))
                        ? user.getUserEmail().toUpperCase()
                        : " ";

                String contact = (OrgUtil.isNeitherNullNorEmpty(/* regMstr.getDocType() */"AD")
                        ? documentType(/* regMstr.getDocType() */"AD")
                        : " ")
                        + "  "
                        + userMobile
                        + "  "
                        + userEmail;
                Paragraph p = contentPara()
                        .add(new Text("Contact No and e-Mail Id, if any, of any associated with -\n"))
                        .add(new Text(contact).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 2.(a): Constitution
            // ───────────────────────────────────────────────────
            table.addCell(createCell(labelPara("2.(a)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Constitution of farm/company/society - "))
                        .add(new Text(isIndSOP ? "NA" : getConstitution(regMstr.getOrgType()))
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 2.(b): Registered with
            // ────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Registered with whom? - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getRegisteredWith())
                                ? regMstr.getRegisteredWith().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 2.(c): Particulars of registration
            // ────────────────────────────────────
            table.addCell(createCell(labelPara("(c)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Particulars of registration - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getRegistrationDetails())
                                ? regMstr.getRegistrationDetails().toUpperCase()
                                : "NA")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 2.(d): Managing Director
            // ──────────────────────────────────────────────
            table.addCell(createCell(labelPara("(d)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Name of the Managing Director or Manager or Principal Officer who will actually conduct the business - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgContactPerson())
                                ? regMstr.getOrgContactPerson().toUpperCase()
                                : "NA")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 3.(a): Type of place
            // ──────────────────────────────────────────────────
            table.addCell(createCell(labelPara("3.(a)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether the application is for a storage or hat or bazar or mela or any other place for sale or purchase "))
                        .add(new Text(decodeCategory(orgCategory.toUpperCase()))
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 3.(b): Name/style
            // ─────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Name or style under which the storage / hat / bazaar / mela etc. will be conducted\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgName())
                                ? regMstr.getOrgName().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 3.(c): Situation/Market Area
            // ──────────────────────────────────────────
            table.addCell(createCell(labelPara("(c)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Situation of the applicant's place of business with particulars as to the Market Area/ areas "
                                        + marketArea
                                        + " (name of police station / village / town / premises number / plot number and boundary of premises to be given)\n"))
                        .add(new Text((OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgAddress())
                                ? regMstr.getOrgAddress().replaceAll("\n", " ").toUpperCase()
                                : " ")
                                + " "
                                + (OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgPoliceStation())
                                        ? regMstr.getOrgPoliceStation()
                                        : ""))
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 3.(d): Nature of interest on land
            // ─────────────────────────────────────
            table.addCell(createCell(labelPara("(d)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Nature of the applicant's interest on the land and premises\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(/* regMstr.getRegAddtnInfo().getLandDetails() */"")
                                ? /* regMstr.getRegAddtnInfo().getLandDetails().toUpperCase() */"willfetch from db"
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 3.(e): Market year
            // ────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(e)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Market year or part thereof for which licence is applied for - "))
                        .add(new Text(marketYear).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(a): Bank account
            // ───────────────────────────────────────────────────
            table.addCell(createCell(labelPara("4.(a)", 0), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Has the applicant any bank account? If so the bank details should be furnished -\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgBankName())
                                ? regMstr.getOrgBankName().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(b): Experience
            // ─────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(b)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether the applicant has any experience in the business of the nature for which the licence is applied for. If so, the particulars should be furnished - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getPrevExp())
                                ? regMstr.getPrevExp().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(c): Previous licence
            // ───────────────────────────────────────────────
            table.addCell(createCell(labelPara("(c)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether the applicant has any licence for any kind of market from this Market Committee. If so, full particular should be stated. - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getPrevLicense())
                                ? regMstr.getPrevLicense().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(d): Previous market licence ───────────────────────────────────────
            table.addCell(createCell(labelPara("(d)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Did the applicant hold a licence from the Market Committee on any previous occasion? If so, give particulars including its suspension or cancellation, if any - "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getPrevmarketLicense())
                                ? regMstr.getPrevmarketLicense().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(e): Misconduct
            // ─────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(e)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "If the applicant had ever been found guilty of professional misconduct, details of convictions "))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getGuiltyDoc())
                                ? regMstr.getGuiltyDoc().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(f): Amenities
            // ──────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(f)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Details of water supply, drainage, sanitation, shelter, parking, storage and other amenities made or proposed to be made by the applicant in the place of business for which the licence is applied for\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(/*
                                                                     * regMstr.getRegAddtnInfo().getAmenitiesDetails()
                                                                     */"")
                                ? /* regMstr.getRegAddtnInfo().getAmenitiesDetails().toUpperCase() */"will fetch from db"
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(g): Transaction details ───────────────────────────────────────────
            table.addCell(createCell(labelPara("(g)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Details regarding frequency (date on which held), usual hours of transaction, nature and volume of commodities to be handled, number of persons assembling\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(/*
                                                                     * regMstr.getRegAddtnInfo().getTransactionDetails()
                                                                     */"")
                                ? /* regMstr.getRegAddtnInfo().getTransactionDetails().toUpperCase() */"will fetch from db"
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(h): Godown details
            // ─────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(h)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Details godowns and other storage premises-particulars, capacity and location\n"))
                        .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getGodownDetails())
                                ? regMstr.getGodownDetails().toUpperCase()
                                : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(i): Employee details
            // ───────────────────────────────────────────────
            table.addCell(createCell(labelPara("(i)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text("Details of employees / assistant / agents "))
                        .add(new Text(
                                OrgUtil.isNeitherNullNorEmpty(/* regMstr.getRegAddtnInfo().getEmployeeDetails() */"")
                                        ? /* regMstr.getRegAddtnInfo().getEmployeeDetails().toUpperCase() */"will fetch from db"
                                        : "")
                                .setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 4.(j): Bank guarantee
            // ─────────────────────────────────────────────────
            table.addCell(createCell(labelPara("(j)", 8), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Whether the applicant is prepared to furnish cash security or bank guarantee as may be required by the market committee - "))
                        .add(new Text(isBGReady.toUpperCase()).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── Row 5: Licence fee
            // ────────────────────────────────────────────────────────
            table.addCell(createCell(labelPara("5.", 10), 1, 1, 0, false));
            {
                Paragraph p = contentPara()
                        .add(new Text(
                                "Amount of licence fee deposited with particulars of receipt number and date. - "))
                        .add(new Text(licenseFeesDetails).setUnderline().setBold());
                table.addCell(createCell(p, 1, 1, 0, false));
            }

            // ── DECLARATION
            // ───────────────────────────────────────────────────────────────
            // FIX: old code had para5(Declaration heading) + para6 + para7 + para8 + para9
            // all with leading=0 or 1, causing serious overlap. Merged into two clean
            // blocks.
            Paragraph declHeading = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Declaration -").setBold())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.4f)
                    .setMarginTop(10)
                    .setMarginBottom(6);

            Paragraph declBody = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text(
                            "I certify that the facts stated above are true to the best of my knowledge.\n\n"
                                    + "I hereby undertake to abide by the conditions of licence, the provisions of the West "
                                    + "Bengal Agricultural Produce Marketing (Regulation) Act, 1972 and the rules framed there under "
                                    + "and the byelaws made thereunder by the Regulated Market Committee.\n\n"
                                    + "I shall be responsible for all acts, omissions and commissions of my employees, if it so happens."))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.4f) // FIX: was 0/1 split across 4 paragraphs
                    .setMarginBottom(16);

            // ── SIGNATURE TABLE
            // ───────────────────────────────────────────────────────────
            // FIX: old table1 (4-col with padding(30) spacers) + table2 was redundant and
            // produced inconsistent layout. Replaced with single percent-width table.
            Table signatureTable = new Table(UnitValue.createPercentArray(new float[] { 45, 10, 45 }));
            signatureTable.setWidth(UnitValue.createPercentValue(100));
            signatureTable.setMarginTop(10);

            Paragraph addrPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Full address of the applicant\n"))
                    .add(new Text(OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgAddress())
                            ? regMstr.getOrgAddress().replaceAll("\n", " ")
                            : " ").setBold())
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            signatureTable.addCell(createCell(addrPara, 1, 1, 0, false));
            signatureTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

            Paragraph sigPara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Yours faithfully\n\nSignature (in full) of the applicant"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.CENTER);
            signatureTable.addCell(createCell(sigPara, 1, 1, 0, false));

            Paragraph placePara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Place: ___________________"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            signatureTable.addCell(createCell(placePara, 1, 1, 0, false));
            signatureTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // spacer

            Paragraph datePara = new Paragraph()
                    .setFont(font)
                    .setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Date: ___________________"))
                    .setMultipliedLeading(1.3f)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.LEFT);
            signatureTable.addCell(createCell(datePara, 1, 1, 0, false));

            // ── ADD ALL ELEMENTS TO DOCUMENT
            // ──────────────────────────────────────────────
            document.add(para);
            document.add(para1);
            document.add(para2); // FIX: merged old para2+para3+para4 (leading=0 overlap fixed)
            document.add(table);
            // FIX: removed stray document.add(para5) — old code added last para5 (empty)
            document.add(declHeading);
            document.add(declBody); // FIX: merged old para6+para7+para8+para9
            // FIX: removed orphaned table1+para11+para14; replaced with signatureTable
            document.add(signatureTable);

        } catch (IOException e) {
            e.printStackTrace();
            log.error("IOException generating Form Two PDF: {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error generating Form Two PDF for OrgId {}: {}", regMstr.getOrgId(), e.getMessage());
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
                return "REG PARTNERSHIP DEED"; // FIX: old code missing break — fell through to PN
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

        switch (con.trim().toUpperCase()) {
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

    private String decodeCategory(String category) {
        if (category == null)
            return "";
        switch (category) {
            case "TRADER":
                return "Trader";
            case "PROCESSOR":
                return "Processor";
            case "COMMISSION AGENT":
                return "Commission Agent";
            case "BROKER":
                return "Broker";
            case "SURVEYOR":
                return "Surveyor";
            case "WEIGHMAN":
                return "Weighman";
            case "MEASURER":
                return "Measurer";
            case "WAREHOUSEMAN":
                return "Warehouseman";
            case "STORAGE":
                return "STORAGE";
            case "BAZAR":
                return "BAZAR";
            case "MELA":
                return "MELA";
            case "HAT":
                return "HAT";
            case "ANY OTHER PLACE FOR SALE OR PURCHASE":
                return "Any Other Place For Sale Or Purchase";
            default:
                return category;
        }
    }

    public void deletePdf(RegistrationMstr regMstr) {
        try {
            log.info("Inside Form2->deletePdf()");
            String orgId = regMstr.getOrgId().toUpperCase();
            String filePath = pdfStoragePath + orgId + File.separator + "formTwo.pdf";
            log.info("File Path : " + filePath);
            File file = new File(filePath);
            if (file.exists()) {
                log.info("Deleting file: " + filePath);
                file.delete();
            }
        } catch (Exception e) {
            log.error("Error deleting Form Two PDF: {}", e.getMessage());
        }
    }
}

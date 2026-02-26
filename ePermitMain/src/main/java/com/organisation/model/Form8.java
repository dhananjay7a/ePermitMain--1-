package com.organisation.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.organisation.util.OrgUtil;
import com.organisation.model.RegistrationMstr;

@Service("form8Service")
public class Form8 {

    @Value("${file.formEight.pdf.dir:./formEight-pdf/}")
    private String pdfStoragePath;

    private static final Logger log = LoggerFactory.getLogger(Form8.class);
    private static final DateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");
    private static final String FORM_EIGHT_FOLDER_NAME = "form8";
    private static final String FORM_EIGHT_FILENAME = "Form8.pdf";

    private static final float BODY_FONT_SIZE = 11f;
    private static final float HEADING_FONT_SIZE = 13f;

    private Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph).setFontSize(BODY_FONT_SIZE)
                .setPaddingTop(4).setPaddingBottom(4).setPaddingLeft(4).setPaddingRight(4);
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

    private String value(String input) {
        return OrgUtil.isNeitherNullNorEmpty(input) ? input : "...............................";
    }

    public String createPdf(RegistrationMstr regMstr) {
        Document document = null;
        PdfDocument pdf = null;
        File file = null;
        String filePath = null;

        try {
            log.info("Inside Form8->createPdf()");

            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return null;
            }

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            String orgId = regMstr.getOrgId().toUpperCase();
            String folderPath = pdfStoragePath + orgId + File.separator + FORM_EIGHT_FOLDER_NAME;

            file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            File[] files = file.listFiles();
            if (files != null) {
                for (File form8File : files) {
                    form8File.delete();
                }
            }

            filePath = folderPath + File.separator + FORM_EIGHT_FILENAME;
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
            }

            pdf = new PdfDocument(new PdfWriter(filePath));
            document = new Document(pdf, PageSize.A4);
            document.setMargins(24, 24, 24, 24);

            Paragraph header = new Paragraph().setFont(font).setFontSize(HEADING_FONT_SIZE)
                    .add(new Text("FORM 8").setBold()).add("\n\n")
                    .add(new Text("[See rule 18(3)]")).add("\n\n")
                    .add(new Text("Notice on the Licensee").setBold())
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);

            Table bodyTable = new Table(UnitValue.createPercentArray(new float[] { 100 }))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginLeft(20);

            Paragraph para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text(value(regMstr.getOrgBaseMarket()).toUpperCase()))
                    .add("-")
                    .add(new Text(value(/* regMstr.getMarketName() */regMstr.getOrgBaseMarket()).toUpperCase()))
                    .add(new Text(" Regulated Market Committee"))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false).setPadding(0));

            para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add("\nTo\n")
                    .add(new Text("Shri "))
                    .add(new Text(value(regMstr.getOrgName()).toUpperCase()))
                    .add("\n\n")
                    .add(new Text("Licence No. "))
                    .add(value(regMstr.getOrgId()))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false).setPadding(0));

            String fortnightEnd = regMstr.getRequestEndDate() != null
                    ? SDF.format(regMstr.getRequestEndDate())
                    : ".........";

            para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Whereas the fortnightly Return of Turnover in form 7 for the fortnight ending "))
                    .add(fortnightEnd)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false).setPaddingLeft(20));

            String submittedDate = regMstr.getRegistrationSubmitDate() != null
                    ? SDF.format(regMstr.getRegistrationSubmitDate())
                    : ".........";

            para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text("submitted by him on "))
                    .add(submittedDate)
                    .add(new Text(" does not appear to be satisfactory: "))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false));

            para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text(
                            "Now, therefore, he is hereby directed under the provisions of Sec. 17A(4) of the West"))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false).setPaddingLeft(20));

            para = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text(
                            "Bengal Agricultural Produce Marketing (Regulation) Act, 1972, to appear before the under"))
                    .add("\n")
                    .add(new Text(
                            "signed either personally or through an authorised nominee, on ................... at"))
                    .add("\n")
                    .add(new Text(
                            "............ and produce before the undersigned all oral and/or documentary evidence in"))
                    .add("\n")
                    .add(new Text("support of the return."))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            bodyTable.addCell(createCell(para, 1, 1, 0, false));

            Table footerTable = new Table(UnitValue.createPercentArray(new float[] { 60, 40 }))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginLeft(20);

            Paragraph footerLeft = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text("Dated this ..................... day of .....................20 .......... "))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);
            footerTable.addCell(createCell(footerLeft, 1, 1, 0, false).setPaddingRight(40));

            Paragraph footerRight = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text("(Signed)\nSecretary"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);
            footerTable.addCell(createCell(footerRight, 1, 1, 0, false));

            Paragraph seal = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text("\n(Seal of the RMC)"))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMultipliedLeading(1.2f);
            footerTable.addCell(createCell(seal, 1, 1, 0, false).setPaddingRight(40));

            Paragraph govtOrder = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                    .add(new Text(
                            "By order of the Governor\nJ. DUTTA GUPTA\nDeputy Secretary to the\nGovernment of West Bengal"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);
            footerTable.addCell(createCell(govtOrder, 1, 1, 0, false));

            // AssessmentBean signature from provided code (kept commented as requested)
            // public String createPdf(AssessmentBean assessmentBean) {

            // AssessmentBean-dependent lines from provided Form8 (kept commented as
            // requested)
            // filePath = filePath + assessmentBean.getTraderLicenceeNo() + "_" +
            // assessmentBean.getMarketCode() + "_"
            // + assessmentBean.getAssessmentNo() + "_" + "Form8.pdf";
            // para.add(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getMarketCode())
            // ? assessmentBean.getMarketCode().toUpperCase() : "....."));
            // para.add(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getMarketName())
            // ? assessmentBean.getMarketName().toUpperCase() : "....."));
            // para.add(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getTraderName())
            // ? assessmentBean.getTraderName().toUpperCase() :
            // "..............................."));
            // .add(assessmentBean.getTraderLicenceeNo())
            // .add((ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getToDate()) ?
            // sdf.format(assessmentBean.getToDate()) : "........."))
            // .add((ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getSubmittedDate()) ?
            // sdf.format(assessmentBean.getSubmittedDate()) : "........."))

            document.add(header);
            document.add(bodyTable);
            document.add(footerTable);

        } catch (IOException e) {
            log.error("Error creating Form 8 PDF", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }

        log.info("Response : {} | {}", filePath, OrgConstants.PDF_RESPONSE.SUCCESS);
        return filePath;
    }
}
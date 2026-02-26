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

@Service("form7Service")
public class Form7 {

    @Value("${file.formSeven.pdf.dir:./formSeven-pdf/}")
    private String pdfStoragePath;

    private static final Logger log = LoggerFactory.getLogger(Form7.class);
    private static final String FORM_SEVEN_FOLDER_NAME = "form7";
    private static final String FORM_SEVEN_FILENAME = "Form7.pdf";
    private static final DateFormat SDF = new SimpleDateFormat("dd-MM-yyyy");

    private static final float BODY_FONT_SIZE = 11f;
    private static final float HEADING_FONT_SIZE = 13f;

    private Cell createCell(Paragraph paragraph, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(paragraph)
                .setFontSize(BODY_FONT_SIZE)
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

    private Cell createCell(Table table, int colspan, int rowspan, int border, boolean isBorder) {
        Cell cell = new Cell(rowspan, colspan).add(table).setFontSize(BODY_FONT_SIZE).setPadding(0);
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
            log.info("Inside Form7->createPdf()");

            if (regMstr == null || !OrgUtil.isNeitherNullNorEmpty(regMstr.getOrgId())) {
                return null;
            }

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            String orgId = regMstr.getOrgId().toUpperCase();
            String folderPath = pdfStoragePath + orgId + File.separator + FORM_SEVEN_FOLDER_NAME;

            file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            File[] files = file.listFiles();
            if (files != null) {
                for (File form7File : files) {
                    form7File.delete();
                }
            }

            filePath = folderPath + File.separator + FORM_SEVEN_FILENAME;
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
            document = new Document(pdf, PageSize.A4.rotate());
            document.setMargins(24, 24, 24, 24);

            Paragraph para = new Paragraph().setFont(font).setFontSize(HEADING_FONT_SIZE);
            para.add(new Text("FORM 7").setBold()).add("\n").add(new Text("[See rule 18(1)]")).add("\n")
                    .add(new Text(" Fortnightly Return of Turnover ")).setBold()
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);

            Paragraph para1 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);

            para1.add(new Text(value(regMstr.getOrgBaseMarket()).toUpperCase()))
                    .add(new Text(" Regulated Market Committee")).add("\n").setTextAlignment(TextAlignment.LEFT)
                    .setMarginLeft(30).setMultipliedLeading(1.2f);

            // Original AssessmentBean-dependent line from provided code (kept as requested)
            // para1.add(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getMarketCode())
            // ? assessmentBean.getMarketCode().toUpperCase() :
            // "..............................."));

            Table table1 = new Table(3);
            Paragraph para2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);

            para2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para2.add(new Text("Name of the Licensee: "))
                    .add(value(regMstr.getOrgName()).toUpperCase())
                    .setVerticalAlignment(VerticalAlignment.TOP).setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);

            table1.addCell(createCell(para2, 1, 1, 0, false)).setMarginLeft(30);
            table1.addCell(new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(150));

            para2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para2.add(new Text("Report for the fortnight ending")).add("\n")
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f);
            table1.addCell(createCell(para2, 1, 1, 0, false));

            para2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para2.add(new Text("Licence No.: ")).add(value(regMstr.getOrgId())).add("\n")
                    .setVerticalAlignment(VerticalAlignment.TOP).setTextAlignment(TextAlignment.LEFT)
                    .setMultipliedLeading(1.2f);

            table1.addCell(createCell(para2, 1, 1, 0, false)).setMarginLeft(30);
            table1.addCell(new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(150));

            para2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            String toDate = regMstr.getRequestEndDate() != null ? SDF.format(regMstr.getRequestEndDate())
                    : "........................................";

            para2.add(new Text(toDate)).add("\n")
                    .setVerticalAlignment(VerticalAlignment.TOP).setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);

            table1.addCell(createCell(para2, 1, 1, 0, false)).setMarginLeft(30);

            // Original AssessmentBean-dependent lines from provided code (kept as
            // requested)
            // para2.add(new
            // Text((ePermitUtil.isNeitherNullNorEmpty(assessmentBean.getToDate())
            // ? sdf.format(assessmentBean.getToDate()) :
            // "........................................")));
            // para2.add(new Text("Licence No.:
            // ")).add(assessmentBean.getTraderLicenceeNo());
            // para2.add(ePermitUtil.isNeitherNullNorEmpty(assessmentBeanList.get(0).getTraderName())
            // ? assessmentBeanList.get(0).getTraderName().toUpperCase() : "............");

            Table table = new Table(
                    UnitValue.createPercentArray(new float[] { 5, 30, 10, 30, 13, 13, 15, 3, 3, 12, 3 }));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setPadding(0).setMarginLeft(20).setBorderTop(new SolidBorder(1)).setBorderBottom(new SolidBorder(1));

            Paragraph para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);

            para4.add(new Text("Date")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(10).setPaddingRight(40).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("From whom purchased")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingTop(10).setBorderBottom(new SolidBorder(1))
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text(""));
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(25).setPaddingRight(30).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("To whom sold(Where the buyer is not licensee )"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setTextAlignment(TextAlignment.CENTER)
                    .setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPadding(10).setBorderBottom(new SolidBorder(1))
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Commodity")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(8).setPaddingRight(8).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Quantity of No. Purchased / sold")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(9).setPaddingRight(8).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Rate of which Purchased / sold")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(8).setPaddingRight(7).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Total Value (RS)")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(8).setPaddingRight(9).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Total market Fee payable (RS)")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(8).setPaddingRight(9).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text(
                    "Amount of market fee recovered from purchaser where he is not a licensee, with data (RS)"))
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(8).setPaddingRight(8).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            para4 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para4.add(new Text("Remarks")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table.addCell(createCell(para4, 1, 1, 0, false).setPaddingLeft(10).setPaddingRight(12).setPaddingTop(10)
                    .setBorderTop(new SolidBorder(1)));

            table.addCell(new Cell().setBorder(Border.NO_BORDER));

            Table table2 = new Table(UnitValue.createPercentArray(new float[] { 39, 2, 39 }));
            table2.setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

            Paragraph para5 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para5.add(new Text("Name and")).add(" address").setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table2.addCell(createCell(para5, 1, 1, 0, false));

            para5 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para5.add(new Text(""));
            table2.addCell(createCell(para5, 1, 1, 0, false));

            para5 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para5.add(new Text("Bill No.   if any")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f);
            table2.addCell(createCell(para5, 1, 1, 0, false));

            table.addCell(createCell(table2, 1, 1, 0, false));

            table.addCell(new Cell().setBorder(Border.NO_BORDER));

            Table table3 = new Table(UnitValue.createPercentArray(new float[] { 35, 10, 35 }));
            table3.setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

            Paragraph para6 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para6.add(new Text("Name and")).add(" address").setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.LEFT).setMultipliedLeading(1.2f);
            table3.addCell(createCell(para6, 1, 1, 0, false));

            para6 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para6.add(new Text(""));
            table3.addCell(createCell(para6, 1, 1, 0, false));

            para6 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para6.add(new Text("Bill No.       if any")).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f);
            table3.addCell(createCell(para6, 1, 1, 0, false));

            table.addCell(createCell(table3, 1, 1, 0, false));

            para6 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            para6.add(new Text(""));
            table.addCell(createCell(para6, 1, 1, 0, false).setBorderBottom(new SolidBorder(1)));

            Table table4 = new Table(12).setMarginLeft(20).setBorderBottom(new SolidBorder(1));
            for (int colNo = 1; colNo <= 12; colNo++) {
                Paragraph col = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE)
                        .add(new Text(String.valueOf(colNo)))
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setTextAlignment(TextAlignment.LEFT);
                table4.addCell(createCell(col, 1, 1, 0, false).setBorderBottom(new SolidBorder(1)));
            }

            // AssessmentBean-dependent logic from provided Form7 (kept commented as
            // requested)
            // Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            // for (AssessmentBean bean : assessmentBeanList) {
            // table4.addCell(createCell(new Paragraph(new Text(
            // ePermitUtil.isNeitherNullNorEmpty(bean.getCreatedOn()) ?
            // formatter.format(bean.getCreatedOn()) : "")), 1,
            // 1, 0, false).setFontSize(9).setPadding(0).setBorder(Border.NO_BORDER));
            //
            // if (!ePermitConstants.TRUE.equalsIgnoreCase(bean.getIsSeller())) {
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getSellerName())
            // ? bean.getSellerName().toUpperCase() : ""))
            // .add(" (").add((ePermitUtil.isNeitherNullNorEmpty(bean.getSellerAddress())
            // ? String.valueOf(bean.getSellerAddress()) : "")).add(")"), 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getBillNo())
            // ? bean.getBillNo().toUpperCase() : "")), 1, 1, 0, false));
            // } else {
            // table4.addCell(createCell(new Paragraph(new Text("")), 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new Text("")), 1, 1, 0, false));
            // }
            //
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getCommodityId())
            // ? bean.getCommodityId().toUpperCase() : "")), 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getQty())
            // ? String.valueOf(bean.getQty()) : "0")), 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new Text(
            // ePermitUtil.isNeitherNullNorEmpty(bean.getRate()) ?
            // String.valueOf(bean.getRate()) : "0")),
            // 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getTradeValue())
            // ? String.valueOf(bean.getTradeValue()) : "0")), 1, 1, 0, false));
            // table4.addCell(createCell(new Paragraph(new
            // Text(ePermitUtil.isNeitherNullNorEmpty(bean.getMarketFee())
            // ? String.valueOf(bean.getMarketFee()) : "0")), 1, 1, 0, false));
            // }

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 12; j++) {
                    table4.addCell(createCell(new Paragraph(" ").setFont(font).setFontSize(9), 1, 1, 0, false)
                            .setBorderBottom(new SolidBorder(1)));
                }
            }

            Paragraph paraForSignature = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            paraForSignature.add(new Text("Signature of the Licensee")).add("\n")
                    .setVerticalAlignment(VerticalAlignment.TOP).setTextAlignment(TextAlignment.RIGHT)
                    .setMultipliedLeading(1.2f).setMarginRight(45).setMarginTop(40).setBold();

            Paragraph paraForSignature1 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            paraForSignature1.add(new Text(" Date :  ")).setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f).setMarginRight(100).setBold();

            Paragraph paraForSignature2 = new Paragraph().setFont(font).setFontSize(BODY_FONT_SIZE);
            paraForSignature2.add(new Text("Full name and address of the licensee ")).add("\n").add("\n")
                    .add("...................................").add("\n").add("\n")
                    .add("...................................").setVerticalAlignment(VerticalAlignment.TOP)
                    .setTextAlignment(TextAlignment.RIGHT).setMultipliedLeading(1.2f).setMarginRight(45).setBold();

            // Legacy file naming from provided AssessmentBean version (kept commented as
            // requested)
            // filePath = filePath + assessmentBean.getTraderLicenceeNo() + "_" +
            // assessmentBean.getMarketCode() + "_"
            // + assessmentBeanList.get(0).getAssessmentNo() + ".pdf";

            document.add(para);
            document.add(para1);
            document.add(table1);
            document.add(table);
            document.add(table4);
            document.add(paraForSignature);
            document.add(paraForSignature1);
            document.add(paraForSignature2);

        } catch (IOException e) {
            log.error("Error creating Form 7 PDF", e);
        } finally {
            if (document != null) {
                document.close();
            }
        }

        log.info("Response : {} | {}", filePath, OrgConstants.PDF_RESPONSE.SUCCESS);
        return filePath;
    }
}

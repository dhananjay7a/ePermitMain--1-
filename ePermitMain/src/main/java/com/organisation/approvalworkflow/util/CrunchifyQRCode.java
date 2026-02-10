package com.organisation.approvalworkflow.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.organisation.model.RegistrationMstr;
import com.google.zxing.client.j2se.MatrixToImageWriter;

public class CrunchifyQRCode {

    public String generateQRCode(RegistrationMstr reg) throws Exception {

        String qrText = "ORG_ID=" + reg.getOrgId();
        String filePath = System.getProperty("java.io.tmpdir")
                + "/FORM4_QR_" + reg.getOrgId() + ".png";

        BitMatrix matrix = new MultiFormatWriter().encode(
                qrText,
                BarcodeFormat.QR_CODE,
                200,
                200);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        return filePath;
    }
}

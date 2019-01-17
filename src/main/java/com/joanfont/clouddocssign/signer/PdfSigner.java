package com.joanfont.clouddocssign.signer;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.joanfont.clouddocssign.trustedx.TrustedXOAuthClient;
import com.joanfont.clouddocssign.trustedx.TrustedXServerSignature;
import com.joanfont.clouddocssign.trustedx.entities.Identity;

import java.io.*;
import java.security.cert.Certificate;

public class PdfSigner {

    private static final Rectangle SIGNATURE_LOCATION = new Rectangle(36, 748, 144, 780);
    private static final int SIGNATURE_PAGE = 1;
    private static final String SIGNATURE_FIELD_NAME = "sig";

    private TrustedXOAuthClient client;

    public PdfSigner(TrustedXOAuthClient client) {
        this.client = client;
    }

    public ByteArrayOutputStream sign(ByteArrayInputStream bytes, Identity identity) throws Exception {
        PdfReader reader = new PdfReader(bytes);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        PdfStamper stamper = PdfStamper.createSignature(reader, output, '\0');
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setReason("CloudDocsSign");
        appearance.setLocation("CloudDocsSign");
        appearance.setVisibleSignature(
                SIGNATURE_LOCATION,
                SIGNATURE_PAGE,
                SIGNATURE_FIELD_NAME
        );

        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature = new TrustedXServerSignature(this.client, identity);

        Certificate[] chain = {identity.getCertificate()};
        MakeSignature.signDetached(
                appearance,
                digest,
                signature,
                chain,
                null,
                null,
                null,
                0,
                MakeSignature.CryptoStandard.CADES
        );

        return output;
    }
}

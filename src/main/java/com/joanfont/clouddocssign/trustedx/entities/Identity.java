package com.joanfont.clouddocssign.trustedx.entities;

import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Identity {

    private String id;

    private String pkcs12;

    private String password;

    private List<String> labels;

    private Certificate certificate;

    public Identity() {

    }

    public Identity(String id) {
        this.id = id;
    }

    public Identity(String id, String pkcs12, String password, List<String> labels, Certificate certificate) {
        this.id = id;
        this.pkcs12 = pkcs12;
        this.password = password;
        this.labels = labels;
        this.certificate = certificate;
    }

    public Identity(String pkcs12, String password, List<String> labels) {
        this.pkcs12 = pkcs12;
        this.password = password;
        this.labels = labels;
    }

    public String getId() {
        return this.id;
    }

    public Identity setId(String id) {
        this.id = id;
        return this;
    }

    public String getPkcs12() {
        return this.pkcs12;
    }

    public Identity setPkcs12(String pkcs12) {
        this.pkcs12 = pkcs12;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public Identity setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public Identity setLabels(List<String> labels) {
        this.labels = labels;
        return this;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public Identity setCertificate(Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public Role getRole() {
        String label = this.labels
                .stream()
                .filter(s -> s.startsWith("role:"))
                .collect(Collectors.joining());

        return Role.fromValue(label.replace("role:", ""));
    }

    public static Identity fromMap(Map map) throws CertificateException {
        Identity identity = new Identity();

        identity
                .setId((String) map.get("id"))
                .setLabels((List<String>) map.get("labels"));

        if (map.get("details") != null) {
            Certificate certificate = buildCertificateFromString(
                    ((Map<String, String>) map.get("details")).get("certificate")
            );

            identity.setCertificate(certificate);
        }

        return identity;
    }

    protected static Certificate buildCertificateFromString(String raw) throws CertificateException {
        byte[] bytes = Base64.getDecoder().decode(raw);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");

        return certificateFactory.generateCertificate(
                new ByteArrayInputStream(bytes)
        );
    }
}

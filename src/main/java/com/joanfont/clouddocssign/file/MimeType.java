package com.joanfont.clouddocssign.file;

public enum MimeType {

    PDF ("application/pdf"),
    TEXT ("text/plain");

    private String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return this.mimeType;
    }


    public static MimeType fromValue(String value) {
        for (MimeType mimeType : MimeType.values()) {
            if (mimeType.mimeType.equalsIgnoreCase(value)) {
                return mimeType;
            }
        }
        return null;
    }

}

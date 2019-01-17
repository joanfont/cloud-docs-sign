package com.joanfont.clouddocssign.file;

public abstract class File {

    protected String id;

    protected String name;

    protected MimeType mimeType;

    public File setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return this.id;
    }

    public File setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public File setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public abstract String getNameToUpload();

    public abstract void setNameToUpload(String nameToUpload);
}

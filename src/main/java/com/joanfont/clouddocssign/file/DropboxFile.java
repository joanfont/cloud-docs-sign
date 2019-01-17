package com.joanfont.clouddocssign.file;

public class DropboxFile extends File {

    private String path;

    public String getPath() {
        return path;
    }

    public File setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public String getNameToUpload() {
        return this.path;
    }

    @Override
    public void setNameToUpload(String nameToUpload) {
        this.path = nameToUpload;
    }
}

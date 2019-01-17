package com.joanfont.clouddocssign.file;

public class GoogleDriveFile extends File {

    @Override
    public String getNameToUpload() {
        return this.name;
    }

    @Override
    public void setNameToUpload(String nameToUpload) {
        this.name = nameToUpload;
    }
}

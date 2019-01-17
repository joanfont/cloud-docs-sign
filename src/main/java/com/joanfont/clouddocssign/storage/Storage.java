package com.joanfont.clouddocssign.storage;

import com.joanfont.clouddocssign.file.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public interface Storage {

    public File get(String id);

    public List<File> listFiles();

    public ByteArrayOutputStream download(File file);

    public File upload(File file, ByteArrayInputStream stream);

    public File newFile();

}

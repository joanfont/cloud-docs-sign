package com.joanfont.clouddocssign.storage;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.joanfont.clouddocssign.file.DropboxFile;
import com.joanfont.clouddocssign.file.File;
import com.joanfont.clouddocssign.file.MimeType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Dropbox implements Storage {

    private DbxClientV2 client;

    public Dropbox(DbxClientV2 client) {
        this.client = client;
    }

    @Override
    public File get(String id) {
        File f = null;
        try {
            FileMetadata fileMetadata = (FileMetadata) this.client.files().getMetadata(id);
            f = this.buildFileFromFileMetadata(fileMetadata);
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return f;
    }

    @Override
    public List<File> listFiles() {
        List<File> files = null;

        try {
            SearchResult result = this.client.files().search("", "*.pdf");
            files = this.processDropboxFiles(result);
        } catch (DbxException e) {
            e.printStackTrace();
        }

        return files;
    }

    @Override
    public ByteArrayOutputStream download(File file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            DbxDownloader<FileMetadata> downloader = this.client.files().download(file.getId());
            downloader.download(outputStream);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }

        return outputStream;
    }

    @Override
    public File upload(File file, ByteArrayInputStream stream) {
        File uploadedFile = null;
        try {
            FileMetadata fileMetadata = this.client.files().uploadBuilder(file.getNameToUpload())
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(stream);

            uploadedFile = this.buildFileFromFileMetadata(fileMetadata);
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }

        return uploadedFile;
    }

    @Override
    public File newFile() {
        return new DropboxFile();
    }

    private List<File> processDropboxFiles(SearchResult result) {
        return result.getMatches()
                .stream()
                .map(s -> (FileMetadata) s.getMetadata())
                .map(this::buildFileFromFileMetadata)
                .collect(Collectors.toList());
    }

    private File buildFileFromFileMetadata(FileMetadata fileMetadata) {
        DropboxFile f = new DropboxFile();

        f.setId(fileMetadata.getId());
        f.setName(fileMetadata.getName());
        f.setPath(fileMetadata.getPathLower());
        f.setMimeType(MimeType.PDF);

        return f;
    }
}

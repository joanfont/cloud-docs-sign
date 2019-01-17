package com.joanfont.clouddocssign.storage;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.joanfont.clouddocssign.file.File;
import com.joanfont.clouddocssign.file.GoogleDriveFile;
import com.joanfont.clouddocssign.file.MimeType;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GoogleDrive implements Storage {

    private static String APPLICATION_NAME = "CloudDocsSign/1.0";

    private static final String LIST_QUERY = String.format("mimeType='%s'", MimeType.PDF);

    private Credential credential;

    private Drive drive;

    private HttpTransport httpTransport;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public GoogleDrive(Credential credential) throws GeneralSecurityException, IOException {
        this.credential = credential;
        this.initializeClient();
    }

    @Override
    public File get(String id) {
        File file = null;
        try {
            this.drive.files().get(id).execute();
            file = this.buildFileFromGoogleDriveFile(
                    this.drive.files().get(id).execute()
            );
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return file;
    }

    @Override
    public List<File> listFiles() {
        List<File> files = null;
        try {
            files = this.doListFiles();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return files;
    }

    @Override
    public File upload(File file, ByteArrayInputStream stream) {
        com.google.api.services.drive.model.File googleFile = this.buildGoogleFileFromFile(file);

        BufferedInputStream bufferedStream = new BufferedInputStream(stream);
        InputStreamContent mediaContent = new InputStreamContent(
                googleFile.getMimeType(),
                bufferedStream
        );

        try {
            googleFile = this.drive.files().create(googleFile, mediaContent).execute();
            file = this.buildFileFromGoogleDriveFile(googleFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return file;
    }

    @Override
    public File newFile() {
        return new GoogleDriveFile();
    }

    public ByteArrayOutputStream download(File file) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {

            this.drive.files().get(file.getId()).executeMediaAndDownloadTo(byteArrayOutputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return byteArrayOutputStream;
    }

    private List<File>doListFiles() throws IOException {
        String pageToken = null;
        List<File> files = new ArrayList<>();

        do {
            FileList fileList = this.fetchFiles(pageToken);
            List<File> processedFiles = this.processGoogleFiles(fileList);
            files.addAll(processedFiles);
            pageToken = fileList.getNextPageToken();
        } while (pageToken != null);

        return files;
    }


    private FileList fetchFiles(String pageToken) throws IOException {
        return this.drive.files()
                .list()
                .setQ(LIST_QUERY)
                .setFields("nextPageToken, files(id, name, mimeType, webContentLink)")
                .setPageToken(pageToken)
                .execute();
    }

    private List<File> processGoogleFiles(FileList fileList) {
        return fileList
                .getFiles()
                .stream()
                .map(this::buildFileFromGoogleDriveFile)
                .collect(Collectors.toList());
    }

    private File buildFileFromGoogleDriveFile(com.google.api.services.drive.model.File googleFile) {
        GoogleDriveFile file = new GoogleDriveFile();

        file
                .setId(googleFile.getId())
                .setName(googleFile.getName())
                .setMimeType(MimeType.fromValue(googleFile.getMimeType()));

        return file;
    }

    private com.google.api.services.drive.model.File buildGoogleFileFromFile(File file) {
        com.google.api.services.drive.model.File googleFile = new com.google.api.services.drive.model.File();
        googleFile.setName(file.getName())
                .setMimeType(file.getMimeType().toString());

        return googleFile;
    }

    private void initializeClient() throws GeneralSecurityException, IOException {
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.drive = new Drive.Builder(
                this.httpTransport,
                JSON_FACTORY,
                this.credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}




package com.joanfont.clouddocssign.lib;

import java.util.Arrays;
import java.util.regex.Pattern;

public class FileUtils {

    public static String appendToFileName(String fileName, String appendPart) {
        String[] fileParts = fileName.split(Pattern.quote("."));
        String[] fileNameParts = Arrays.copyOfRange(fileParts, 0, fileParts.length - 1);
        String fileJoined = String.join(".", fileNameParts);
        String fileWithAppendPart = fileJoined  + appendPart;
        return fileWithAppendPart + "." + fileParts[fileParts.length - 1];
    }

}

package com.dawid.typepython;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Dawid on 17.11.2019 at 14:04.
 */
@Data
@NoArgsConstructor
public class FileContext {
    private static String rootPath;
    private static String targetPath;

    public static void setRootPath(String rootPath) {
        FileContext.rootPath = rootPath;
    }

    public static void setTargetPath(String targetPath) {
        FileContext.targetPath = targetPath;
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static String getTargetPath() {
        return targetPath;
    }

    public static String createTargetPath(String filePath) {
        return targetPath + filePath;
    }
}

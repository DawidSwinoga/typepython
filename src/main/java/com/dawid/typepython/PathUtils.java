package com.dawid.typepython;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Dawid on 27.12.2019 at 17:50.
 */
public class PathUtils {
    public static String getRelativePath(String to, String from) {
        Path pathAbsolute = Paths.get(from);
        Path pathBase = Paths.get(to);
        return pathAbsolute.relativize(pathBase).toString().substring(3);
    }
}

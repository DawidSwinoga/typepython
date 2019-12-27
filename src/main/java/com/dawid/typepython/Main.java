package com.dawid.typepython;

import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
import com.dawid.typepython.symtab.scope.GlobalScope;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Optional.ofNullable;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static final boolean DEBUG = false;
    public static final String TARGET_BUILD_DIRECTORY_NAME = "target";
    public static final String TARGET_BUILD_SRC_DIRECTORY = "src";
    public static final String STANDARD_LIBRARY_DIRECTORY = "stdtpy";
    public static final String STANDARD_LIBRARY_FILE_NAME = "stdtpy.h";
    public static final String CMAKE_RESOURCE_DIR = "build";
    public static final String CMAKE_LISTS_FILE_NAME = "CMakeLists.txt";
    public static final String JAVA_CLASSPATH_SEPARATOR = "/";

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Invalid compiler run. Pass main file as first program argument.");
            System.exit(0);
        }

        Path main = Paths.get(args[0]);
        Path rootPath = ofNullable(main.getParent()).orElse(Paths.get(""));
        FileContext.setRootPath(rootPath.toString() + File.separator);

        if (args.length < 2) {
            FileContext.setTargetPath(FileContext.getRootPath() + TARGET_BUILD_DIRECTORY_NAME + File.separator);
        } else {
            FileContext.setTargetPath(args[1]);
        }

        Paths.get(args[0]);


        Path fileName = Paths.get(args[0]).getFileName();
        String filePath = fileName.toString();
        Compiler.compile(filePath, new ConsoleCodeWriter(filePath), new GlobalScope(filePath), null);
        saveFileToOutput(JAVA_CLASSPATH_SEPARATOR + STANDARD_LIBRARY_DIRECTORY + JAVA_CLASSPATH_SEPARATOR + STANDARD_LIBRARY_FILE_NAME,
                TARGET_BUILD_SRC_DIRECTORY + File.separator + STANDARD_LIBRARY_DIRECTORY + File.separator + STANDARD_LIBRARY_FILE_NAME);
        saveFileToOutput(JAVA_CLASSPATH_SEPARATOR + CMAKE_RESOURCE_DIR + JAVA_CLASSPATH_SEPARATOR + CMAKE_LISTS_FILE_NAME, CMAKE_LISTS_FILE_NAME);
    }


    private static void saveFileToOutput(String resourceName, String target) throws IOException {
        InputStream resourceAsStream = Main.class.getResourceAsStream(resourceName);
        FileUtils.copyInputStreamToFile(resourceAsStream, Paths.get(FileContext.getTargetPath() + target).toFile());
        resourceAsStream.close();
    }

}

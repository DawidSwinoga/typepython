package com.dawid.typepython;

import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
import com.dawid.typepython.symtab.scope.GlobalScope;
import org.apache.commons.io.FileUtils;

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

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Invalid compiler run. Pass main file as first program argument.");
            System.exit(0);
        }

        Path main = Paths.get(args[0]);
        Path rootPath = ofNullable(main.getParent()).orElse(Paths.get(""));
        FileContext.setRootPath(rootPath.toString() + "/");

        if (args.length < 2) {
            FileContext.setTargetPath(FileContext.getRootPath() + "target/");
        } else {
            FileContext.setTargetPath(args[1]);
        }

        Paths.get(args[0]);


        Path fileName = Paths.get(args[0]).getFileName();
        Compiler.compile(fileName.toString(), new ConsoleCodeWriter(fileName.toString()), new GlobalScope(), null);
        saveFileToOutput("/stdtpy/stdtpy.h", "stdtpy/stdtpy.h");
        saveFileToOutput("/build/CMakeLists.txt", "CMakeLists.txt");
    }


    private static void saveFileToOutput(String resourceName, String target) throws IOException {
        InputStream resourceAsStream = Main.class.getResourceAsStream(resourceName);
        FileUtils.copyInputStreamToFile(resourceAsStream, Paths.get(FileContext.getTargetPath() + target).toFile());
        resourceAsStream.close();
    }

}

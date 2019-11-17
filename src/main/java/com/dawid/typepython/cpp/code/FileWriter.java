package com.dawid.typepython.cpp.code;

import com.dawid.typepython.FileContext;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by Dawid on 17.11.2019 at 15:36.
 */
public class FileWriter implements Writer {
    private final String filename;

    public FileWriter(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(String data) {
        try {
            FileUtils.writeStringToFile(Paths.get(FileContext.getTargetPath() + filename).toFile(), data, UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeFileHeader(String header) {
    }
}

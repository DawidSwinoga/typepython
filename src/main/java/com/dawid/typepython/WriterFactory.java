package com.dawid.typepython;

import com.dawid.typepython.cpp.code.FileWriter;
import com.dawid.typepython.cpp.code.Writer;

/**
 * Created by Dawid on 17.11.2019 at 15:30.
 */
public class WriterFactory {
    public static Writer create(String filePath) {
        return new FileWriter(filePath);
    }
}

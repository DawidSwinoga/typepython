package com.dawid.typepython.cpp.code;

/**
 * Created by Dawid on 17.11.2019 at 15:21.
 */
public class ConsoleWriter implements Writer {
    @Override
    public void write(String data) {
        System.out.println(data);
    }

    @Override
    public void writeFileHeader(String header) {
        write(header);
    }
}

package com.dawid.typepython;

import com.dawid.typepython.cpp.code.ConsoleCodeWriter;
import com.dawid.typepython.symtab.scope.GlobalScope;

import java.io.IOException;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        String fileName = "/bubble_sort";
//        String fileName = "/import";
//        String fileName = "/filter";
//        String fileName = "/prime_numbers";
//        String fileName = "/factorial";
//        String fileName = "/map";
        String fileName = "/tuple";
        Compiler.compile(fileName + ".tpy", new ConsoleCodeWriter(fileName), new GlobalScope());
    }

}

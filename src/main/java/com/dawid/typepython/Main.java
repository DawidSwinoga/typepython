package com.dawid.typepython;

import java.io.IOException;

import com.dawid.typepython.cpp.code.ConsoleCodeWriter;

/**
 * Created by Dawid on 18.05.2019 at 15:41.
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        InputStream inputFile = Main.class.getResourceAsStream("/bubble_sort.tpy");
//        InputStream inputFile = Main.class.getResourceAsStream("/prime_numbers.tpy");
//        InputStream inputFile = Main.class.getResourceAsStream("/factorial.tpy");
        Compiler.compile("/bubble_sort.tpy", new ConsoleCodeWriter());
    }

}

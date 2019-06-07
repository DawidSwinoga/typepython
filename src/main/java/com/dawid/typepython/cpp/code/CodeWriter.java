package com.dawid.typepython.cpp.code;

public enum CodeWriter {
    INSTANCE;

    private StringBuilder main = new StringBuilder();

    public void appendMainCode(String code) {
        main.append(code);
    }

    public void writeAll() {
        System.out.println(main.toString());
    }
}

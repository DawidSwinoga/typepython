package com.dawid.typepython.symtab;

import com.dawid.typepython.symtab.type.Type;
import lombok.Value;

/**
 * Created by Dawid on 13.10.2019 at 13:21.
 */
@Value
public class FunctionResult {
    private String displayText;
    private Type type;
    private boolean assignable;

    public FunctionResult(String displayText, Type type, boolean assignable) {
        this.displayText = displayText;
        this.type = type;
        this.assignable = assignable;
    }

    public FunctionResult(String displayText, Type type) {
        this(displayText,type, true);
    }
}

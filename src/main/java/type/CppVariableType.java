package type;

import com.dawid.typepython.cpp.code.literal.BooleanLiteral;
import com.dawid.typepython.cpp.code.literal.UnsupportedLiteralException;
import com.dawid.typepython.symtab.symbol.type.VariableType;
import lombok.Getter;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 08.06.2019 at 02:47.
 */

@Getter
public enum CppVariableType implements VariableType {
    INT("int", "int"),
    LONG("long", "long"),
    DOUBLE("double", "double"),
    FLOAT("float", "float"),
    BOOLEAN("bool", "bool"),
    STRING("string", "string");


    CppVariableType(String pythonName, String cppName) {
        this.pythonName = pythonName;
        this.cppName = cppName;
    }

    private String cppName;
    private String pythonName;

    @Override
    public String getCppNameType() {
        return cppName;
    }

    public static VariableType translate(String pythonName) {
        return stream(values())
                .filter(it -> it.getPythonName().equals(pythonName))
                .findFirst()
                .orElseThrow(() -> new UnsupportedLiteralException(pythonName));
    }
}

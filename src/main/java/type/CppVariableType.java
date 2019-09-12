package type;

import com.dawid.typepython.symtab.symbol.TypedSymbol;
import com.dawid.typepython.symtab.matching.MatchType;
import com.dawid.typepython.symtab.type.VariableType;
import lombok.Getter;

import static java.util.Arrays.stream;

/**
 * Created by Dawid on 08.06.2019 at 02:47.
 */

@Getter
public enum CppVariableType implements VariableType {
    INT("int", "int", true),
    LONG("long", "long long", true),
    DOUBLE("double", "double", true),
    FLOAT("float", "float", true),
    BOOLEAN("bool", "bool", true),
    STRING("string", "string", false),
    VOID(null, "void", false);


    CppVariableType(String pythonName, String cppName, boolean numeric) {
        this.pythonName = pythonName;
        this.cppName = cppName;
        this.numeric = numeric;
    }

    private String cppName;
    private String pythonName;
    private final boolean numeric;

    @Override
    public String getCppNameType() {
        return cppName;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public String getPythonType() {
        return pythonName;
    }

    public static VariableType translate(String pythonName) {
        return stream(values())
                .filter(it -> it.getPythonName().equals(pythonName))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(pythonName));
    }

    @Override
    public MatchType match(TypedSymbol typedSymbol) {
        VariableType variableType = typedSymbol.getVariableType();
        if (this == variableType) {
            return MatchType.FULL;
        }

        if (isNumeric() && variableType.isNumeric()) {
            return MatchType.PARTIAL;
        }

        return MatchType.NONE;
    }
}

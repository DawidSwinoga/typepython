package type;

import com.dawid.typepython.symtab.symbol.type.Type;
import lombok.Getter;

/**
 * Created by Dawid on 08.06.2019 at 02:47.
 */

@Getter
public enum CppType implements Type {
    INT("int"),
    LONG("long"),
    DOUBLE("double"),
    FLOAT("float"),
    BOOLEAN("bool"),
    STRING("string");


    CppType(String name) {
        this.name = name;
    }

    private String name;

    @Override
    public String getNameType() {
        return name;
    }
}

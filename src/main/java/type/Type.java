package type;

import lombok.Getter;

/**
 * Created by Dawid on 08.06.2019 at 02:47.
 */

@Getter
public enum  Type {
    INT("int"),
    LONG("long"),;

    Type(String name) {
        this.name = name;
    }

    private String name;
}

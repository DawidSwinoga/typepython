package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;

import java.io.Serializable;

/**
 * Created by Dawid on 11.09.2019 at 00:34.
 */
public interface TypeMatcher extends Serializable {
    MatchType match(Type t);
}

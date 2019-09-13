package com.dawid.typepython.symtab.type;

import com.dawid.typepython.symtab.matching.MatchType;

/**
 * Created by Dawid on 11.09.2019 at 00:34.
 */
public interface TypeMatcher {
    MatchType match(Type t);
}

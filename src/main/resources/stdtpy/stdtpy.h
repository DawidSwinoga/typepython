#include <algorithm>
#include <iostream>
#include <cmath>
#include <vector>
#include <set>
#include <map>
#include <string>

#pragma once

namespace stdtpy {
    template<class T>
    std::vector<T> filter(std::vector<T> vec, bool (*predicate)(T)) {
        std::vector<T> result(vec.size());
        auto it = std::copy_if(vec.begin(), vec.end(), result.begin(), predicate);
        result.resize(distance(result.begin(), it));
        return result;
    }

    template<class T, class R>
    std::vector<R> map(std::vector<T> vec, R (*mapper)(T)) {
        std::vector<R> result(vec.size());
        std::transform(vec.begin(), vec.end(), result.begin(), mapper);
        return result;
    }

    template<class T>
    std::set<T> filter(std::set<T> set, bool (*predicate)(T)) {
        std::vector<T> result(set.size());
        auto it = std::copy_if(set.begin(), set.end(), result.begin(), predicate);
        result.resize(distance(result.begin(), it));
        return result;
    }

    template<class T, class R>
    std::set<R> map(std::set<T> set, R (*mapper)(T)) {
        std::vector<R> result(set.size());
        std::transform(set.begin(), set.end(), result.begin(), mapper);
        return result;
    }
}
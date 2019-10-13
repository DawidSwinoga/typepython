#include <vector>
#include <algorithm>
#pragma once

namespace stdtpy {
    template <class T> std::vector<T> filter(std::vector<T> vec, bool (*predicate)(T)) {
        std::vector<T> result(vec.size());
        auto it = copy_if(vec.begin(), vec.end(), result.begin(), predicate);
        result.resize(distance(result.begin(), it));
        return result;
    }
}
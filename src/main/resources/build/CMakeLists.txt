cmake_minimum_required(VERSION 3.12)
project(project)

set(CMAKE_CXX_STANDARD 17)
SET(CMAKE_EXE_LINKER_FLAGS "-static")

file(GLOB_RECURSE SRC_FILE
        "src/*.h"
        "src/*.cpp"
        )
add_executable(project ${SRC_FILE})
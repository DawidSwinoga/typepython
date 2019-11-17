cmake_minimum_required(VERSION 3.12)
project(project)

set(CMAKE_CXX_STANDARD 14)
SET(CMAKE_EXE_LINKER_FLAGS "-static")

file(GLOB SRC_FILE
        "*.h"
        "*.cpp"
        )

add_executable(project ${SRC_FILE})
cmake_minimum_required(VERSION 3.22.1)
project("retrobit")

add_library(retrobit SHARED retrobit.cpp)

find_library(log-lib log)
target_link_libraries(retrobit ${log-lib})

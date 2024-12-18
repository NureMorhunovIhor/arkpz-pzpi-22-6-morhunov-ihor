cmake_minimum_required(VERSION 3.30)
project(untitled)

set(CMAKE_CXX_STANDARD 20)

include_directories("C:/mqtt/paho-cpp/include")
include_directories("C:/mqtt/paho-c/include")
include_directories(${PROJECT_SOURCE_DIR}/include)

# Убедитесь, что vcpkg подключен через этот путь
set(CMAKE_TOOLCHAIN_FILE "C:/Users/user/vcpkg/scripts/buildsystems/vcpkg.cmake" CACHE STRING "")

# Найти cpprestsdk
find_package(cpprestsdk CONFIG REQUIRED)

# Создайте исполнимый файл
add_executable(untitled main.cpp)

# Линкуем библиотеки
target_link_libraries(untitled PRIVATE
        cpprestsdk::cpprest
        cpprestsdk::cpprestsdk_zlib_internal
        cpprestsdk::cpprestsdk_brotli_internal
        "C:/mqtt/paho-cpp/lib/paho-mqttpp3-static.lib"
        "C:/mqtt/paho-c/lib/paho-mqtt3a-static.lib"
        "C:/mqtt/paho-c/lib/paho-mqtt3c-static.lib"
        Ws2_32
)

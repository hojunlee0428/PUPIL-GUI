#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_alessiamorin_myapplication_MainActivity_stringFromJNI(JNIEnv* env1, jobject) {
    std::string hello1 = "Hello from C++";
    return env1->NewStringUTF(hello1.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_alessiamorin_myapplication_Main2Activity_stringFromJNI2(JNIEnv* env1, jobject) {
    std::string hello2 = "Hello from C++ 2";
    return env1->NewStringUTF(hello2.c_str());
}
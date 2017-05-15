#include <jni.h>
#include <string>

extern "C"
jstring
Java_com_hwx_camera_1doul_cameradp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++ test!!!";
    return env->NewStringUTF(hello.c_str());
}

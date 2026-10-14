#include <jni.h>
#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "RetroBit", __VA_ARGS__)

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_retrobit_emulator_native_NativeCore_initCore(JNIEnv *env, jobject /*thiz*/, jstring system) {
    const char *s = env->GetStringUTFChars(system, nullptr);
    LOGI("initCore: %s", s);
    env->ReleaseStringUTFChars(system, s);
    // No real emulation core bundled. Return false so the UI falls back
    // to launching an external core (Skyline/Sudachi/Eden/Yuzu).
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_com_retrobit_emulator_native_NativeCore_loadRom(JNIEnv *env, jobject /*thiz*/, jstring path) {
    const char *p = env->GetStringUTFChars(path, nullptr);
    LOGI("loadRom: %s", p);
    env->ReleaseStringUTFChars(path, p);
    return JNI_FALSE;
}

JNIEXPORT jint JNICALL
Java_com_retrobit_emulator_native_NativeCore_runFrame(JNIEnv * /*env*/, jobject /*thiz*/) {
    return 0;
}

JNIEXPORT void JNICALL
Java_com_retrobit_emulator_native_NativeCore_reset(JNIEnv * /*env*/, jobject /*thiz*/) {
}

}

#include "jni.h"
#ifdef BUILD_NDK
#include "android/log.h"
#endif
#ifndef _Included_com_blueocean_jni_InputAdapter
#define _Included_com_blueocean_jni_InputAdapter

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL
Java_com_only_jni_InputAdapter_init(JNIEnv *env, jclass jzz);

JNIEXPORT jboolean JNICALL
Java_com_only_jni_InputAdapter_start(JNIEnv *env, jclass jzz);

JNIEXPORT jboolean JNICALL
Java_com_only_jni_InputAdapter_stop(JNIEnv *env, jclass jzz);

JNIEXPORT void JNICALL
Java_com_only_jni_InputAdapter_getKey(JNIEnv *env, jclass clazz, jobject rawEvent);

JNIEXPORT jint JNICALL
Java_com_only_jni_InputAdapter_openDeviceLocked(JNIEnv *env, jclass clazz, jstring devPath);
#ifdef __cplusplus
}
#endif
#endif


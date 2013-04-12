#define LOG_TAG "JNI_INPUT_ADAPTER"
    
#ifdef BUILD_NDK
#else
#include <utils/Log.h>
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#endif
#include <assert.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include "InputAdapter.h"
#include "CallBackInterface.h"
#include "com_only_jni_InputAdapter.h"

struct fields_t {
	jmethodID onInputAdapterKeyDown;
	jmethodID onInputAdapterKeyUp;
	jmethodID onInputAdapterJoystickChange;
	jmethodID onDeviceAdded;
	jmethodID onOpenEventConfigFile;
};

static fields_t inputCallBackField;

typedef struct tagJNiGlobale_t {
	JNIEnv *env;
	JavaVM *jvm;
} stJniGlobal_t;

static stJniGlobal_t sg_jni_global;
static jclass g_com_only_jni_InputAdapter_class;

static void init_jni_global() {
	memset(&sg_jni_global, 0x00, sizeof(stJniGlobal_t));
}


#ifdef BUILD_NDK
static JNIEnv *_env = NULL;
class AndroidRuntime {
public:
	static JNIEnv* getJNIEnv() {
		if (NULL != sg_jni_global.jvm && NULL == sg_jni_global.env) {
			sg_jni_global.jvm->GetEnv((void **) &sg_jni_global.env,
					JNI_VERSION_1_4);
		}
		return sg_jni_global.env;
	}
};
#else
using namespace android;
#endif

struct RawKeyEvent {
	int scanCode;
	int value;
	int KeyCode;
};

struct RawKeyEvent keyEvent;

static void doOnKeyDown(int scanCode, int value, char *configFileName);
static void doOnKeyUp(int scanCode, int value, char *configFileName);
static void doOnJoystickDataChange(int scanCode, int value, char *configFileName);
static void doOnDeviceAdded(char *devName);
static void doOnOpenEventConfigFile(char *configFileName);

class InputAdapterCallBack : public CallBackInterface {
public:
	void attachEnv() {
        _env = AndroidRuntime::getJNIEnv();
        sg_jni_global.jvm->GetEnv((void **) &_env, JNI_VERSION_1_4);
        sg_jni_global.jvm->AttachCurrentThread(&_env, NULL);
	}

	void detachEnv() {
		//sg_jni_global.jvm->DetachCurrentThread();
	}

	int keyProcess(const RawEvent *rawEvent, char *configFileName) {
		int scanCode = rawEvent->scanCode;
		int value = rawEvent->value;
		LOGE("[%s][%d] ==> raw event scancode = 0X%02X value = 0x%02x", __FUNCTION__, __LINE__, scanCode, value);
		if (scanCode == 0) return 1;
		if (value == 1) {
			doOnKeyDown(scanCode, value, configFileName);
		} else {
			doOnKeyUp(scanCode, value, configFileName);
		}
		return 1; 
	}
	
	int joystickProcess(const RawEvent *rawEvent, char *configFileName) {
		int scanCode = rawEvent->scanCode;
		int value = rawEvent->value;
		doOnJoystickDataChange(scanCode, value, configFileName);
		return 1; 
	}

	void deviceAdded(char *devName) {
		doOnDeviceAdded(devName);
	}

	void openEventConfigFile(char *configFileName) {
		doOnOpenEventConfigFile(configFileName);
	}
};

#ifdef BUILD_NDK
static InputAdapter* mInputAdapter = NULL;
static InputAdapterCallBack* mInputAdapterCallBack;
#else
static sp<InputAdapter> mInputAdapter = NULL;
static sp<InputAdapterCallBack> mInputAdapterCallBack;
#endif

static void doOnKeyDown(int scanCode, int value, char *configFileName) {
	keyEvent.scanCode = scanCode;
	keyEvent.value = value;
#if 1
	mInputAdapterCallBack->attachEnv();
    LOGE("_env = 0X%0X   inputCallBackField.onInputAdapterKeyDown = 0x%0x g_com_only_jni_InputAdapter_class = 0x%0x",
    		_env, inputCallBackField.onInputAdapterKeyDown, g_com_only_jni_InputAdapter_class);
    _env->CallStaticVoidMethod(g_com_only_jni_InputAdapter_class,
                    inputCallBackField.onInputAdapterKeyDown, scanCode, value, _env->NewStringUTF(configFileName));
    mInputAdapterCallBack->detachEnv();
#endif
}

static void doOnKeyUp(int scanCode, int value, char *configFileName) {
	keyEvent.scanCode = scanCode;
	keyEvent.value = value;
#if 1
	mInputAdapterCallBack->attachEnv();
	_env->CallStaticVoidMethod(g_com_only_jni_InputAdapter_class,
                   inputCallBackField.onInputAdapterKeyUp, scanCode, value, _env->NewStringUTF(configFileName));
	mInputAdapterCallBack->detachEnv();
#endif
}

static void doOnJoystickDataChange(int scanCode, int value, char *configFileName) {
#if 1
	mInputAdapterCallBack->attachEnv();
	_env->CallStaticVoidMethod(g_com_only_jni_InputAdapter_class,
                    inputCallBackField.onInputAdapterJoystickChange, scanCode, value, _env->NewStringUTF(configFileName));
	mInputAdapterCallBack->detachEnv();
#endif
}

static void doOnDeviceAdded(char *devName) {
	mInputAdapterCallBack->attachEnv();
	_env->CallStaticVoidMethod(g_com_only_jni_InputAdapter_class,
						inputCallBackField.onDeviceAdded, _env->NewStringUTF(devName));
	mInputAdapterCallBack->detachEnv();
}

static void doOnOpenEventConfigFile(char *configFileName) {
	mInputAdapterCallBack->attachEnv();
	LOGE("[%s][%d] ==> _env = 0x%0x", __FUNCTION__, __LINE__, _env);
	_env->CallStaticVoidMethod(g_com_only_jni_InputAdapter_class,
					inputCallBackField.onOpenEventConfigFile, _env->NewStringUTF(configFileName));
	mInputAdapterCallBack->detachEnv();
}

JNIEXPORT jboolean JNICALL Java_com_only_jni_InputAdapter_init(JNIEnv *env, jclass clazz) {
	init_jni_global();
	env->GetJavaVM(&sg_jni_global.jvm);

#if 1
	jclass thiz = env->FindClass("com/only/jni/InputAdapter");
	if (thiz == NULL) {
		LOGE("[%s][%d] ==> can't find class com_only_jni_InputAdapter", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
#endif
#if 0
	jclass jclazz = (jclass)env->GetObjectClass(thiz);
	if (jclazz == NULL) {
		g_com_only_jni_InputAdapter_class = (jclass) 0;
		LOGE("[%s][%d] ==> can't find class com_only_jni_InputAdpater", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
#endif	

	g_com_only_jni_InputAdapter_class = (jclass)env->NewGlobalRef(thiz);

	inputCallBackField.onInputAdapterKeyDown = env->GetStaticMethodID(thiz, "onInputAdapterKeyDown", "(IILjava/lang/String;)V");
	if (inputCallBackField.onInputAdapterKeyDown == NULL) {
		LOGE("[%s][%d] ==> can't get onInputAdapterKeyDown from com.only.jni.InputAdapter class file", __FUNCTION__, __LINE__);
		return JNI_TRUE;
	}
	inputCallBackField.onInputAdapterKeyUp = env->GetStaticMethodID(thiz, "onInputAdapterKeyUp", "(IILjava/lang/String;)V");
	if (inputCallBackField.onInputAdapterKeyUp == NULL) {
		LOGE("[%s][%d] ==> can't get onInputAdapterKeyUp from com.only.jni.InputAdapter class file", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	inputCallBackField.onInputAdapterJoystickChange = env->GetStaticMethodID(thiz, "onInputAdapterJoystickChange", "(IILjava/lang/String;)V");
	if (inputCallBackField.onInputAdapterJoystickChange == NULL) {
		LOGE("[%s][%d] ==> can't get onInputAdapterJoystickChange from com.only.jni.InputAdapter class file", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	inputCallBackField.onDeviceAdded = env->GetStaticMethodID(thiz, "onDeviceAdded", "(Ljava/lang/String;)V");
	if (inputCallBackField.onDeviceAdded == NULL) {
		LOGE("[%s][%d] ==> can't get onInputAdapterJoystickChange from com.only.jni.InputAdapter class file", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	inputCallBackField.onOpenEventConfigFile = env->GetStaticMethodID(thiz, "onOpenEventConfigFile", "(Ljava/lang/String;)V");
	if (inputCallBackField.onOpenEventConfigFile == NULL) {
		LOGE("[%s][%d] ==> can't get onOpenEventConfigFile from com.only.jni.InputAdapter class file", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}

	mInputAdapter = InputAdapter::create();
	mInputAdapterCallBack = new InputAdapterCallBack();
	mInputAdapter->getKeyManager()->registerCallBackInterface(mInputAdapterCallBack);
	mInputAdapter->getJoystick()->registerCallBackInterface(mInputAdapterCallBack);
	mInputAdapter->getDeviceManager()->registerCallBackInterface(mInputAdapterCallBack);
	
	return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_only_jni_InputAdapter_start(JNIEnv *env, jclass clazz) {
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	mInputAdapter->start();
	return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_com_only_jni_InputAdapter_stop(JNIEnv *env, jclass clazz) {
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return JNI_FALSE;
	}
	
	mInputAdapter->stop();
	return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_com_only_jni_InputAdapter_getKey(JNIEnv *env, jclass clazz, jobject rawEvent) {
	jclass jclazz = env->FindClass("com/blueocean/jni/RawEvent");
	if (jclazz == NULL) {
		LOGE("[%s][%d] ==> could't find the com/blueocean/jni/RawEvent class", __FUNCTION__, __LINE__);
		return;
	}

	jfieldID scanCode = env->GetFieldID(jclazz, "scanCode", "I");
	jfieldID value = env->GetFieldID(jclazz, "value", "I");

	env->SetIntField(rawEvent, scanCode, keyEvent.scanCode);
	env->SetIntField(rawEvent, value, keyEvent.value);
}

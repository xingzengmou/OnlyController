LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional eng

LOCAL_SRC_FILES := \
        EventHub.cpp \
        InputAdapter.cpp \
        com_only_jni_InputAdapter.cpp

LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
    libcutils \
    libutils \
    libhardware \
    libhardware_legacy \
    libskia \
    libgui \
    libui \
    
LOCAL_LDLIBS := -llog

LOCAL_C_INCLUDES := \
    external/skia/include/core \
    $(LOCAL_PATH)/
    

LOCAL_CFLAGS += -DBUILD_NDK

LOCAL_PRELINK_MODULE := false

LOCAL_MODULE:= libjni_input_adapter

include $(BUILD_SHARED_LIBRARY)
#include $(LOCAL_PATH)/Android.mk.bk
############################################################
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng
LOCAL_MODULE:=libjni_console
LOCAL_SRC_FILES:= \
  termExec.cpp
LOCAL_SHARED_LIBRARIES := \
	libutils
LOCAL_LDLIBS := -ldl -llog
LOCAL_STATIC_LIBRARIES :=
LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE)
# No special compiler flags.
LOCAL_CFLAGS += -DBUILD_NDK
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#opencv
OPENCVROOT:= /home/duongtd/OpenCV-android-sdk
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on
OPENCV_LIB_TYPE:=SHARED
include ${OPENCVROOT}/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES := MyLibrary.cpp
LOCAL_LDLIBS += -llog -llog -landroid
LOCAL_LDLIBS += -ljnigraphics
LOCAL_MODULE := MyLibrary

include $(BUILD_SHARED_LIBRARY)


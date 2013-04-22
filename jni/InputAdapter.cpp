#define LOG_TAG "INPUT_ADAPTER"
     
#include "InputAdapter.h"
#include "debug.h"
#ifdef BUILD_NDK
#include "pthread.h"
#endif
#include "errno.h"

#ifdef BUILD_NDK
#else
namespace android {
#endif
//-----InputAdapter-----

void InputAdapter::release() {
	mEventHub->release();
}

int InputAdapter::init() {
	mJoystick = Joystick::create();
	mKeyManager = KeyManager::create();
	mEventHub = EventHub::create();
	mDeviceManager = DeviceManager::create();
	mEventHub->setInputAdapter(this);
	memset(confFileName, 0, sizeof(confFileName));
#ifdef BUILD_NDK
	thread_exit = 0;
#else
	mInputAdapterThread = new InputAdapterThread(this);
	mInputAdapterNotifierThread = new InputAdapterNotifierThread(this);
	LOGE("[%s][%d] ==> init finished", __FUNCTION__, __LINE__);
#endif
	return 1;
}

void *getEventThread(void *p) {
	InputAdapter *mInputAdapter = (InputAdapter*) p;
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return NULL;
	}
	while (0 == mInputAdapter->thread_exit) {
		memset(mInputAdapter->mEventBuffer, 0, mInputAdapter->EVENT_BUFFER_SIZE);
		mInputAdapter->getEventHub()->getEvents(0, mInputAdapter->mEventBuffer, mInputAdapter->EVENT_BUFFER_SIZE, mInputAdapter->confFileName);
		mInputAdapter->processRawEventLocked(mInputAdapter->mEventBuffer, mInputAdapter->confFileName);
	}
	return NULL;
}

void *monitorNotoifierThread(void *p) {
	InputAdapter *mInputAdapter = (InputAdapter*) p;
	if (mInputAdapter == NULL) {
		LOGE("[%s][%d] ==> mInputAdapter is NULL", __FUNCTION__, __LINE__);
		return NULL;
	}
	while(0 == mInputAdapter->thread_exit) {
		mInputAdapter->getEventHub()->readNotifyLocked();
	}
	return NULL;
}

void InputAdapter::openEventConfigFile(char *configFileName) {
	LOGE("[%s][%d] ==> configfile = %s", __FUNCTION__, __LINE__, configFileName);
	mDeviceManager->openEventConfigFile(configFileName);
}

void InputAdapter::deviceAdded(char *devName) {
	mDeviceManager->deviceAdded(devName);
}

int InputAdapter::openDeviceLocked(char *devicePath) {
	return this->mEventHub->openDeviceLocked(devicePath);
}

int InputAdapter::start() {
	//mEventHub->scanInput();
#ifdef BUILD_NDK
	pthread_t pid;
	int ret = pthread_create(&pid, NULL, getEventThread, this);
	if (ret) {
		LOGE("[%s][%d] ==> create getEventThread error (%s)", __FUNCTION__, __LINE__, strerror(errno));
		return -1;
	}
	ret = pthread_create(&pid, NULL, monitorNotoifierThread, this);
	if (ret) {
		LOGE("[%s][%d] ==> create monitorNotifierThread error (%s)", __FUNCTION__, __LINE__, strerror(errno));
		return -1;
	}

	return 1;
#else
	int ret = mInputAdapterThread->run("InputAdapterThread", PRIORITY_URGENT_DISPLAY);
	if (ret) {
		 LOGE("[%s][%d] ==> Could not start InputAdapterThread thread due to error %d.", __FUNCTION__, __LINE__, ret);
		 mInputAdapterThread->requestExit();
		 return -1;
	}
	ret = mInputAdapterNotifierThread->run("InputAdapterNotifierThread", PRIORITY_URGENT_DISPLAY);
	if (ret) {
		 LOGE("[%s][%d] ==> Could not start InputAdapterNotifierThread thread due to error %d.", __FUNCTION__, __LINE__, ret);
		 mInputAdapterNotifierThread->requestExit();
		 return -1;
	}

	return OK;
#endif
}

int InputAdapter::stop() {
#ifdef BUILD_NDK
	thread_exit = 1;
	return 1;
#else
    int result = mInputAdapterThread->requestExitAndWait();
    if (result) {
        LOGE("[%s][%d] ==> Could not stop InputReader thread due to error %d.", __FUNCTION__, __LINE__, result);
    }

    return OK;
#endif
}

#ifdef BUILD_NDK
Joystick* InputAdapter::getJoystick() {
	return mJoystick;
}

KeyManager* InputAdapter::getKeyManager() {
	return mKeyManager;
}

EventHub* InputAdapter::getEventHub() {
	return mEventHub;
}

DeviceManager* InputAdapter::getDeviceManager() {
	return mDeviceManager;
}
#else
sp<Joystick> InputAdapter::getJoystick() {
	return mJoystick;
}

sp<KeyManager> InputAdapter::getKeyManager() {
	return mKeyManager;
}

sp<EventHub> InputAdapter::getEventHub() {
	return mEventHub;
}
#endif

void InputAdapter::loopOnce() {
#ifdef BUILD_NDK
#else
	AutoMutex _l(mLock);
	memset(mEventBuffer, 0, EVENT_BUFFER_SIZE);
	mEventHub->getEvents(0, mEventBuffer, EVENT_BUFFER_SIZE, confFileName);
	processRawEventLocked(mEventBuffer, confFileName);
#endif
}

void InputAdapter::dumpRawEvent(const RawEvent *event) {
	LOGE("[%s][%d] ==> event.type = 0x%02x, event.scancode = 0x%02x event.value = 0x%02x event.deviceid = %d",
			__FUNCTION__, __LINE__, event->type, event->scanCode, event->value, event->deviceId);
}

void InputAdapter::processRawEventLocked(const RawEvent *eventBuffer, char *configFileName) {
	for (size_t i = 0; i < eventBuffer->count; i ++) {
#if DEBUG_SWITCH
		dumpRawEvent(eventBuffer);
#endif
		switch (eventBuffer->type) {
		case EV_KEY:
			LOGE("[%s][%d] ==> processKeys code = %d  configFileName = %s", __FUNCTION__, __LINE__, eventBuffer->scanCode, configFileName);
			mKeyManager->processKeys(eventBuffer, configFileName);
			break;
		case EV_ABS:
			if (eventBuffer->scanCode == 0 || eventBuffer->scanCode == 1
					|| eventBuffer->scanCode == 2 || eventBuffer->scanCode == 5) {
				//ABS_X = 0, ABS_Y = 1, ABS_Z = 2, ABS_RZ = 5
				//右遥感
				//向右是ABS_Z = 02   0X7F为中心,向右就>7F,向左小于7F
				//向上时ABS_RZ = 05  0x7f为中心，向下〉7F，向上<7F
				//左遥感
				//横向：ABS_X = 0X00  0X7F为中心,向右就>7F,向左小于7F
				//纵向：ABS_Y = 0X01  0x7f为中心，向下〉7F，向上<7F
				mJoystick->joystickProcess(eventBuffer, configFileName);
			}
			break;
		}
		eventBuffer ++;
	}
}


#ifdef BUILD_NDK

#else
//---- InputAdapterThread-----
InputAdapter::InputAdapterThread::InputAdapterThread(sp<InputAdapter> adapter):
	Thread(true), mInputAdapter(adapter) {
	
}

InputAdapter::InputAdapterThread::~InputAdapterThread() {
	
}

bool InputAdapter::InputAdapterThread::threadLoop() {
	mInputAdapter->loopOnce();
	return true;
}

//---- InputAdapterNotifierThread-----
InputAdapter::InputAdapterNotifierThread::InputAdapterNotifierThread(sp<InputAdapter> adapter):
	Thread(true), mInputAdapter(adapter) {
	
}

InputAdapter::InputAdapterNotifierThread::~InputAdapterNotifierThread() {
	
}

bool InputAdapter::InputAdapterNotifierThread::threadLoop() {
	LOGE("[%s][%d] ==> readNotifyLocked", __FUNCTION__, __LINE__);
	mInputAdapter->getEventHub()->readNotifyLocked();
	return true;
}

};
#endif

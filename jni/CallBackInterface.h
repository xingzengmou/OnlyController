#ifndef __CALLBACK_INTERFACE_H__
#define __CALLBACK_INTERFACE_H__

#include "EventHub.h"

#ifdef BUILD_NDK
class CallBackInterface {
#else
namespace android {

class CallBackInterface  : public virtual RefBase  {
#endif
public:
	virtual void openEventConfigFile(char *configFileName) = 0;
	virtual void deviceAdded(char *devName) = 0;
	virtual int keyProcess(const RawEvent *rawEvent, char *configFileName) = 0;
	virtual int joystickProcess(const RawEvent *rawEvent, char *configFileName) = 0;
};

#ifdef BUILD_NDK
#else
};
#endif

#endif

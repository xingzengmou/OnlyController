#ifndef __DEVICE_MANAGER_H__
#define __DEVICE_MANAGER_H__

#include "CallBackInterface.h"

class DeviceManager {
public:
	CREATE_FUNC(DeviceManager);
	int init() {
		mCallBackInterface = NULL;
		return 1;
	}
	
	void registerCallBackInterface(CallBackInterface *callBackInterface) {
		mCallBackInterface = callBackInterface;
	}
	
	void deviceAdded(char *devName) {
		mCallBackInterface->deviceAdded(devName);
	}
private:
	CallBackInterface *mCallBackInterface;
};

#endif
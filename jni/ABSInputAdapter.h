#ifndef __ABS_INPUT_ADAPTER_H__
#define __ABS_INPUT_ADAPTER_H__

class ABSInputAdapter {
public:
	virtual void deviceAdded(char *devName) = 0;
};

#endif

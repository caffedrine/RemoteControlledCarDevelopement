#ifndef DRV8835_Driver_h
#define DRV8835_Driver_h

#include "Arduino.h"
#include <esp32-hal-ledc.h>

class DRV8835
{

public:
	typedef enum DIRECTIONS
	{
		FORWARD = 1,
		BACKWARD = -1,	//it's easier to do speed*direction to get right speed value
	}DIRECTIONS;

	void init();

	void attachM1Pin(uint8_t en, uint8_t ph, bool rev = false);
	void attachM2Pin(uint8_t en, uint8_t ph, bool rev = false);

	void setM1Speed(int speed);
	void setM2Speed(int speed);

	void brake();

private:
	bool initialised = false;
	uint8_t M1en = 0, M1enCH = 0,
			M1ph = 0, M1phCH = 1,
			M2en = 0, M2enCH = 2,
			M2ph = 0, M2phCH = 3;
	bool M1rev = false, M2rev = false;


	void ledcAnalogWrite(uint8_t channel, int  value, int valueMax = 4095);

};

#endif

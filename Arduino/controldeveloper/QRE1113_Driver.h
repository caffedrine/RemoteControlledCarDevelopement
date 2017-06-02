#ifndef QRE1113_Driver_h
#define QRE1113_Driver_h

#include <Arduino.h>

class QRE1113
{
public:
	int sensorPin = 0;
	int currVal = 0, lastVal = 0;
	int currSteps = 0, lastSteps = 0;

	QRE1113(int pin)
	{
		this->sensorPin = pin;
		pinMode(pin, INPUT);
	}

	void update()
	{
		this->lastSteps = this->currSteps;
		this->lastVal   = this->currVal;

		this->currVal = analogRead(this->sensorPin);

		if(this->currVal < 500)
			this->currVal = 1;
		else if(this->currVal > 2150)
			this->currVal = 0;
		else
			this->currVal = this->lastVal;

		if(this->lastVal != this->currVal)
			this->currSteps += this->currVal;
	}

	int getSteps()
	{
		return this->currSteps;
	}
};

#endif

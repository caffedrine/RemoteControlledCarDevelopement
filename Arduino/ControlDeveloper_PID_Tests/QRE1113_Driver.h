#ifndef QRE1113_Driver_h
#define QRE1113_Driver_h

#include <Arduino.h>

class QRE1113
{
public:
	int sensorPin = 0;
	int currVal = 0, lastVal = 0;
	long currSteps = 0, lastSteps = 0;

	//This mean that sensors have to be calibrated on every car
	int minUnder = 2500; //under which value will be considered 0 (0-4095)
	int maxUp = 3000;	 //values up to this variable will be considered 1

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

		if(this->currVal < minUnder)
			this->currVal = 1;
		else if(this->currVal > maxUp)
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

#include "DRV8835_Driver.h"

void DRV8835::init()
{
	//Setting up pins as OUTPUTS
	pinMode(this->M1en, OUTPUT);
	pinMode(this->M2en, OUTPUT);
	pinMode(this->M1ph, OUTPUT);
	pinMode(this->M2ph, OUTPUT);

	//Configuring ledc for motor 1
	//M1 EN pin
	ledcSetup(this->M1enCH, 5000, 13); 		//Channel 0; 5000Hz; 13 bits resolution
	ledcAttachPin(this->M1en, this->M1enCH);
	//M1 PH pin
	ledcSetup(this->M1phCH, 5000, 13);
	ledcAttachPin(this->M1ph, this->M1phCH);

	//Configuring ledc for motor 2
	//M2 EN pin
	ledcSetup(this->M2enCH, 5000, 13);
	ledcAttachPin(this->M2en, this->M2enCH);
	//M2 PH pin
	ledcSetup(this->M2phCH, 5000, 13);
	ledcAttachPin(this->M2ph, this->M2phCH);

	//make sure motors are off
	this->setM1Speed(0);
	this->setM2Speed(0);

	//Go out in case motor pins were not attached
	if(this->M1en == 0 || this->M2en == 0 || this->M1ph == 0 || this->M2ph == 0)
		return;

	//we need to run init just once
	this->initialised = true;
}

void DRV8835::attachM1Pin(uint8_t en, uint8_t ph, bool rev)
{
	this->M1en = en;
	this->M1ph = ph;
	this->M1rev = rev;
}

void DRV8835::attachM2Pin(uint8_t en, uint8_t ph, bool rev)
{
	this->M2en = en;
	this->M2ph = ph;
	this->M2rev = rev;
}

void DRV8835::setM1Speed(int speed)
{
	//if reversed just change speed
	if (this->M1rev)
		speed *= -1;

	//Let me be clear: [en = HIGH, ph = LOW] = forward
 	//				   [en = LOW, ph = HIGH] = backward

	if(speed < 0)
	{
		//We can only write positive values
		speed *= -1;

		//This mean that motor shall run backward
		this->ledcAnalogWrite(M1enCH, 0);
		this->ledcAnalogWrite(M1phCH, speed);
	}
	else
	{
		this->ledcAnalogWrite(M1phCH, 0);
		this->ledcAnalogWrite(M1enCH, speed);
	}
}

void DRV8835::setM2Speed(int speed)
{
	//if reversed just change speed
	if (this->M2rev)
		speed *= -1;

	//Let me be clear: [en = HIGH, ph = LOW] = forward
 	//				   [en = LOW, ph = HIGH] = backward

	if(speed < 0)
	{
		//We can only write positive values
		speed *= -1;

		//This mean that motor shall run backward
		this->ledcAnalogWrite(M2enCH, 0);
		this->ledcAnalogWrite(M2phCH, speed);
	}
	else
	{
		this->ledcAnalogWrite(M2phCH, 0);
		this->ledcAnalogWrite(M2enCH, speed);
	}
}

void DRV8835::ledcAnalogWrite(uint8_t channel, int value, int max)
{
	// calculate duty, 8191 from 2 ^ 13 - 1
	uint32_t duty = (8191 / max) * min(value, max);

	// write duty to LEDC
	ledcWrite(channel, duty);
}

void DRV8835::brake()
{
	this->setM1Speed(0);
	this->setM2Speed(0);
}
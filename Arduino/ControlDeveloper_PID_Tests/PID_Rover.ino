#include <Arduino.h>
#include <esp32-hal-dac.h>

#include "DRV8835_Driver.h"
#include "QRE1113_Driver.h"
#include "my_util.h"

//Motor object
DRV8835 motors;

//Encoders (IR Sensor: QRE1113)
QRE1113 leftEncoder(4);
QRE1113 rightEncoder(34);

//Some prototypes
void printEncoderSpeed(int timeBase = 1000);
void updateSlaveMotor(int ms = 10);

#define motor_min 2600
#define motor_max 3900

void setup()
{
	//Starting serial interface
	Serial.begin(115200);
	Serial.println("---STARTING---");

	//initialize motors
	motors.attachM1Pin(26, 27);	// en, ph	-> left motor
	motors.attachM2Pin(25, 14, true/* reverse direction in case you don't want to switch wires*/);	// en, ph	-> right motor
	motors.init();	//init motors and pins
	delay(1500);	//To make sure motors are stopped
}

int power = 0;	//power is received from client [0-100]
int speed = 0;	//max 100, min -100
int slaveSpeed = 0;
int atenuator = 400;
void loop()
{
	//Update encoders values
	leftEncoder.update();
	rightEncoder.update();

	//Get speed from serial and map to correct intervals
	if (Serial.available())
	{
		//init encoders
		leftEncoder.currSteps = 0;
		rightEncoder.currSteps = 0;

		power = to_int(Serial.readString());
		power = map(power, 0, 100, 40, 80);	//for this interval PID algorithm is ideal                                                                                                                           power = 50;
		if (power == 0)
		{
			speed = 0;
			slaveSpeed = 0;
			motors.brake();
			delay(1000);	//give time to stop
		}
		else
		{
			//Maping readed value to 13 bits. Starts with ~2500 as it is the value motors starts to rotate
			if (power < 0)
				speed = map(power, -100, 0, motor_max*-1, motor_min*-1);
			else
				speed = map(power, 0, 100, motor_min, motor_max);

			//Init slave speed with the same value and then correct error on feedback loop
			slaveSpeed = speed;
		}
	}

	//We set th same speed on both motors so we want same speed at output
	motors.setM1Speed(speed);	//Update motor 1 (left) and adapt motor 2 by this speed
	//motors.setM2Speed(speed);
	updateSlaveMotor(0);		//updating slave motor

	//Print speed periodically
	//printPeriodicData("Speed: " + to_string(speed) + "  Slave speed: " + to_string(slaveSpeed), 1000);
	printEncoderSpeed(250);

	

	//*/
}

void printEncoderSpeed(int timeBase)
{
	static int prevMillis = 0;
	static int L_prev_steps = 0, R_prev_steps = 0;

	if (millis() - prevMillis >= timeBase)
	{
		prevMillis = millis();
		Serial.println("L: " + to_string(leftEncoder.currSteps - L_prev_steps) + "/t (" + to_string(speed) + ") Count: " + to_string(leftEncoder.currSteps)
			+ "\nR: " + to_string(rightEncoder.currSteps - R_prev_steps) + "/t (" + to_string(slaveSpeed) + ") Count: " + to_string(rightEncoder.currSteps)
			+ " ERR: " + to_string(rightEncoder.currSteps - leftEncoder.currSteps));
		
		//Updating with last values. We'll need'em next time.
		L_prev_steps = leftEncoder.currSteps;
		R_prev_steps = rightEncoder.currSteps;
	}
}

void updateSlaveMotor(int ms)
{
	static int prevMillis = 0;
	if (millis() - prevMillis > ms)
	{
		prevMillis = millis();
		//We need a buffer to absorb high variations
		int buffer = 350;	//value calibrated with half of speed (50) - this is a magic value to get a stable movement - this is an empirical value
		buffer = map(power, 0, 100, 200, 450 );	//mapping buffer for every value

		//Use these values to prevent whel droping from max_motor to min_motor;
		//In other words, if master speed is 1000, slave speed bust be [800, 1200] but not outside.
		int min_variation = speed - 400; if (min_variation < motor_min) min_variation = motor_min - 300;
		int max_variation = speed + 400; if (max_variation > motor_max) max_variation = motor_max + 100;

		//Try to change R_motor speed in respect with L_motor
		if (rightEncoder.currSteps < leftEncoder.currSteps)
		{
			//We have to increase R_motor
			//first, get the difference
			int error = leftEncoder.currSteps - rightEncoder.currSteps;

			//Now use error to change speed
			slaveSpeed += error;
			if (slaveSpeed > max_variation)
			{
				//speed -= error;
				slaveSpeed = max_variation - buffer; // -> this value is magic ^_^
			}
			if (speed < 0)	//if speed  <  0 we can't decreasse speed outside min and max
			{
				if (slaveSpeed > (min_variation*-1))
					slaveSpeed = (min_variation*-1) - buffer;
			}
		}	

		else if (rightEncoder.currSteps > leftEncoder.currSteps)
		{
			//We have to decreassse R_motor
			//first, get the difference
			int error = rightEncoder.currSteps - leftEncoder.currSteps;

			//Now use error to change the slave speed
			slaveSpeed -= error;
			if (slaveSpeed < (max_variation*-1))
			{
				slaveSpeed = (max_variation*-1) + buffer;
			}
			if (speed > 0)
			{
				if (slaveSpeed < min_variation)
					slaveSpeed = min_variation + buffer;
			}
		}
	}
	//Now we have to send value calculated to motor
	motors.setM2Speed(slaveSpeed);
}


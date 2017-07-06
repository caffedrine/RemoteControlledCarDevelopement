#ifndef PID_H
#define PID_H

#define motor_min 2600	//the minimul value motors starts to rotate
#define motor_max 3900	//maximum speed which can be send to motors

//Motors object
DRV8835 motors;		

//Encoders (IR Sensor: QRE1113)
QRE1113 leftEncoder(32);
QRE1113 rightEncoder(34);

int power = 0;			//power is received from client [0-100]
int speed = 0;			//max 100, min -100. Positive vals means FORWARD and negative values mean BACKWARD
int slaveSpeed = 0;		//slave speed

// Prototypes
void printEncoderSpeed(int timeBase = 1000);
void updateSlaveMotor(int ms = 0);
void computeSteps(int steps, int m1Dir = DRV8835::FORWARD, int m2 = -1, int m2Dir = DRV8835::FORWARD);
void mapSpeed(int power);

/**
	@brief Print data periodically! Easy to observe errors when building control algoritm.
	@param[in] timeBase This is time in miliseconds how often you want to display data to serial console!
*/
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

/**
	@brief This is the function which correct the errors of slave motor!
	In other words, M1 is automatically updated with speed given by user. M2 speed is adjustes so that keeps the same speed with master motor (M1).
	This must be done because motors won't rotate with the same speed for same analog output values.

	@param[in] ms This is how often you want to check for errors on movement and try to correct them. Default value is 0. Check and correct errors as fast as possible.
	For bigger steps this value mai be increased to 10ms for example.
	PS: miliseconds!!
*/
void updateSlaveMotor(int ms)
{
	int changeSlaveSpeedFlag = 1, changeMainSpeedFlag = 1;	//by default, direction is 1, FORWARD
	static int prevMillis = 0;
	if (millis() - prevMillis > ms)
	{
		prevMillis = millis();
		
		//Sometime slave motor mai have to rotate in oposite direction
		if (slaveSpeed < 0)
		{
			slaveSpeed *= -1;		//make it positive
			changeSlaveSpeedFlag = -1;	//set a flag to know that we changed the direction
		}

		if (speed < 0)	//we work only with positive speeds - we'll reverse that speed at the end
		{
			changeMainSpeedFlag = -1;
			speed *= -1;
		}

		//We need a buffer to absorb high variations
		int buffer = 350;	//value calibrated with half of speed (50) - this is a magic value to get a stable movement - this is an empirical value
		buffer = map(power, 0, 100, 200, 450 );	//mapping buffer for every value

		//Use these values to prevent whel droping from max_motor to min_motor;
		//In other words, if master speed is 1000, slave speed bust be [800, 1200] but not outside.
		int min_variation = speed - 400; if (min_variation < motor_min) min_variation = motor_min - 100;
		int max_variation = speed + 400; if (max_variation > motor_max) max_variation = motor_max + 100;

		//Try to change R_motor speed in respect with L_motor
		if (rightEncoder.currSteps < leftEncoder.currSteps)
		{
			//We have to increase R_motor
			//first, get the difference
			int error = leftEncoder.currSteps - rightEncoder.currSteps;

			//Now use error to change speed
			slaveSpeed += error;

			if (slaveSpeed > max_variation)		//if slaveSpeed is going up to much
			{
				//speed -= error;
				slaveSpeed = max_variation - buffer; // -> this value is magic ^_^
			}
		}	
		else if (rightEncoder.currSteps > leftEncoder.currSteps)
		{
			//We have to decreassse R_motor
			//first, get the difference
			int error = rightEncoder.currSteps - leftEncoder.currSteps;

			//Now use error to change the slave speed
			slaveSpeed -= error;

			if (slaveSpeed <  min_variation)
			{
				slaveSpeed = min_variation + buffer;
			}
		}
	}
	//Now we have to send value calculated to motor
	
	//However...
	if (speed == 0)
		slaveSpeed = 0;

	//Reverse changes as initially
	speed		*= changeMainSpeedFlag;
	slaveSpeed	*= changeSlaveSpeedFlag;
	motors.setM2Speed(slaveSpeed);
}

/**
	@brief Translate the power received from user and map, translate and corelate with local variables speed and slaveSpeed
	@param[in] power This is the power received from uset. It have to be in interval [0-100], where 0 means breake and 100 means full speed
	This function should be called everytime a command from user is received
*/
void mapSpeed(int power)
{
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
		{
			power = map(power, -100, 0, -80, -40);	//for this interval PID algorithm is ideal
			speed = map(power, -100, 0, motor_max*-1, motor_min*-1);
		}
		else
		{
			power = map(power, 0, 100, 40, 80);	//for this interval PID algorithm is ideal
			speed = map(power, 0, 100, motor_min, motor_max);
		}

		//Init slave speed with the same value and then correct error on feedback loop
		slaveSpeed = speed;
	}
}

/**
	@brief This function compute a number of steps in directions given
	By default compute steps only for motor M1. But you can compute steps in paralel in both motors if m2 != -1
	No status updates when using this function!
	@param[in] steps Number of steps to be executed
	@param[in] m1Dir Optional parameter to specify direction of M1
	@param[in] m2 When != -1, steps will also be computed on second motor, in parallel with M1
	@param[in] m2Dir Optional parameter to specify direction of motor2
	The direction should be provided like this: DRV8835::DIRECTIONS::FORWARD, DRV8835::DIRECTIONS::BACKWARD
*/
void computeSteps(int steps, int m1Dir, int m2, int m2Dir)
{
	if (steps <= 0)
		return;

	//reset encoders
	leftEncoder.currSteps = 0;
	rightEncoder.currSteps = 0;

	//Perform direction with that speed
	power = 5;			//lower speed to prevent inertia
	mapSpeed(power);

	//Keep motor rotating until we reach the target
	do
	{
		motors.setM1Speed(speed*m1Dir);
		if (m2 != -1)
		{
			slaveSpeed *= m2Dir;
			updateSlaveMotor(0);		//use PID to stabilize in respect with main motor - why not
			slaveSpeed *= m2Dir;		//reverse speed so next time won't be negative
		}
		leftEncoder.update();
		rightEncoder.update();

	} while (leftEncoder.currSteps < steps && rightEncoder.currSteps < steps);
	
	//The rest of the code apply if two motors were set. If you wanted to compute speed for only one motor, then we stop here! Job was done
	if (m2 == -1)	//no m2
	{
		//brake first
		motors.setM1Speed(speed*m1Dir*-1);
		delay(25);
		motors.setM1Speed(0);

		mapSpeed(0);	//set speed to 0 as it was changed
		return;
	}

	//At least one of motors finished the job. 
	//Next we check if both motors made required steps else finish the job on correct motor
	
	//Both motors made theis job so brake them bots
	if (leftEncoder.currSteps == steps && rightEncoder.currSteps == steps)	// 0.000001 probability to reach this but just in case
	{
		motors.setM1Speed(speed*m1Dir*-1);	//force brake to prevent intertia
		motors.setM2Speed(speed*m2Dir*-1);
		delay(25);						//motors needs some time to stop
		motors.brake();					//act brakes
	}
	else
	{
		//We need to brake the motor which finished the job
		if (leftEncoder.currSteps == steps)
		{
			motors.setM1Speed(speed*m1Dir*-1);
			delay(25);
			motors.setM1Speed(0);
		}

		if (rightEncoder.currSteps == steps)
		{
			motors.setM2Speed(speed*m2Dir*-1);
			delay(25);
			motors.setM2Speed(0);
		}
	}

	//Check if right motor have to do some more steps
	if (rightEncoder.currSteps < steps)
	{
		//make some steps using right motor
		do
		{
			motors.setM2Speed(speed*m2Dir);
			leftEncoder.update();
			rightEncoder.update();

		} while (rightEncoder.currSteps < steps);

		//brake time
		motors.setM2Speed(speed*m2Dir*-1);
		delay(25);
		motors.setM2Speed(0);
	}

	//Check if left motor have to do some more steps
	if (leftEncoder.currSteps < steps)	//if left motors have to do some more steps
	{
		//left motor need so make some more steps
		do
		{
			//set speed on motor
			motors.setM1Speed(speed*m1Dir);

			//It is recommended to update booth encoders
			leftEncoder.update();
			rightEncoder.update();

		} while (leftEncoder.currSteps < steps);
		
		//brake after job is done
		motors.setM1Speed(speed*m1Dir*-1);
		delay(25);
		motors.setM1Speed(0);
	}

	//Make power low again
	mapSpeed(0);//*/
}
#endif

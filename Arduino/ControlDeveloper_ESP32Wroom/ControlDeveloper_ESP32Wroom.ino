#include <Arduino.h>
#include <esp32-hal-dac.h>
#include <WiFi.h>

//Enable this to get info via serial port
#define DEBUG true

#include "connection.h"
#include "DRV8835_Driver.h"
#include "QRE1113_Driver.h"
#include "my_util.h"
#include "PID.h"

//Functions prototypes
void parseData();

//Commands structure and create a variable
struct Command
{
	String name;
	int degrees;
	long duration;
	int speed;
	int nrSteps;
	int nrRepetition;

	//private data regarding command execution
	//Make this false when command was executed and make true when command was received
	bool updated = false;
}command;

void setup()
{
	Serial.begin(115200);
	Serial.println("---STARTING---");

	//initialize motors
	motors.attachM1Pin(26, 27);			// en, ph	-> left motor
	motors.attachM2Pin(25, 14, true);	// en, ph	-> right motor
	motors.init();
	motors.brake();
	
	//initialize connection
	conn::setupAP();
	server.begin();
	conn::waitForServerClients();
	//*/

	delay(1000);
}

void loop()
{
	//Updating encoders values
	leftEncoder.update();
	rightEncoder.update();
	
	//Wifi link - check if client is still connected
	if (!client || !client.connected())
		conn::waitForServerClients();

	//Check if client have a command for us
	if (client.available())
	{
		String recvMsg = conn::readString();
		Serial.print(recvMsg.substring(0, recvMsg.length() - 1));	//Removing \n from the end

		//Send feedback to Java App telling that we received command.
		conn::writeString("Command(s) received...");

		//Try to parse received data
		parseData(recvMsg) ? Serial.println("Data parsed!") : Serial.println("Failed to parse data!");
		
		//Try to execute command
		if (execute_command())
			conn::writeString("Command(s) executed...");
		else
			conn::writeString("Command(s) failed (NOT)...");
	}
	//*/

	//Allow commands execution from serial
	if (Serial.available() > 0)
	{
		String data = Serial.readString();
		if (!parseData(data))
			Serial.println("Failed to parse!");
		if (!execute_command())
			Serial.println("Failed to execute!");
	}

	//Precious debug info
	if (DEBUG)
		printEncoderSpeed(500);	//print data every 0.5 seconds
}

bool parseData(String data)
{
	//Format of data we receive: >>[G;-100;0] or [D;-100;0]#[R;-100;0]#[P;-100;0]
	if (data.length() < 7)
	{
		command.updated = false;
		return false;
	}

	//Geting string between barrecks: [*]
	data = data.substring(data.indexOf('[') + 1);
	data = data.substring(0, data.indexOf(']'));

	command.name = getStringPartByNr(data, ';', 0);
	command.name.toUpperCase();

	if (command.name == "D")
	{
		command.degrees = getStringPartByNr(data, ';', 1).toInt();
	}
	else if (command.name == "G")
	{
		command.speed = getStringPartByNr(data, ';', 1).toInt();
		command.duration = getStringPartByNr(data, ';', 2).toInt();
	}
	else if (command.name == "R")
	{
		command.nrSteps = getStringPartByNr(data, ';', 1).toInt();
		command.nrRepetition = getStringPartByNr(data, ';', 2).toInt();
	}
	else if (command.name == "P")
	{
		command.duration = getStringPartByNr(data, ';', 1).toInt();
	}
	else
	{
		command.updated = false;
		return false;
	}

	command.updated = true;
	return true;
}

bool execute_command()
{
	//reset encoders
	leftEncoder.currSteps = 0;
	rightEncoder.currSteps = 0;

	if (command.updated == false)
		return false;

	if (command.name == "D")
	{
		command_direction();
		return true;
	}
	else if (command.name == "G")
	{
		command_gear();
		return true;
	}
	else if (command.name == "R")
	{
		command_repetition();
		return true;
	}
	else if (command.name == "P")
	{
		command_pause();
		return true;
	}
	else
	{
		command.updated = false;
		return false;
	}
}

void command_direction()
{
	Serial.println("Executing direction...");
	Serial.println("Degree: " + String(command.degrees));

	//Firstly we have to map speed. This will prepare variables and all thr stuff for motors
	mapSpeed(10);	//the speed used to execute this command

	//degrees to steps conversion
	//26 = 180 degrees
	int steps = 26;	//90

	//Degees can be [-90,90], where -90 = 90degrees to left and 90 = 90degrees to right
	if (command.degrees < 0)
	{
		steps = map(command.degrees, 0, -90, 0, 13);
		computeSteps(steps, DRV8835::BACKWARD, 1, DRV8835::FORWARD);
	}
	else if (command.degrees > 0)
	{
		steps = map(command.degrees, 0, 90, 0, 13);
		computeSteps(steps, DRV8835::FORWARD, 1, DRV8835::BACKWARD);
	}

	mapSpeed(0);		//set speed to 0
	motors.brake();		//brake motors

	//Also reset encoders
	leftEncoder.currSteps = 0;
	rightEncoder.currSteps = 0;

	command.updated = false;
	Serial.println("SUCCESS\n");
}

void command_gear()
{
	Serial.println("Executing gear...");
	Serial.println("Speed: " + String(command.speed) + " Duration: " + String(command.duration));

	//Firstly we have to map speed. This will prepare variables and all thr stuff for motors
	mapSpeed(command.speed);

	//We need to go in a direction for a specific amount of time
	int startTime = millis();	//store time execution starts.
	do
	{
		//Need to update encoders
		leftEncoder.update();
		rightEncoder.update();

		motors.setM1Speed(speed);
		updateSlaveMotor(0);					//slave motor will pick up speed automatically from encoders measuements in order to get a smooth movement
		
		printEncoderSpeed(500);	//print data every 0.5 seconds -> good for debugging
	} while(millis() - startTime < command.duration);

	//brake motors - no more steps after gear was executed
	motors.setM1Speed(speed*-1);
	motors.setM2Speed(speed*-1);
	delay(25);

	mapSpeed(0);		//set speed to 0
	motors.brake();		//brake motors
	
	command.updated = false;
	Serial.println("SUCCESS\n");
}

void command_repetition()
{
	Serial.println("Executing repetition...");
	Serial.println("nrSteps: : " + String(command.nrSteps) + " nrRepetitions: " + String(command.nrRepetition));

	//Repetition? Not sure...

	command.updated = false;
	Serial.println("SUCCESS\n");

}

void command_pause()
{
	Serial.println("Executing pause...");
	Serial.println("Duration: " + String(command.duration));
	
	//Pause means to delay program
	delay(command.duration);

	//Set this marker to show when new commands were received
	command.updated = false;

	Serial.println("SUCCESS\n");
}




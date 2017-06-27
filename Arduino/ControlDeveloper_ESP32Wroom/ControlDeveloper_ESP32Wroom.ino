#include <Arduino.h>
#include <esp32-hal-dac.h>
#include <WiFi.h>

#include "connection.h"
#include "DRV8835_Driver.h"
#include "QRE1113_Driver.h"
#include "my_util.h"

//Motor object
DRV8835 motors;

//Encoders (IR Sensor: QRE1113)
QRE1113 leftEncoder(12);
QRE1113 rightEncoder(13);

//Functions prototypes
void parseData();
void updateEncoders();

//Commands structure and create a variable
struct Command
{
	String name;
	int degrees;
	long duration;
	int speed;
	int nrSteps;
	int nrRepetition;

	//private data regarding command
	bool updated = false;
}command;

void setup()
{
	Serial.begin(115200);
	Serial.println("---STARTING---");

	//initialize motors
	motors.attachM1Pin(25, 14);
	motors.attachM2Pin(26, 27);
	motors.init();

	//initialize connection
	conn::setupAP();
	server.begin();
	conn::waitForServerClients();
	//*/
}

int speed = 0;
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
		Serial.print(recvMsg);

		conn::writeString("Command(s) received...");

		parseData(Serial.readString());
		if (execute_command())
		{
			conn::writeString("Command(s) executed...");
		}
		else
		{
			conn::writeString("Command(s) failed (NOT)...");
		}
	}
	//*/

	if (Serial.available() > 0)
	{
		parseData(Serial.readString());
		execute_command();
	}

	//Get exact number of steps since last time
	//printPeriodicData(to_string(leftSteps) + "\t" + to_string(rightSteps), 50);
	//*/
}

bool parseData(String data)
{
	//Format of data we receive: >>[R;-100;0] or [R;-100;0]#[G;-100;0]#[R;-100;0]
	if (data.length() < 7)
	{
		command.updated = false;
		return false;
	}
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
	Serial.println("Degree:\t" + String(command.degrees));

	motors.setM1Speed(2000);
	motors.setM1Speed(-2000);
}

void command_gear()
{
	Serial.println("Executing gear...");
	Serial.println("Speed:\t" + String(command.speed) + "\tDuration: " + String(command.duration));

	motors.setM1Speed(4000);
	motors.setM1Speed(4000);
}

void command_repetition()
{
	Serial.println("Executing repetition...");
	Serial.println("nrSteps: :\t" + String(command.nrSteps) + "\tnrRepetitions: " + String(command.nrRepetition));

	//Repetition? Not sure...
}

void command_pause()
{
	Serial.println("Executing pause...");
	Serial.println("Duration:\t" + String(command.duration));
	
	//Pause means to delay program
	delay(command.duration);
}

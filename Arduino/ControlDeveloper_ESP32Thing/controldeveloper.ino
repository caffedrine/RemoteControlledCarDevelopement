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
QRE1113 leftEncoder(35);
QRE1113 rightEncoder(39);

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
	motors.attachM1Pin(25, 26);	// en, ph	-> left motor
	motors.attachM2Pin(27, 14);	// en, ph	-> right motor
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
	if(!client || !client.connected())
		conn::waitForServerClients();

	//Check if client have a command for us
	if(client.available())
	{
		String recvMsg = conn::readString();
		Serial.print(recvMsg);

		conn::writeString("Command(s) received...");
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
	//Format of data we receive: >>[0;-100;0] or [0;-100;0]#[0;-100;0]#[0;-100;0]
	if(data.length() < 7)
	{
		command.updated = false;
		return false;
	}
	data = data.substring( data.indexOf('[') + 1 );
	data = data.substring( 0, data.indexOf(']') );

	command.name = getStringPartByNr(data, ';', 0);
	command.name.toUpperCase();

	if(command.name == "D")
	{
		command.degrees = getStringPartByNr(data, ';', 1).toInt();
	}
	else if(command.name == "G")
	{
		command.speed = getStringPartByNr(data, ';', 1).toInt();
		command.duration = getStringPartByNr(data, ';', 2).toInt();
	}
	else if(command.name== "R")
	{
		command.nrSteps = getStringPartByNr(data, ';', 1).toInt();
		command.nrRepetition = getStringPartByNr(data, ';', 2).toInt();
	}
	else if(command.name == "P")
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
	if(command.updated == false)
		return false;

	if(command.name == "D")
	{
		return true;
	}
	else if(command.name == "G")
	{
		command_gear();
		return true;
	}
	else if(command.name== "R")
	{
		return true;
	}
	else if(command.name == "P")
	{
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
}

void command_gear()
{
	Serial.println("Executing gear...");
	Serial.println("Speed:\t" + String(command.speed) + "\tDuration: " + String(command.duration));
}

void command_repetition()
{
	Serial.println("Executing repetition...");
	Serial.println("nrSteps: :\t" + String(command.nrSteps) + "\tnrRepetitions: " + String(command.nrRepetition));
}

void command_pause()
{
	Serial.println("Executing pause...");
	Serial.println("Duration:\t" + String(command.duration));
}

#ifndef connection_h
#define connection_h

#include <Arduino.h>
#include <WiFi.h>
#include <WiFiServer.h>
#include <WiFiClient.h>

//AP Credentials. Those will be used in our Java applicaion.
const char *AP_Name   = "LAB_ROVER_1";
const char *AP_Passwd = "LabRover@Passwd";

//Led which will indicate data transfers
int connectionLedIndicatorPin = 19;
int trafficLedIndicatorPin = 22;

//Setting up server on specified port
WiFiServer server(1337, 1);
WiFiClient client;

//Connection manage namespace prototype
namespace conn
{
	void setupAP();
	void waitForWifiClients();
	void waitForServerClients();
	bool writeString(String data);
	String readString();
	bool isWritable();
	void connectionLedIndicator();
	void writePeriodicData(String data, int interval = 1000);
}

namespace conn
{
	//function used to initialize WiFi module in AP Mode
	void setupAP()
	{
		//Leds PINS are set as OUTPUT
	    pinMode(trafficLedIndicatorPin, OUTPUT);
	    pinMode(connectionLedIndicatorPin, OUTPUT);

		//Configure chip on AP mode
		Serial.print("Configuring AP...");
		WiFi.mode(WIFI_AP);
		bool AP_Created = WiFi.softAP(AP_Name, AP_Passwd);
		AP_Created ? Serial.println( "done" ) : Serial.println( "failed" );

		//Print AP IP Address
		IPAddress apIP = WiFi.softAPIP();
		Serial.print("AP IP Address: "); Serial.println(apIP);
	}

	//Loop to wait for wifi clients in case of connection lost
	void waitForWifiClients()
	{
		Serial.print("Waiting for Wi-Fi clients...");
		while(WiFi.softAPgetStationNum() == 0)
		{
			delay(600);
			connectionLedIndicator();
		}
		Serial.println("done");

		pinMode(trafficLedIndicatorPin, OUTPUT);
		digitalWrite(trafficLedIndicatorPin, LOW);
	}

	//Loop to wait for clients to open tcp sockets
	void waitForServerClients()
	{
		//Maybe socket stopped because link was lost because of WiFi disconnect
		delay(100); //We need this delay to get update values of WiFi Clients
		if(WiFi.softAPgetStationNum() == 0)
			conn::waitForWifiClients();

		Serial.print("Waiting for socket clients...");
		while(!client || !client.connected())
		{
			connectionLedIndicator();
			delay(250);
			client = server.available();

			//In the meanwhile AP may be disconnected - we may want to check
			if(WiFi.softAPgetStationNum() == 0)
			{
				Serial.println("failed");
				conn::waitForWifiClients();
				Serial.print("Waiting for socket clients...");
			}
		}
		client.setNoDelay(true);
		Serial.println( "done" );

		pinMode(trafficLedIndicatorPin, OUTPUT);
		digitalWrite(trafficLedIndicatorPin, LOW );
	}

	//Function to write to client TCP socket
	bool writeString(String content)
	{
		digitalWrite(trafficLedIndicatorPin, HIGH);

		if(!isWritable())
		{
			digitalWrite(trafficLedIndicatorPin, LOW);
			return false;
		}

		int result = client.println(content); delay(2);
		if(result < 1)
		{
			digitalWrite(trafficLedIndicatorPin, LOW);
			return false;
		}

		digitalWrite(trafficLedIndicatorPin, LOW);//*/
		return true;
	}

	//Read string from client TCP socket
	String readString()
	{
		digitalWrite(trafficLedIndicatorPin, HIGH);
		String result = client.readString();
		digitalWrite(trafficLedIndicatorPin, LOW);//*/
		return result;
	}

	//check if client socket is writable
	bool isWritable()
	{
		int result = client.print(">");
		delay(4); //wait for register with error to be filled up
		if(!result)
			return false;

		result = client.print('>');
		delay(4); //wait for register with error to be filled up
		if(!result)
			return false;

		return true;
	}

	//Indicate via leds connection status
	void connectionLedIndicator()
	{
		static long previousMillis = 0;
		if (millis() - previousMillis > 400)
		{
			previousMillis = millis();

			pinMode(trafficLedIndicatorPin, INPUT);
			bool currStatus = digitalRead(5);

			pinMode(trafficLedIndicatorPin, OUTPUT);
			digitalWrite(trafficLedIndicatorPin, currStatus?0:1 );
		}
	}

	//Write periodic some data
	void writePeriodicData(String data, int interval)
	{
		static long previousMillis = millis();
		if (millis() - previousMillis > interval)
		{
			previousMillis = millis();
			conn::writeString(data);
		}
	}
}
#endif

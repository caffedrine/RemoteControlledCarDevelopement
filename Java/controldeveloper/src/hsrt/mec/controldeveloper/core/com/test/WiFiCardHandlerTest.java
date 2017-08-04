package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.ComHandler;
import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.core.com.WiFiCard;
import hsrt.mec.controldeveloper.core.com.WiFiCardHandler;

import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.WiFi;
import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Status;

import java.util.Vector;

public class WiFiCardHandlerTest
{
	//Possible protocols
	public enum Protocols
	{
		TextFile,
		ComPort,
		WiFi
	}
	
	//Choosing protocol wanted to be used
	private static Protocols protocol;
	private static Vector<ICommand> v;
	
	public static void main(String[] args) throws InterruptedException
	{
		//WiFi Cards handler is required to get a list of available Wi-Fi adapters
		WiFiCardHandler hWifi = new WiFiCardHandler();
		
		//Desired wifi adapter - handler
		WiFiCard wifi;
		
		//Specifying protocol
		protocol = Protocols.WiFi;
		
		//Initializing vector
		v = new Vector<ICommand>();
		v.addElement(new Gear("Gear", 50, 3));
		v.addElement(new Direction("Direction", 90));
		v.addElement(new Pause("Pause", 3.5));
		v.addElement(new Gear("Gear", -50, 2));
		v.addElement(new Gear("Direction", -90, 4));
		
		// Make sure Wi-Fi protocol is selected
		if(protocol != Protocols.WiFi)
		{
			Log.log("Only WIFI is implemented here!");
			return;
		}
		
		//Getting all wifi adapters available
		WiFiCard[] wifiCards = hWifi.getWifiCards();
		if(wifiCards.length <= 0)
		{
			Log.log(hWifi.getLastError());
			return;
		}
		Log.log("Compatible wifi cards detected: " + wifiCards.length);
		
		//Selecting card we want to use and update ip address
		wifi = wifiCards[0];
		wifi.updateIPv4();
		
		// Print informations regarding Wi-Fi about adapter to use
		Log.log("Selected first index card:\n----------------------\n" + 
				 "Index    : " + wifi.getIndex()       + "\n" +
				 "Name     : " + wifi.getName()        + "\n" +
				 "Disp Name: " + wifi.getDisplayName() + "\n" +
				 "IPv4     : " + wifi.getIPv4()        + "\n----------------------");
		
		
		//I/O type and the stuff
		IOType ioType;
		ioType = new WiFi( wifi );	
		
		//Make sure connection is established before start sending commands
		while(!((WiFi) ioType).isConnected())
		{
			Log.log("ERORR. No connection with car. Trying to connect again...");
			ioType = new WiFi( wifi );
			
			//Attempt reconnection every 3 seconds
			Thread.sleep(3000);
		}
		Log.log("Connection with car established...");
		
		
		ComHandler.getInstance().start(v, ioType);
		
		//Thread will be destroyed after main function ends. 
		//This way we have to make sure that we wait for it to finish his work.
		Thread.sleep(100000);
	}
}

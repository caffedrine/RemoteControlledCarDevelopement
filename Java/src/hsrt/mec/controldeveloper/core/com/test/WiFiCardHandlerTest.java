package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.ComHandler;
import hsrt.mec.controldeveloper.core.com.ComPort;
import hsrt.mec.controldeveloper.core.com.ComPortHandler;
import hsrt.mec.controldeveloper.core.com.WiFiComHandler;
import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.core.com.WiFiCard;
import hsrt.mec.controldeveloper.core.com.WiFiCardHandler;

import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.SerialUSB;
import hsrt.mec.controldeveloper.io.TextFile;
import hsrt.mec.controldeveloper.io.WiFi;
import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Status;

import java.io.File;
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
	
	//Car credentials
	private int roverNumber = 1;
	private final String roverPassword = "123";
	
	//Choosing protocol wanted to be used
	private static Protocols protocol;
	private static Vector<ICommand> v;
	
	public static void main(String[] args) throws InterruptedException
	{
		//WiFi Cards handler required to get wifi adapters
		WiFiCardHandler hWifi = new WiFiCardHandler();
		
		//Desired wifi adapter - handler
		WiFiCard wifi;
		
		//Specifing protocol
		protocol = Protocols.WiFi;
		
		//Initializing vector
		v = new Vector<ICommand>();
		v.addElement(new Direction("Direction", 100));
		v.addElement(new Gear("Gear", 90, 3.0));
		v.addElement(new Pause("Pause", 3.0));
		v.addElement(new Gear("Gear", 90, 3.0));
		v.addElement(new Gear("Gear", 120, 3.0));
		
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
		
		Log.log("Selected first index card:\n----------------------\n" + 
				 "Index    : " + wifi.getIndex()       + "\n" +
				 "Name     : " + wifi.getName()        + "\n" +
				 "Disp Name: " + wifi.getDisplayName() + "\n" +
				 "IPv4     : " + wifi.getIPv4()        + "\n----------------------");
		
		//Before dealing with I/O operations, we have to establish Wi-Fi link 
		//Something like this: wifi.connectAP("name", "password", encription)
		
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
		
		/*// Debugging connection
		////////////////////////////////////////////
		// Example of how we can make sure that our data is actually being send
		Vector<String> vc = new Vector<String>();
		vc.addElement("test_");
		for(int i=0; i<=10; i++)
		{
			vc.set(0, vc.get(0) + i);
			
			Log.log("Attempting to send message " + i);
			int attempts = 0;
			while(true)
			{
				if(ioType.write(vc))
				{
					Log.log("Message " + i + " send!\n");
					break;
				}	
				
				Log.log("Attempting to reconnect...");
				if(((WiFi)ioType).doReconnect())
				{
					Log.log("Reconnected: message " + i + " send!\n");
					ioType.write(vc);
					break;
				}
				else
				{
					Log.log("Failed to reconnect!");
					Log.log("Cause: " + Status.getInstance().getMessage());
				}
				
				if(attempts++ == 5)
				{
					Log.log("All attempts failed! Message " + i + " not send.\n");
					break;
				}
				Thread.sleep(3000);
			}
			Thread.sleep(3000);
		}
		*/
		
		ComHandler.getInstance().start(v, ioType);
		
		//Thread will be destroyed after main function ends. 
		//This way we have to make sure that we wait for it to finish his work.
		Thread.sleep(100000);
	}
}

package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.util.Globals;

import java.net.*;
import java.util.*;

/**
 * Class used to store data about Wi-Fi card used to 
 * communicate with car.
 *
 */
public class WiFiCard
{
	private int index;
	private String name;
	private String displayName;
	private String IPv4 = Globals.ROVER_IPv4;
	
	private String lastError;
	
	//Contructor params
	WiFiCard(int index, String name, String displayName)
	{
		this.index = index;
		this.name = name;
		this.displayName = displayName;
	}
	
	////////////////////////
	//Get and set methods
	public int getIndex()
	{
		return this.index;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public String getIPv4()
	{
		return this.IPv4;
	}
	
	public String getLastError()
	{
		return this.lastError;
	}
	
	public void setLastError(String err)
	{
		this.lastError = err;
	}
	
	//Updating IPv4 Address. After pairing with rover we have to update it as it may change
	public boolean updateIPv4()
	{
	    //For our rover, we just defined IP in globals
        this.IPv4 = Globals.ROVER_IPv4; 
        //this.IPv4 = "127.0.0.1"; //for debugging local;
            return true;
        
	    //Get IP Automatically - Unstable under windows...
	    /*
		this.IPv4 = "";
		
		try
		{
			//Getting instance of our card
			NetworkInterface networkCard = NetworkInterface.getByIndex(this.index);
			
			//Getting all IP addresses
		    Enumeration<InetAddress> inetAddress = networkCard.getInetAddresses();
		    
		    //Check if we have IP addresses assigned to our adapter
		    if(!inetAddress.hasMoreElements())
		    {
		    	this.setLastError("No IP Addresses assigned to selected adapter. Are you sure if you are connected to car?");
		    	return false;
		    }
		    
		    //IP DataType
		    InetAddress currentAddress;
		    
		    //Loop to check all assigned IPs and grab first IPv4
		    currentAddress = inetAddress.nextElement();
		    while(inetAddress.hasMoreElements())
		    {
		        currentAddress = inetAddress.nextElement();
		        if(currentAddress instanceof Inet4Address)
		        {
		            
		            this.IPv4 = currentAddress.toString().replace("/",  "");
		            break;
		        }
		    }
		    
		    if(this.IPv4 != "")
		    	return true;
		    else
		    {
		    	this.setLastError("STRANGE! Only IPv6 Assigned to this WiFi Adapter!!!");
		    	return false;
		    }
		}
		catch (SocketException e)
		{
			this.setLastError(e.toString());
			return false;
		}
		//*/	
	}
}

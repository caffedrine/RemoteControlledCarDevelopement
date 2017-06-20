package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.util.Globals;

import java.net.*;
import java.util.*;

public class WiFiComHandler
{
	//WiFi Card handler
	WiFiCard hCard;
	
	private int car_number;
	private String car_name;
	private String car_password;
	
	//env vars
	private String lastError;
	
	public String getLastError()
	{
		return this.lastError;
	}
	
	private void setLastError(String err)
	{
		this.lastError = err;
	}
	
	public WiFiComHandler(WiFiCard card)
	{
		//Update IPv4 before connection
		this.hCard = card;
		hCard.updateIPv4();
	}
	
	public void setRoverNumber(int nr)
	{
		this.car_number = nr;
		
		//Updating name as it is dependent by number
		this.car_name = Globals.CAR_PREFIX_NAME + nr;
	}
	
	public void setRoverPassword(String pass)
	{
		this.car_password = pass;
	}
	
	public int getCarNumber()
	{
		return this.car_number;
	}
}

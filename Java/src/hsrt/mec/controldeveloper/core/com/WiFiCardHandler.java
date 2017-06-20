package hsrt.mec.controldeveloper.core.com;

import java.net.*;
import java.util.*;

public class WiFiCardHandler
{
	private String lastError;
	
	private class WiFiIdentifier{
		private final String WIFI= "wi-fi";
		private final String WIRELESS= "wireless";
		private final String WLAN="wlan";
		
		public boolean isValid(NetworkInterface netint){
			if(netint.getDisplayName().toLowerCase().contains(WLAN) ||
			   netint.getDisplayName().toLowerCase().contains(WIFI)||
			   netint.getDisplayName().toLowerCase().contains(WIRELESS))
			   return true;
			return false;
		}
	}
	
	public String getLastError()
	{
		return this.lastError;
	}
	
	private void setLastError(String err)
	{
		this.lastError = err;
	}
	
	public WiFiCard[] getWifiCards()
	{
		//Getting all network interfaces available
		Enumeration<NetworkInterface> nets;
		try
		{
			nets = NetworkInterface.getNetworkInterfaces();
		}
		catch (SocketException e)
		{
			this.setLastError(e.toString());
			return new WiFiCard[0];
		}
		
		//Keep only Wi-Fi adapters
		List<WiFiCard> cards = new ArrayList<WiFiCard>();
		
		for (NetworkInterface netint : Collections.list(nets))
		{
			if (new WiFiIdentifier().isValid(netint))
			{
				//Getting device info
				int index = netint.getIndex();
				String name = netint.getName();
				String displayName = netint.getDisplayName();
				
				//Creating WiFi Card object we want to add into list
				WiFiCard card = new WiFiCard(index, name, displayName);
				
				//Populating out custom list with right adapters
				cards.add( card );
			}
		}
		if(cards.size() == 0)
			this.setLastError("List empty! No Wi-Fi adapters found!");
		
		return cards.toArray(new WiFiCard[0]);
	}	
}

package hsrt.mec.controldeveloper.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;

import hsrt.mec.controldeveloper.core.com.WiFiCard;
import hsrt.mec.controldeveloper.util.Globals;
import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;

public final class WiFi implements IOType
{
	//Handler to wifi card we'll use
	private WiFiCard wifiAdapter = null;
	
	//Socket handler
	private Socket hSocket = null;
	DataInputStream inputStream = null;
	DataOutputStream outputStream = null;
	
	public WiFi(WiFiCard card)
	{
		this.wifiAdapter = card;
		if(!wifiAdapter.updateIPv4())
		{
			Status.getInstance().setStatus(Status.E_NOK, wifiAdapter.getLastError());
			return;
		}
		
	    try
		{ 
	        //check if ip address pass is in the same range
	        if(!sameNetwork(wifiAdapter.getIPv4()))
	            return;     
	        
			this.hSocket = new Socket(wifiAdapter.getIPv4(), Globals.TCP_PORT);
			hSocket.setKeepAlive(true);
			
			//Write some data to server
			this.outputStream = new DataOutputStream( hSocket.getOutputStream() );
			this.outputStream.write( ("New connction!\n").getBytes() );
			
			//Receive data from server
			this.inputStream = new DataInputStream( hSocket.getInputStream() );
			//Log.log(this.inputStream.readUTF());
		}
		catch (UnknownHostException e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
		}
		catch (IOException e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
		}
	    catch(Exception e)
	    {
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
	    }
	}

	//Methods implementation
	public boolean read(Vector<String> content)
	{	
	    if(!isConnected())
	    {
	        doReconnect();
	        return false;
	    }
	    
	    try
	    {
	        if(inputStream.read() == -1)
	            doReconnect();
	    }
	    catch(Exception e)
	    {
	        doReconnect(); 
	    }
	    
	    //If we were able to access read() then we are also able to read our messge
		try
		{
		    //initialize first element of our vector
		    content.add("");
		    String recvData = this.inputStream.readLine();
		    if(recvData == null)
		        return false;
		    
		    content.setElementAt(recvData,  0);
		    return true;
		}
		catch(Exception e)
		{
		    Status.getInstance().setStatus(Status.E_NOK, e.toString());
	        return false;
		}
	}

	public boolean write(Vector<String> data)
	{
		if(!this.isConnected())
		{
		    if(!this.doReconnect())
		        return false;
		}
		
		if (data == null)
		{
			Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
			return false;
		}

		
		//Try to write >> first to make sure we can write
		try
		{
		    outputStream.write( (">>>").getBytes() );
		}
		catch(Exception e)
		{
		    this.doReconnect();
		}
		//*/
		
		//If we were able to write >>  then we can write our message
		try
		{		
			for (String element : data)
			{
				element += "\n";
				byte[] dataBytes = element.getBytes();
				this.outputStream.write(dataBytes);
			}
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage() + " write()");
			return false;
		}
		return true;
	}

	public boolean close()
	{
		try
		{   
			if(this.hSocket != null)
				this.hSocket.close();
			if(this.inputStream != null)
				this.inputStream.close();
			if(this.outputStream != null)
				this.outputStream.close();
			
			return true;
		}
		catch (IOException e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.toString());
			return false;
		}
		finally
		{
		    hSocket = null;
		    inputStream = null;
		    outputStream = null;
		}
	}

    public boolean isConnected()
	{
		if(this.hSocket == null)
			return false;
		
		if(!this.hSocket.isConnected())
			return false;
		
		if(this.hSocket.isClosed())
		    return false;
		
		if(this.inputStream == null)
			return false;
		
		if(this.outputStream == null)
			return false;
		
		/*
		try
		{	
		    outputStream.writeChar('>');
		}
		catch (Exception e)
		{
		    this.close();
		    
		    Log.log("Exception while testing...");
			return false;
		}
		//*/
		return true;
		
	}

	public boolean doReconnect()
	{
		if(!this.close())
			return false;
		
        if(!sameNetwork(wifiAdapter.getIPv4()))
            return false;  
		
		if(!wifiAdapter.updateIPv4())
		{
			Status.getInstance().setStatus(Status.E_NOK, wifiAdapter.getLastError());
			return false;
		}
		
	    try
		{	
			this.hSocket = new Socket(wifiAdapter.getIPv4(), Globals.TCP_PORT);
			hSocket.setKeepAlive(true);
				
			//Write some data to server
			this.outputStream = new DataOutputStream( hSocket.getOutputStream() );
			this.outputStream.write( ("\n--------------\nReconnected!!\n").getBytes() );
			
			//Receive data from server
			this.inputStream = new DataInputStream( hSocket.getInputStream() );
			//Log.log(this.inputStream.readUTF());
			
			Log.log("Reconnected...");
			
			return true;
		}
		catch (UnknownHostException e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage() + " doReconnect()");
			return false;
		}
		catch (IOException e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage() + " doReconnect()");
			return false;
		}
	    catch(Exception e)
	    {
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage() + " doReconnect()");
			return false;
	    }
	}

    private boolean sameNetwork(String ip)
    {
        try
        {
            String mask = "255.255.255.0";
            
            InetAddress ip1 = InetAddress.getByName(ip);
            byte[] a1 = ip1.getAddress();
            
            InetAddress ip2 = getIPv4(wifiAdapter.getIndex());
            if(ip2 == null)
            {
                Status.getInstance().setStatus(Status.E_NOK, "No IPv4 Addresses detected...");
                return false;
            }
                    
            byte[] a2 = ip2.getAddress();
            byte[] m = InetAddress.getByName(mask).getAddress();
            
            for (int i = 0; i < a1.length; i++)
                if ((a1[i] & m[i]) != (a2[i] & m[i]))
                    return false;
        }
        catch(Exception e)
        {
            Status.getInstance().setStatus(Status.E_NOK, "Can get local ip address...");
            return false;
        }
        return true;
    }
    
    private InetAddress getIPv4(int iface_index)
    {
        //IP DataType
        InetAddress currentAddress = null;
        
        try
        {
            //Getting instance of our card
            NetworkInterface networkCard = NetworkInterface.getByIndex(iface_index);
            
            //Getting all IP addresses
            Enumeration<InetAddress> inetAddress = networkCard.getInetAddresses();
            
            //Check if we have IP addresses assigned to our adapter
            if(!inetAddress.hasMoreElements())
            {
                Status.getInstance().setStatus(Status.E_NOK,"No IP Addresses assigned to selected adapter. Are you sure if you are connected to car?");
                return currentAddress;
            }
            
            //Loop to check all assigned IPs and grab first IPv4
            currentAddress = inetAddress.nextElement();
            while(inetAddress.hasMoreElements())
            {
                currentAddress = inetAddress.nextElement();
                if(currentAddress instanceof Inet4Address)
                {
                    return currentAddress;
                }
            }
        }
        catch (SocketException e)
        {
            Status.getInstance().setStatus(Status.E_NOK,e.toString());
            return currentAddress;
        }
        
        return currentAddress;
    }
}

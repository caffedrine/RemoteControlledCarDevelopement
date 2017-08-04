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

/**
 * 
 * Read and Write string data to Input/Output Streams attached
 * to TCP Socket session.
 *
 */
public final class WiFi implements IOType
{
	/// Handler to wifi card we'll use
	private WiFiCard wifiAdapter = null;
	
	private Socket hSocket = null;         /// TCP Socket Handler
	DataInputStream inputStream = null;    /// Socket input stream
	DataOutputStream outputStream = null;  /// Socket output stream 
	
	/**
	 * This is the constructor of this class. 
	 * @param card is an WiFiCard object used in order to identify Wi-Fi
	 * card used to communicate with rover.
	 */
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
	        // Check if ip address pass is in the same range. To be investigated as it fails sometime under windows.
	        //if(!sameNetwork(wifiAdapter.getIPv4()))
	            //return;     
	        
			this.hSocket = new Socket(wifiAdapter.getIPv4(), Globals.TCP_PORT);      // Initialize a new socket session
			hSocket.setKeepAlive(true);                                              // Keep connection alive
			hSocket.setSoTimeout(Globals.TIMEOUT);                                   // this timeout is required as read() will block thread;
			
			//Write some data to server
			this.outputStream = new DataOutputStream( hSocket.getOutputStream() );   // Attaching output stream to created socket
			this.outputStream.write( ("New connction!\n").getBytes() );              // Sending an welcome message to car
			
			//Receive data from server
			this.inputStream = new DataInputStream( hSocket.getInputStream() );      // Attaching the input stream to TCP Socket
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

	/**
	 * Read input stream attached to TCP connection.
	 * @return true in case it was able to read content being send by car
	 * otherwise is returned false.
	 * Readed element is stored in vector given as parameter.
	 * 
	 */
	public boolean read(Vector<String> content)
	{	
	    if(!isConnected())     // Make sure there is a connection in order to read content
	    {
	        doReconnect();     // If no connection attempt to establish one
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
	    
	    //If we were able to access read() then we are also able to read our message
		try
		{
		    //initialize first element of our vector
		    content.add("");
		    String recvData = this.inputStream.readLine();    // The reading process
		    if(recvData == null)
		        return false;
		    
		    content.setElementAt(recvData,  0);               // Filling vector with message received
		    return true;
		}
		catch(Exception e)
		{
		    Status.getInstance().setStatus(Status.E_NOK, e.toString());
	        return false;
		}
	}

	/**
	 * Write data to output stream attached to the TCP Socket
	 * Written data is given as a vector. 
	 * In case data was written successfully function return true otherwise false.
	 */
	public boolean write(Vector<String> data)
	{
		if(!this.isConnected())
		{
		    if(!this.doReconnect())
		        return false;
		}
		
		if (data == null)         // If no data to send
		{
			Status.getInstance().setStatus(Status.E_NOK, Message.mIO_DATA_ERROR);
			return false;
		}

		// Try to write >> first to make sure we can write
		try
		{
		    outputStream.write( (">>>").getBytes() );
		}
		catch(Exception e)        // Exception thrown in case of no connection or lost connection
		{
		    this.doReconnect();
		}
		
		//If we were able to write >>  then we can write our message
		try
		{		
			for (String element : data)
			{
				element += "\n";
				byte[] dataBytes = element.getBytes();
				this.outputStream.write(dataBytes);         // Write data to socket input stream
			}
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK, e.getMessage() + " write()");
			return false;
		}
		return true;  
	}

	/**
	 * Because streams are used to write/read data,
	 * those shall be closed at the end of application.
	 * (non-Javadoc)
	 * @see hsrt.mec.controldeveloper.io.IOType#close()
	 */
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

	/**
	 * This function check is there is a connection established and
	 * active with the car. 
	 * This function shall be called before any attempt to write on output
	 * stream.
	 * 
	 * @return true in case connection is active and data can be written/readed
	 * and false otherwise
	 */
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
		
		/*        // Deep try
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

    /**
     * In case of connection lost, this function will attempt to
     * re-establish a new connection with car.
     * @return true if a connection with car was established and 
     * false otherwise.
     */
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
			hSocket.setSoTimeout(Globals.TIMEOUT); // this timeout is required as read() will block thread;
				
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

	/**
	 * Auxiliary function which will check an IP address so see whether it is in the same subnet.
	 * If IP it's not in same subnet, then computer is connected to wrong access point.
	 * 
	 * @param ip ip address in standard format. E.g. 192.168.1.1
	 * @return true in case IP address is in the subnet required
	 */
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
    
    /**
     * Retrieve the IPv4 Address assigned to selected  Wi-Fi adapter (interface)
     * @param iface_index The interface which was used to connect to rover.
     * @return InetAddress object.
     */
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

package hsrt.mec.controldeveloper.util;

/**
 * Defines global constants which are used program wide.
 */
public final class Globals
{
	private Globals()
	{
	}
	
	/**
	 * Delemiter, which devides the command values representing speed,
	 * direction, etc. within one command. <br/>
	 * <br/>
	 * Defined value is: {@value #DELEMITER}
	 */
	public static final String DELEMITER = ";";
	
	/**
	 * Time to pause the worker task in order to initialize the serial port. <br/>
	 * <br/>
	 * Defined value is: {@value #STEPTIMER}
	 */
	public static final int STEPTIMER = 5000;
	
	/**
	 * {@code true}, if logging information shall be printed out on the console;
	 * {@code false} otherwise. <br/>
	 * <br/>
	 * Defined value is: {@value #ISLOG}
	 */
	public static final boolean ISLOG = true;
	
	// Added later
	/** 
	 * Any rover will have a number attached to the prefix. The name is  with prefix hardcoded on 
	 * car firmware and should be unique for each rover. This mean that in environments with multiple
	 * rovers suffix value will be incremented.
	 * E.g. LAB_ROVER_1, LAB_ROVER_2
	 */
	public static final String CAR_PREFIX_NAME = "LAB_ROVER_";
	
	/**
	 * The default port used by rover to listen for incoming connections. This parameter is also hardcoded
	 * directly in car's firmware.
	 */
	public static final int TCP_PORT = 1337;       
	
	/**
	 * The car is acting like a server. It listen for incoming connections using port above and the
	 * IP address defined here. Also IP Address is hardcoded inside car firmware.
	 */
	public static final String ROVER_IPv4 = "192.168.4.1";
	
	/**
	 * In case of connection lost, define how many attempts to reconnect shall be performed
	 */
	public static final int ATTEMPTS = 5;
	
	/**
	 * Timeout in ms in which rover should send an response
	 * !! Time out must be greater than the time required by direction command to execute a 90 degree rotation
	 */
	public static final int TIMEOUT = 8000;
}


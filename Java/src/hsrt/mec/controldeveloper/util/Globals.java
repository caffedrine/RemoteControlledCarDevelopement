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
	
	//Defining rover prefix name
	public static final String CAR_PREFIX_NAME = "LAB_ROVER_";
	public static final int TCP_PORT = 1337;
	public static final String ROVER_IPv4 = "192.168.4.1";
}

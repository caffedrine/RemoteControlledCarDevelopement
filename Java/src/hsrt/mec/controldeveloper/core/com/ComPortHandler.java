package hsrt.mec.controldeveloper.core.com;

import com.fazecast.jSerialComm.*;

/**
 * Helper class for providing serial communication ports. {@code ComPortHandler}
 * provides all available serial communication ports in form of an array of
 * {@link hsrt.mec.controldeveloper.core.com.ComPort} objects. <br/>
 * <br/>
 * In order to use a serial communication port one
 * {@link hsrt.mec.controldeveloper.core.com.ComPort} object out of the array
 * has to be set in a {@link hsrt.mec.controldeveloper.io.SerialUSB} object.
 * This {@link hsrt.mec.controldeveloper.io.SerialUSB} object in turn must be
 * provided when calling the
 * {@link hsrt.mec.controldeveloper.core.com.ComHandler#start(java.util.Vector, hsrt.mec.controldeveloper.io.IOType)}
 * method in order to start the command execution process.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComPort
 * @see hsrt.mec.controldeveloper.core.com.ComHandler
 */
public class ComPortHandler
{
	/**
	 * Provides all available serial communication ports.
	 * 
	 * @return Array of serial communication ports.
	 */
	public static ComPort[] getPorts()
	{
		SerialPort[] sPorts = SerialPort.getCommPorts();
		
		ComPort[] cPorts = new ComPort[sPorts.length];
		for (int i = 0; i < sPorts.length; i++)
		{
			int id = i + 1;
			String name = sPorts[i].getSystemPortName();
			String description = sPorts[i].getSystemPortName().concat(": ")
					.concat(sPorts[i].getDescriptivePortName());
			
			cPorts[i] = new ComPort(id, name, description);
		}
		
		return cPorts;
	}
}
package hsrt.mec.controldeveloper.core.com;

/**
 * Defines a serial communication port with an id, name, description.
 * {@code ComPort} objects are provided through
 * {@link hsrt.mec.controldeveloper.core.com.ComPortHandler} and they are used
 * to configure an serial communication in a
 * {@link hsrt.mec.controldeveloper.io.SerialUSB} object. <br/>
 * <br/>
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComPortHandler
 * @see hsrt.mec.controldeveloper.io.SerialUSB
 */

public final class ComPort
{
	private int id;
	private String name;
	private String description;
	
	ComPort(int id, String portName, String portDescription)
	{
		this.id = id;
		this.name = portName;
		this.description = portDescription;
	}
	
	/**
	 * Returns the id of the serial communication port.
	 * 
	 * @return Id of the serial communication port.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Returns the name of the serial communication port.
	 * 
	 * @return Name of the serial communication port.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the description of the serial communication port.
	 * 
	 * @return Description of the serial communication port.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Compares two serial communication ports. Two ports are identical if they
	 * reference the same object or if they have the same description.
	 * 
	 * @return {@code true} if this object is equals to the obj argument;
	 *         {@code false} otherwise.
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ComPort))
			return false;
		
		if (super.equals(obj)) // identisches objekt
			return true;
		
		if (this.description.equals(((ComPort) obj).description))
			return true;
		else
			return false;
	}
}

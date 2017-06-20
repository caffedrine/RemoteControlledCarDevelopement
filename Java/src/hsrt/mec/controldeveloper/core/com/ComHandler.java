package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.core.com.command.IDirection;
import hsrt.mec.controldeveloper.core.com.command.IGear;
import hsrt.mec.controldeveloper.core.com.command.IPause;
import hsrt.mec.controldeveloper.core.com.command.IRepetition;
import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.WiFi;
import hsrt.mec.controldeveloper.util.Log;

import java.util.Vector;

/**
 * {@code ComHandler} handles the communication with an object of
 * {@link hsrt.mec.controldeveloper.io.IOType}. The class takes a vector of
 * {@link hsrt.mec.controldeveloper.core.com.command.ICommand} objects, prepares
 * its content internally, and sends the prepared commands to the previously
 * specified i/o port. <br/>
 * <br/>
 * Using {@code ComHandler} requires the following steps: <br/>
 * <br/>
 * 1. Getting an object of class {@code ComHandler} <br/>
 * {@code ComHandler} is implemented as a singleton. An object of this class can
 * be acquired using the method {@link #getInstance()} <br/>
 * <br/>
 * 2. Defining a communication port <br/>
 * The communication port is an implementation of the interface
 * {@link hsrt.mec.controldeveloper.io.IOType} and is provided to the
 * {@code ComHandler} instance using the {@link #start(Vector, IOType)} method.
 * In case the commands shall be send to a serial usb port, first, the
 * communication port has to be determined using the
 * {@link hsrt.mec.controldeveloper.core.com.ComPortHandler}. <br/>
 * <br/>
 * 3. Registering to {@code ComHandler} <br/>
 * The registration to the {@code ComHandler} is based on the
 * {@link hsrt.mec.controldeveloper.core.com.IComListener} interface. In order
 * to retrieve information (here: which command is actually executed) from the
 * actually process, {@code ComHandler} implements an event model (cp. JButton
 * and ActionListener). This means, the class which uses the {@code ComHandler}
 * has to register itself to the {@code ComHandler} before starting the control
 * process execution. Therefore, the class has to implement the
 * {@link hsrt.mec.controldeveloper.core.com.IComListener} interface and calls
 * the method {@link #register(IComListener)}. After starting the execution
 * process {@code ComHandler} will inform the class calling the method
 * {@link hsrt.mec.controldeveloper.core.com.IComListener#commandPerformed(ICommand)}
 * of the interface {link hsrt.mec.controldeveloper.core.com.IComListener}. When
 * calling this method {@code ComHandler} provides the actual executed command
 * in form of an {@link hsrt.mec.controldeveloper.core.com.command.ICommand}
 * object. <br/>
 * <br/>
 * 4. Starting and stopping the execution process <br/>
 * For starting and stopping the execution of the command process
 * {@code ComHandler} provides the methods {@link #start(Vector, IOType)} and
 * {@link #stop()}.
 * 
 * @see hsrt.mec.controldeveloper.core.com.ComPortHandler
 * @see hsrt.mec.controldeveloper.core.com.ComPort
 * @see hsrt.mec.controldeveloper.core.com.IComListener
 * @see hsrt.mec.controldeveloper.core.com.command.ICommand
 * @see hsrt.mec.controldeveloper.io.SerialUSB
 */
public class ComHandler
{
	private final String IDIRECTION = "IDirection";
	private final String IGEAR = "IGear";
	private final String IPAUSE = "IPause";
	private final String IREPETITION = "IRepetition";
	
	private static ComHandler instance = null;
	private IComListener commandListener = null;
	private BackgroundControl backgroundControl = null;
	
	// singleton --- begin ---
	private ComHandler()
	{
	}
	
	/**
	 * Static access to the singleton object.
	 * 
	 * @return {@code ComHandler} Singleton object of type {@code ComHandler}.
	 */
	public static ComHandler getInstance()
	{
		if (instance == null)
			instance = new ComHandler();
		
		return instance;
	}
	
	// singleton --- end ---
	
	// observer/listener --- begin ---
	/**
	 * Registers an object of the class, which implements the
	 * {@link hsrt.mec.controldeveloper.core.com.IComListener} interface, to
	 * this object.
	 */
	public void register(IComListener cL)
	{
		// register lediglich ein einziges objekt
		commandListener = cL;
	}
	
	/**
	 * Unregisters an object of the class, which implements the
	 * {@link hsrt.mec.controldeveloper.core.com.IComListener} interface, from
	 * this object.
	 */
	public void unregister(IComListener cL)
	{
		if (commandListener == cL)
			commandListener = null;
	}
	
	boolean update(ICommand command)
	{
		if (commandListener == null)
			return false;
		
		commandListener.commandPerformed(command);
		return true;
	}
	
	// observer/listener --- end ---
	
	/**
	 * Starts the control process based on the given commmand list and the
	 * communication port.
	 * 
	 * @param commandList
	 *            List of
	 *            {@link hsrt.mec.controldeveloper.core.com.command.ICommand}
	 *            objects.
	 * @param ioType
	 *            Communication port of type
	 *            {@link hsrt.mec.controldeveloper.io.IOType}.
	 * @return true, if no error occured; false, otherwise.
	 */
	public boolean start(Vector<ICommand> commandList, IOType ioType)
	{
		Vector<MotorCommand> motorCommands = new Vector<MotorCommand>();
		
		// create motor commands
		for (ICommand command : commandList)
		{
			MotorCommand mC = createMotorCommand(command, commandList);
			if (mC != null)
				motorCommands.addElement(mC);
		}
		
		backgroundControl = new BackgroundControl(this, ioType, motorCommands);
		backgroundControl.setDaemon(true);
		backgroundControl.setPriority(Thread.MAX_PRIORITY);
		backgroundControl.start();
		return true;
	}
	
	/**
	 * Stops the control process.
	 * 
	 * @return true, if no error occured; false, otherwise.
	 */
	public boolean stop()
	{
		if (backgroundControl == null)
			return false;
		
		backgroundControl.cancel();
			return true;
	}
	
	private MotorCommand createMotorCommand(ICommand command,
			Vector<ICommand> commandList)
	{
		Class<?>[] interfaces = command.getClass().getInterfaces();
		
		MotorCommand mC = null;
		for (int i = 0; i < interfaces.length; i++)
		{
			String iPath = interfaces[i].getName();
			String iName = iPath.substring(iPath.lastIndexOf(".") + 1);
			
			if (iName.equalsIgnoreCase(IDIRECTION))
			{
				mC = computeDirectionCommand((IDirection) command);
				break;
			}
			else
				if (iName.equalsIgnoreCase(IGEAR))
				{
					mC = computeGearCommand((IGear) command);
					break;
				}
				else
					if (iName.equalsIgnoreCase(IPAUSE))
					{
						mC = computePauseCommand((IPause) command);
						break;
					}
					else
						if (iName.equalsIgnoreCase(IREPETITION))
						{
							mC = computeRepetitionCommand(
									(IRepetition) command, commandList);
							break;
						}
		}
		
		return mC;
	}
	
	private MotorCommand computeDirectionCommand(IDirection command)
	{
		// bei positiven werten dreht die arduino software die mobile plattform
		// nach links, statt nach rechts (umgekehrte logik) -> -1
		int degree = (command.getDegree()) * -1;
		int pause = 2000;
		
		MotorCommand mC = new MotorCommand(command, pause, 0, degree, 0, 0, 0);
		return mC;
	}
	
	private MotorCommand computeGearCommand(IGear command)
	{
		// die arduino software setzt alle werte kleiner 60 auf 60 (, da dies
		// das
		// minimum ist, ab dem sich das fahrzeug bewegt). damit trotzdem der
		// volle
		// zahlenraum von 0 bis 100 zur verf�gung steht, werden in der
		// berechnung
		// die (max) 100% auf einen wert von 0 bis 40 umgerechnet und auf den
		// min
		// wert von 60 aufaddiert
		int speed = 0;
		if (command.getSpeed() >= 0)
			speed = 60 + (int) (command.getSpeed() * 0.4);
		else
			speed = -60 + (int) (command.getSpeed() * 0.4);
		
		int pause = (int) (2000 + command.getDuration() * 1000);
		
		MotorCommand mC = new MotorCommand(command, pause, speed, 0,
				(int) command.getDuration() * 1000, 0, 0);
		return mC;
	}
	
	private MotorCommand computePauseCommand(IPause command)
	{
		int pause = (int) (command.getDuration() * 1000);
		
		MotorCommand mC = new MotorCommand(command, pause, 0, 0,
				(int) command.getDuration() * 1000, 0, 0);
		return mC;
	}
	
	private MotorCommand computeRepetitionCommand(IRepetition command,
			Vector<ICommand> commandList)
	{
		int index = commandList.indexOf(command);
		if (index < -1) // command nicht teil der commandList
			return null;
		
		int steps = command.getNrSteps();
		if ((index - steps) < 0) // anzahl auszuf�hrender steps gr��er als
									// aktuelle position
			return null;
		
		// keine geschachtelten schleifen
		for (int i = index - steps; i < index; i++)
		{
			Class<?>[] interfaces = commandList.elementAt(i).getClass()
					.getInterfaces();
			
			for (int j = 0; j < interfaces.length; j++)
			{
				String iPath = interfaces[j].getName();
				String iName = iPath.substring(iPath.lastIndexOf(".") + 1);
				
				if (iName.equalsIgnoreCase(IREPETITION))
					return null;
			}
		}
		
		MotorCommand mC = new MotorCommand(command, 0, 0, 0, 0,
				command.getNrSteps(), command.getNrRepetitions());
		return mC;
	}
}

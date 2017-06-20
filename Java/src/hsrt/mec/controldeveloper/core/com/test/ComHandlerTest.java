package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.ComHandler;
import hsrt.mec.controldeveloper.core.com.ComPort;
import hsrt.mec.controldeveloper.core.com.ComPortHandler;
import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.SerialUSB;
import hsrt.mec.controldeveloper.io.TextFile;
import hsrt.mec.controldeveloper.util.Log;

import java.io.File;
import java.util.Vector;

class ComHandlerTest
{
	private static Vector<ICommand>	v = new Vector<ICommand>();
	
	// switch to write to SerialUSB or to TextFile
	private static final boolean	TO_SERIALUSB	= false;
	
	private static void setTestData()
	{
		v.addElement(new Direction("Direction", 100));
		v.addElement(new Gear("Gear", 90, 3.0));
		v.addElement(new Pause("Pause", 3.0));
		v.addElement(new Gear("Gear", 90, 3.0));
		// v.addElement(new Repetition("Repetition", 2, 2));
	}
	
	public static void main(String[] s)
	{
		setTestData();
		
		ComPort[] comPorts = ComPortHandler.getPorts();
		Log.log("Number Ports: " + comPorts.length);
		for (ComPort comPort : comPorts)
			Log.log("Available Com-Port... " + comPort.getDescription());
		
		IOType ioType;
		// set port [0] if available ...
		if (TO_SERIALUSB)
		{
			if (comPorts.length == 0)
				return;
			
			Log.log("Selected Com-Port... " + comPorts[0].getName());
			ioType = new SerialUSB(comPorts[0]);
		}
		else
		{
			File f = new File(System.getProperty("user.dir") + "\\test.txt");
			Log.log("Selected File... " + f);
			
			ioType = new TextFile(f, false);
		}
		
		// send data to port or file ...
		ComHandler.getInstance().start(v, ioType);
	}
}

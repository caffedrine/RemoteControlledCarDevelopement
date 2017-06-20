package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.ComHandler;
import hsrt.mec.controldeveloper.core.com.ComPort;
import hsrt.mec.controldeveloper.core.com.ComPortHandler;
import hsrt.mec.controldeveloper.core.com.IComListener;
import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.TextFile;
import hsrt.mec.controldeveloper.io.SerialUSB;
import hsrt.mec.controldeveloper.util.Log;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

@SuppressWarnings("serial")
class ControlGUI extends JFrame implements IComListener
{
	private Vector<ICommand> v = new Vector<ICommand>();
	private static final boolean TO_SERIALUSB = true;
	
	private JTextField tF = new JTextField(30);
	private JButton stop = new JButton("Stop");
	private JButton start = new JButton("Start");
	
	ControlGUI()
	{
		fillTestData();
		
		ComHandler cH = ComHandler.getInstance();
		cH.register(this);
		
		ComPort[] comPorts = ComPortHandler.getPorts();
		for (ComPort comPort : comPorts)
			Log.log("Available Com-Port... " + comPort.getDescription());
		
		// verwendung des ersten ports:
		/*
		 * if (comPorts.length==0) return; else
		 * Log.log("Selected Com-Port... "+comPorts[0].getName());
		 */
		buildGUI();
	}
	
	private void fillTestData()
	{
		v.addElement(new Gear("Gear", 100, 4.0));
		v.addElement(new Direction("Direction", 100));
		v.addElement(new Pause("Pause", 3.0));
		v.addElement(new Gear("Gear", 90, 3.0));
		v.addElement(new Repetition("Repetition", 2, 2));
	}
	
	private IOType getIOType()
	{
		ComPort[] comPorts = ComPortHandler.getPorts();
		for (ComPort comPort : comPorts)
			Log.log("Available Com-Port... " + comPort.getDescription());
		
		IOType ioType;
		// setzen eines ports:
		if (TO_SERIALUSB)
		{
			if (comPorts.length == 0)
				return null;
			
			Log.log("Selected Com-Port... " + comPorts[0].getName());
			ioType = new SerialUSB(comPorts[0]);
		}
		else
		{
			File f = new File(System.getProperty("user.dir") + "\\test.txt");
			Log.log("Selected File... " + f.getName());
			
			ioType = new TextFile(f, true);
		}
		
		return ioType;
	}
	
	private void buildGUI()
	{
		setTitle("Test App");
		setLayout(new GridLayout(2, 1));
		
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(new JLabel("Command: "));
		p1.add(tF);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		p2.add(stop);
		p2.add(start);
		
		add(p1);
		add(p2);
		
		start.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tF.setText("");
				
				// start process
				ComHandler.getInstance().start(v, getIOType());
			}
		});
		
		stop.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ComHandler.getInstance().stop();
			}
		});
	}
	
	public void commandPerformed(ICommand command)
	{
		tF.setText(command.getName());
	}
	
	public static void main(String[] args)
	{
		ControlGUI cGUI = new ControlGUI();
		
		cGUI.pack();
		cGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cGUI.setVisible(true);
	}
}

package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.io.IOType;
import hsrt.mec.controldeveloper.io.WiFi;
import hsrt.mec.controldeveloper.util.Globals;
import hsrt.mec.controldeveloper.util.Log;
import hsrt.mec.controldeveloper.util.Message;
import hsrt.mec.controldeveloper.util.Status;

import java.util.Vector;

class BackgroundControl extends Thread
{
	private Vector<MotorCommand> motorCommands = null;
	private Vector<String> data = new Vector<String>();
	private boolean cancelled = false;
	private IOType ioType = null;
	private ComHandler cH = null;
	
	private boolean commandSend = false;
	private boolean commandExecuted = false;
	
	BackgroundControl(ComHandler comHandler, IOType iType, Vector<MotorCommand> motorCommands)
	{
		if (comHandler == null)
			cancelled = true;
		if (motorCommands == null)
			cancelled = true;
		
		this.motorCommands = motorCommands;
		this.ioType = iType;
		this.cH = comHandler;
	}
	
	public void run()
	{
		try
		{	
			// initialisiere
			//Thread.sleep(Globals.STEPTIMER);
			Log.log("Nr. commands: " + motorCommands.size());
			
			int step = 0;
			while (step < motorCommands.size())
			{
				if (isCancelled())
				    break;

			    commandSend = commandExecuted = false;
				
				if (motorCommands.elementAt(step).getNrRepetitions() > 0)
				{
					motorCommands.elementAt(step).decNrRepetitions();
					step = step - motorCommands.elementAt(step).getNrSteps();
				}
				
				cH.update(motorCommands.elementAt(step).getICommand());
				data.removeAllElements();
				data.addElement("[" + motorCommands.elementAt(step).getCommandString() + "]");
				
				Log.log("Sending command: " + step);
				
				//trying to send command to car. 5 attempts are allowed in case of connection lost
		        int attempts = 0;
		        while(!ioType.write(data))
		        {
		            Log.log("Conn lost! Attempting to reconnect: " + attempts);
		            
		            if(attempts++ == 5)
		            {
		                Log.log("All attempts failed! Command " + step + " not send!");
		                Log.log("Operation cancelled...");
		                cancelled = true;
		                break;
		            }
		            
		            //Attempt reconnection every 3 seconds
		            try { Thread.sleep(3000); }
		            catch (InterruptedException e){Log.log("exceptioooooon!");}
		        }
		        if(cancelled)
		            break;
		        
		        //If we are here it means that command was send
				Log.log("Write... " + motorCommands.elementAt(step).getCommandString());

				//Wait for response and make sure we got confirmations
				waitForResponse(motorCommands.elementAt(step).getPause());	
				if(!commandSend || !commandExecuted)
				{
				    Log.log("Operation canceled! Car failed to respond in time...");
				    break;
				}
				step++;
				
                try { Thread.sleep(3000); }
                catch (InterruptedException e){Log.log("exceptioooooon!");}
			}
			Log.log("Thread work done!");
		}
		catch (Exception e)
		{
			Status.getInstance().setStatus(Status.E_NOK,Message.mTHREAD_EXEC_ERROR);
			Log.log(Status.getInstance().getMessage());
		}
		finally
		{
			if (ioType != null)
				ioType.close();
		}
	}
	
	synchronized void cancel()
	{
		this.cancelled = true;
	}
	
	synchronized boolean isCancelled()
	{
		return cancelled;
	}
	
	private void waitForResponse(int timeout)
	{
	    if(ioType.getClass().getSimpleName() == "ObjectFile")
	    {
	        commandSend = commandExecuted = true;
	        try { Thread.sleep(timeout); }
            catch (InterruptedException e) { Log.log("Exception on thread sleep!"); }
	    }
	    
	    Log.log("Waiting for response...");
	    
	    timeout += 5000;
	    long previousMillis = 0;
	    
	    while(true)
	    {
	        if( (System.currentTimeMillis() % 1000) - previousMillis > timeout)
	        {
	            previousMillis = System.currentTimeMillis() % 1000;
	            break;
	        }
	        
	        Vector<String> recvData = new Vector<String>(); 
	        if(!ioType.read(recvData))
	            continue;
	        
	        if(!this.commandSend)
	        {
	            if(recvData.elementAt(0).contains("received"))
	            {
	                Log.log("Command seccessfully send!");
	                Log.log("Waiting for execution...");
	                this.commandSend = true;
	            }
	        }
	        
	        if(!this.commandExecuted)
	        {
                if(recvData.elementAt(0).contains("executed"))
                {
                    Log.log("Command seccessfully executed!");
                    this.commandExecuted = true;
                    break;
                }
	        }
	    }
	}
}

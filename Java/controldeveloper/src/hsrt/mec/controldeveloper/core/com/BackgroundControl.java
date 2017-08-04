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
	
	private boolean commandSend = false;           // This value is updated if command were successfully send to car
	private boolean commandExecuted = false;       // This value is updated if command were succesffully executed
	
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

			    commandSend = false;
			    commandExecuted = false;   // Set flags to know command was not yet executed nor send
				
				if (motorCommands.elementAt(step).getNrRepetitions() > 0)
				{
					motorCommands.elementAt(step).decNrRepetitions();
					step = step - motorCommands.elementAt(step).getNrSteps();
				}
				
				cH.update(motorCommands.elementAt(step).getICommand());
				data.removeAllElements();
				data.addElement( motorCommands.elementAt(step).getCommandString() );
							
				Log.log("Sending command: " + step);
				
				//trying to send command to car. 5 attempts are allowed in case of connection lost
		        int attempts = Globals.ATTEMPTS;
		        while(!ioType.write(data))        // while command was not send try again
		        {
		            Log.log("Conn lost! Attempting to reconnect: " + attempts);
		            
		            if(attempts++ == Globals.ATTEMPTS)
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
		        
		        if(cancelled) // if operation canceled
		            break;
		        
		        //If we are here it means that command was send
				Log.log("Write... " + motorCommands.elementAt(step).getCommandString());

				//Wait for response and make sure we got confirmations
				/**
				 * The response should contain the following strings: 
				 *  >>received - if command was received (if the only string returned is received, the is must start with ">>"
				 *  >>executed - if command was executed
				 *  
				 *  If those conditions are meet, program will continue.
				 */
				waitForResponse(motorCommands.elementAt(step).getDuration());	// this function will block the loop until response is received or timeout reached
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
	
	/**
	 * This function is waiting for response from IO Type. This is a wrapper for that IOType.
	 * @param timeout The time necessary for car to perform the given command.
	 */
	private void waitForResponse(long timeout)
	{
	    /**
	     * The feedback is only expected for WiFi, SerialUSB.
	     * For the rest of files, response is true by default in order to be able to cntinue with
	     * next step.
	     */
	    if(ioType.getClass().getSimpleName() == "ObjectFile")
	    {
	        commandSend = commandExecuted = true;
	        try { Thread.sleep(timeout); }
            catch (InterruptedException e) { Log.log("Exception on thread sleep!"); }
	    }
	    
	    Log.log("Waiting for response...");
	    
	    /**
	     * Time out have two main components:
	     * 1. Time required to execute the command
	     * 2. Time to wait after command should have been executed
	     * Therefore the timeout is the sum of these two values.
	     */
	    timeout += Globals.TIMEOUT;
	    
	    Log.log("Timeout: " + timeout);
	    
	    long previousMillis = System.currentTimeMillis();   // Starting time. Used to count timeout
	    while(true)
	    {
	        // Check for timeout in case response not received
	        if( (System.currentTimeMillis()) - previousMillis > timeout)    // not received response in time
	        {
	            break;
	        }

	        Vector<String> recvData = new Vector<String>(); 
	        if(!ioType.read(recvData))     // If successfully read socket, append data to vector
	            continue;
	        
	        if(!this.commandSend)      // If not confirmation for command
	        {
	            if(recvData.elementAt(0).contains("received"))
	            {
	                Log.log("Command seccessfully send!");
	                Log.log("Waiting for execution...");
	                this.commandSend = true;
	            }
	        }
	        
	        if(!this.commandExecuted)  // If not confirmation for execution
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

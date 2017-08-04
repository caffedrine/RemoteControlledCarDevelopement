package hsrt.mec.controldeveloper.core.com;

import java.sql.Ref;

import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.util.Globals;

class MotorCommand
{
    private ICommand reference;
    
    private String rotationLeft;
    private String rotationRight;
    private String duration;
    private int pause;
    
    private int nrSteps;
    private int nrRepetitions;
    
    MotorCommand(ICommand ref, int pause, int rL, int rR, int duration,
            int nrSteps, int nrRepetitions)
    {
        this.reference = ref;
        this.pause = pause;
        this.rotationLeft = new Integer(rL).toString();
        this.rotationRight = new Integer(rR).toString();
        this.duration = new Integer(duration).toString();
        
        this.nrSteps = nrSteps;
        this.nrRepetitions = nrRepetitions;
    }
    
    /*
     * Old get command string
     */
    String getCommandString2()
    {
        return rotationLeft.concat(Globals.DELEMITER).concat(rotationRight)
                .concat(Globals.DELEMITER).concat(duration);
    }
    
    /*
     * New command string  forma
     */
    String getCommandString()
    {
        /*
         * Alex: I have changed commands structure into: [<CommandFirstLetter>[degrees]<duration>]
         */
        
        int rlInt = Integer.parseInt(this.rotationLeft);    // Need to work with ints
        int rrInt = Integer.parseInt(this.rotationRight);
        
        String comm;
        comm  = "[";
        comm += this.reference.getName().substring(0, 1); // Get first letter of command name
        comm += Globals.DELEMITER;
        comm += new Integer( (rlInt!=0)?(rlInt):(rrInt)).toString();  // it rotation left then return degree with minus sign else return rotation right
        comm += Globals.DELEMITER;
        comm += this.duration;
        comm += "]";
        
        return comm;
    }
    
    int getPause()
    {
        return pause;
    }
    
    int getDuration() 
    {
        return new Integer(duration);
    }
    
    ICommand getICommand()
    {
        return reference;
    }
    
    int getNrSteps()
    {
        return nrSteps;
    }
    
    int getNrRepetitions()
    {
        return nrRepetitions;
    }
    
    void decNrRepetitions()
    {
        nrRepetitions--;
    }
}

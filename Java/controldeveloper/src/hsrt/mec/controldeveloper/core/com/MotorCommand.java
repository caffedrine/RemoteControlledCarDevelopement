package hsrt.mec.controldeveloper.core.com;

import hsrt.mec.controldeveloper.core.com.command.ICommand;
import hsrt.mec.controldeveloper.util.Globals;

class MotorCommand {
  private ICommand reference;

  private String rotationLeft;
  private String rotationRight;
  private String duration;
  private int pause;
  
  private int nrSteps;
  private int nrRepetitions;
  
  MotorCommand(ICommand ref, int pause, int rL, int rR, int duration, int nrSteps, int nrRepetitions)
  {
    this.reference    = ref;
    this.pause        = pause;
    this.rotationLeft = new Integer(rL).toString();
    this.rotationRight= new Integer(rR).toString();
    this.duration     = new Integer(duration).toString();
    
    this.nrSteps      = nrSteps;
    this.nrRepetitions= nrRepetitions;
  }
  
  
  String getCommandString(){
    return rotationLeft.concat(Globals.DELEMITER).concat(rotationRight).concat(Globals.DELEMITER).concat(duration);
  }
  
  
  int getPause(){
    return pause;
  }
  
  
  ICommand getICommand(){
    return reference;
  }
  
  
  int getNrSteps(){
    return nrSteps;
  }
  
  
  int getNrRepetitions(){
    return nrRepetitions;
  }
  
  
  void decNrRepetitions(){
    nrRepetitions--;
  }
}

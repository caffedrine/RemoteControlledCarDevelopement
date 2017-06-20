package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.command.IRepetition;

class Repetition extends Command implements IRepetition{
  private int nrSteps;
  private int nrRepetitions;
  
  Repetition(String name, int nrSteps, int nrRepetitions){
    super.name= name;
    this.nrSteps= nrSteps;
    this.nrRepetitions= nrRepetitions;
  }
  
  public int getNrSteps(){
    return nrSteps;
  }
  
  public int getNrRepetitions(){
    return nrRepetitions;
  }
}

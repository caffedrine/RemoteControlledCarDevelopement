package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.command.ICommand;

class Command implements ICommand
{
  protected String name;
  
  public String getName()
  {
    return name;
  }
}

package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.command.IPause;

class Pause extends Command implements IPause
{
    private double duration;
    
    Pause(String name, double duration)
    {
        super.name = name;
        this.duration = duration;
    }
    
    public double getDuration()
    {
        return duration;
    }
    
    public String getName()
    {
        return name;
    }
}

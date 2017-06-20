package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.command.IGear;

class Gear extends Command implements IGear
{
	private int speed;
	private double duration;
	
	Gear(String name, int speed, double duration)
	{
		super.name = name;
		this.speed = speed;
		this.duration = duration;
	}
	
	public int getSpeed()
	{
		return speed;
	}
	
	public double getDuration()
	{
		return duration;
	}
}

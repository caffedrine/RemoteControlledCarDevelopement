package hsrt.mec.controldeveloper.core.com.test;

import hsrt.mec.controldeveloper.core.com.command.IDirection;

class Direction extends Command implements IDirection
{
	private int degree;
	
	Direction(String name, int degree)
	{
		super.name = name;
		this.degree = degree;
	}
	
	public int getDegree()
	{
		return degree;
	}
	
	public String getName()
	{
	    return name;
	}
}

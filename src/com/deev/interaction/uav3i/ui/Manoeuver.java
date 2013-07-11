package com.deev.interaction.uav3i.ui;

import java.awt.Graphics2D;

public class Manoeuver
{
	protected boolean _adjusting = false;
	protected static double GRIP = 20.;
	
	public void paint(Graphics2D g2)
	{
		
	}
	
	public boolean adjustAtPx(double x, double y)
	{
		return false;
	}
	
	public boolean isAdjusting()
	{
		return _adjusting;
	}
	
	public void stopAdjusting()
	{
		_adjusting = false;
	}
}

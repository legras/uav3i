package com.deev.interaction.uav3i.ui;

import java.awt.Graphics2D;

import com.deev.interaction.common.ui.Touchable;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;

public abstract class Manoeuver implements Touchable
{
	public static float ADJUST_INTEREST = 20.f;
	public static float MOVE_INTEREST = 15.f;
	
	protected boolean _adjusting = false;
	protected static double GRIP = 20.;
	
	public abstract void paint(Graphics2D g2);
	
	public abstract boolean adjustAtPx(double x, double y);
	
	public boolean isAdjusting()
	{
		return _adjusting;
	}
	
	public void stopAdjusting()
	{
		_adjusting = false;
	}
	
	public abstract boolean isAdjustmentInterestedAtPx(double x, double y);
	
	private float getGeneralInterest()
	{
		if (MainFrame.getAppState() == MainFrameState.COMMAND)
			return MOVE_INTEREST;
		else
			return -1.f;
	}
}

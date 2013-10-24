package com.deev.interaction.uav3i.ui;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Touchable;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;

public abstract class Manoeuver implements Touchable, Animation
{
	protected enum ManoeuverStates {READY, SUBMITTED, REJECTED, FADING};
	protected ManoeuverStates _mnvrState = ManoeuverStates.READY;
	
	public static float ADJUST_INTEREST = 20.f;
	public static float MOVE_INTEREST = 15.f;
	
	protected boolean _adjusting = false;
	protected static double GRIP = 20.;
	
	private Object _touchref;
	private Rectangle2D _shakeArea;
	private double _shakeLength;
	private Point2D.Double _shakedLast;
	
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
	
	/**
	 * @return common value for manoeuvers concerning moves 
	 */
	protected float getGeneralInterest()
	{
		if (MainFrame.getAppState() == MainFrameState.COMMAND)
			return MOVE_INTEREST;
		else
			return -1.f;
	}
	
	
	protected void didShake()
	{
		System.out.println("THIS WAS SHAKEN!!!!");
	}

	public void addTouch(float x, float y, Object touchref)
	{
		_touchref = touchref;
		_shakeLength = 0;
		_shakedLast = new Point2D.Double(x, y);
		_shakeArea = new Rectangle2D.Double(x, y, 0, 0);
	}

	public void updateTouch(float x, float y, Object touchref)
	{
		if (touchref != _touchref)
			return;
		
		_shakeLength += _shakedLast.distance(x, y);
		_shakedLast = new Point2D.Double(x, y);
		_shakeArea.add(_shakedLast);
		
		if (_shakeLength > 2 * (_shakeArea.getWidth()+_shakeArea.getHeight()))
			didShake();
	}

	@Override
	public int tick(int time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int life() {
		// TODO Auto-generated method stub
		return 0;
	}
}

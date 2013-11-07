package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
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
	
	private static long LONGPRESS = 1500;
	
	protected boolean _adjusting = false;
	protected static double GRIP = 20.;
	
	private Object _touchref;
	private Rectangle2D _shakeArea;
	private double _shakeLength;
	private Point2D.Double _touchedLast;
	private long _startTime;
	
	public abstract void paint(Graphics2D g2);
	
	public void paintFootprint(Graphics2D g2, Shape footprint)
	{
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(1.0f, 0.f, 0.f, 1.0f));
		g2.draw(footprint);
		g2.setPaint(new Color(1.0f, 0.1f, 0.1f, 0.2f));
		g2.fill(footprint);
	}
	
	public void paintAdjustLine(Graphics2D g2, Shape line)
	{
		g2.setStroke(new BasicStroke(2.f*(float)GRIP));
		if (!_adjusting)
			g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.3f));
		else
			g2.setPaint(new Color(1.0f, 1.0f, 0.7f, 0.3f));
		g2.draw(line);

		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(0.0f, 0.f, 1.0f, 1.0f));
		g2.draw(line);
	}
	
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
		_startTime = System.currentTimeMillis();
		_shakeLength = 0;
		_touchedLast = new Point2D.Double(x, y);
		_shakeArea = new Rectangle2D.Double(x, y, 0, 0);
	}

	public void updateTouch(float x, float y, Object touchref)
	{
		if (touchref != _touchref)
			return;
		
		_shakeLength += _touchedLast.distance(x, y);
		_touchedLast = new Point2D.Double(x, y);
		_shakeArea.add(_touchedLast);
		
		long time = System.currentTimeMillis();
		
		if (_mnvrState == ManoeuverStates.READY && time-_startTime > LONGPRESS)
		{
			_mnvrState = ManoeuverStates.SUBMITTED;
			System.out.println("sub");
		}
		
		// Si le geste est replié sur lui-même
		double L = _shakeArea.getWidth()+_shakeArea.getHeight();
		if (_shakeLength > GRIP && _shakeLength > 2*L)
		{
			didShake();
			return;
		}
	}

	@Override
	public int tick(int time)
	{
		switch (_mnvrState)
		{
			case SUBMITTED:
				break;
				
			case READY:
			default:
				break;
		}

		return 1;
	}

	@Override
	public int life()
	{
		return 1;
	}
}

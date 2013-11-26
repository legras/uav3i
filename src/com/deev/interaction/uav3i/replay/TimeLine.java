package com.deev.interaction.uav3i.replay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.ui.Palette3i;

public class TimeLine extends JComponent implements Touchable, Animation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -923278315604054575L;
	
	private enum TimeLineState {HIDDEN, SHOWING, ACTIVE, HIDING};
	private TimeLineState _state;
	private double _vOffset;
	
	protected static double _Y = 102;
	protected static double _HEIGHT = 80;
	
	private double _timeOrigin; // Screen Left in milliseconds
	private double _timeScale;  // milliseconds / pixel
	private double _timeOriginTgt; // for animations
	private double _timeScaleTgt;  // for animations
	
	private double _MIN_TIME_SEG_WIDTH = 150;

	public TimeLine(float screenWidth, float screenHeight)
	{
		super();

		_state = TimeLineState.HIDDEN;
		_vOffset = _Y;
		
		setBounds(0, 0, (int) screenWidth, (int) screenHeight);
		
		_timeOriginTgt = _timeOrigin = System.currentTimeMillis();
		_timeScaleTgt = _timeScale = 10*60*1000 / 360.;
	}


	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		float width = this.getBounds().width;	
		
		AffineTransform old = g2.getTransform();
		
		g2.translate(0, this.getBounds().height-_Y+_vOffset);

		/**************** Segments background ******************
		 * Ils vont par deux : un clair puis un foncé.
		 * Taille minimumn de segment à l'écran: _MIN_TIME_SEG_WIDTH
		 */
		
		final double timeSegmentsDurations[] = {10*1000, 60*1000, 10*60*1000, 60*60*1000};
		int timeSegmentIndex = 0;
		
		while (timeSegmentsDurations[timeSegmentIndex] / _timeScale < _MIN_TIME_SEG_WIDTH)
			timeSegmentIndex++;
		
		if (timeSegmentIndex >= timeSegmentsDurations.length)
			timeSegmentIndex = timeSegmentsDurations.length-1;

		double xSegstart = pixelForTime(_timeOrigin  % timeSegmentsDurations[timeSegmentIndex]);
		double segWidth = timeSegmentsDurations[timeSegmentIndex] / _timeScale;
				
		for (double x=xSegstart; x<=width; x+=segWidth)
		{
			g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHT));
			g2.fill(new Rectangle2D.Double(x, 0, segWidth/2., _HEIGHT));
			g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHTER));
			g2.fill(new Rectangle2D.Double(x+segWidth/2., 0, segWidth/2., _HEIGHT));
		}
		
		g2.setTransform(old);
	}

	private double timeForPixel(double x)
	{
		return _timeOrigin + x * _timeScale;
	}
	
	private double pixelForTime(double t)
	{
		return t - _timeOrigin / _timeScale;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_state != TimeLineState.ACTIVE)
			return -1.f;
		
		Rectangle rect = this.getBounds();
		float interest = -1.f;

		if (y > rect.height - _Y)
			interest = 10.f;
		
		return interest;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{

	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{

	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{

	}

	@Override
	public void cancelTouch(Object touchref)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int tick(int time)
	{
		switch (_state)
		{
			case HIDDEN : 
				_vOffset = _Y;
				break;
			case SHOWING : 
				_vOffset /= 2.;
				if (_vOffset < 1.f)
					_state = TimeLineState.ACTIVE;
				break;
			case ACTIVE : 
				_vOffset = 0.f;
				break;
			case HIDING : 
				_vOffset += (_Y-_vOffset)/2.f;
				if (_vOffset > _Y-1.f)
					_state = TimeLineState.HIDDEN;
				break;
			default:
		}
		
		return 1;
	}

	@Override
	public int life()
	{
		return 1;
	}
	

	
	public void hide()
	{
		_state = TimeLineState.HIDING;
	}
	
	public void show()
	{
		_state = TimeLineState.SHOWING;
	}
}

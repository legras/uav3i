package com.deev.interaction.uav3i.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Touchable;

public class TimeLine extends JComponent implements Touchable, Animation
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -923278315604054575L;
	
	private enum TimeLineState {HIDDEN, SHOWING, ACTIVE, HIDING};
	private TimeLineState _state;
	private double _vOffset;
	
	protected static final double _Y = 102;
	protected static final double _HEIGHT = 80;
	
	private double _timeOrigin; // Screen Left in milliseconds
	private double _timeScale;  // milliseconds / pixel
	private double _timeOriginTgt; // for animations
	private double _timeScaleTgt;  // for animations
	
	private SimpleDateFormat _HHmm = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat _HHmmss = new SimpleDateFormat("HH:mm:ss");
	
	private double _MIN_TIME_SEG_WIDTH = 150;
	
	// Interaction
	private enum TimeLineScrubStates {NONE, TRANSLATE, FULL};
	private TimeLineScrubStates _scrubState;
	private Object _touchOne;
	private double _touchtimeOne;
	private Object _touchTwo;
	private double _touchtimeTwo;
	private double _startPosOne;
	private double _startPosTwo;
	private double _currentPosOne;
	private double _currentPosTwo;
	

	public TimeLine(int screenWidth, int screenHeight)
	{
		super();

		_state = TimeLineState.HIDDEN;
		_vOffset = _Y;
		_scrubState = TimeLineScrubStates.NONE;
		
		setBounds(0, 0, screenWidth, screenHeight);
		
		_timeOriginTgt = _timeOrigin = System.currentTimeMillis();
		_timeScaleTgt = _timeScale = 10*60*1000 / 360.;
	}


	@Override
	public void paintComponent(Graphics g)
	{
		if (_state == TimeLineState.HIDDEN)
			return;
		
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		float width = this.getBounds().width;	
		
		AffineTransform old = g2.getTransform();
		
		g2.translate(0, this.getBounds().height-_Y+_vOffset);

		/**************** Segments background ******************
		 * Ils vont par deux : un clair puis un foncé.
		 * Taille minimumn de segment à l'écran: _MIN_TIME_SEG_WIDTH
		 */
		
		final double timeSegmentsDurations[] = {2*10*1000, 2*60*1000, 2*10*60*1000, 2*60*60*1000};
		int timeSegmentIndex = 0;
		
		while (timeSegmentsDurations[timeSegmentIndex] / _timeScale < _MIN_TIME_SEG_WIDTH)
			timeSegmentIndex++;
		
		if (timeSegmentIndex >= timeSegmentsDurations.length)
			timeSegmentIndex = timeSegmentsDurations.length-1;

		double xSegstart = pixelForTime(_timeOrigin - (_timeOrigin  % timeSegmentsDurations[timeSegmentIndex]));
		double segWidth = timeSegmentsDurations[timeSegmentIndex] / _timeScale;

		FontRenderContext frc = g2.getFontRenderContext();
	    Font f = new Font("HelveticaNeue-UltraLight", Font.PLAIN, 18);
	    TextLayout textTl;
	    Shape outline;
						
		for (double x=xSegstart; x<=width; x+=segWidth)
		{
				g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHT));
				g2.fill(new Rectangle2D.Double(x, 0, segWidth / 2., _HEIGHT));
				g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHTER));
				g2.fill(new Rectangle2D.Double(x + segWidth / 2., 0, segWidth / 2., _HEIGHT));

				AffineTransform o = g2.getTransform();
				
				g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHT_TEXT));
				g2.translate(x + 2, _HEIGHT - 4);
				textTl = new TextLayout(_HHmm.format(new Date((long) timeForPixel(x))), f, frc);
				outline = textTl.getOutline(null);
				g2.fill(outline);
				
				g2.translate(segWidth / 2., 0);
				textTl = new TextLayout(_HHmm.format(new Date((long) timeForPixel(x+segWidth/2.))), f, frc);
				outline = textTl.getOutline(null);
				g2.fill(outline);
				
				g2.setTransform(o);
		}
		
		g2.setTransform(old);
	}

	private double timeForPixel(double x)
	{
		return _timeOrigin + x * _timeScale;
	}
	
	private double pixelForTime(double t)
	{
		return (t - _timeOrigin) / _timeScale;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_state != TimeLineState.ACTIVE)
			return -1.f;
		
		float interest = -1.f;

		if (y > this.getBounds().height - _Y)
			interest = 10.f;
		
		return interest;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		switch (_scrubState)
		{
			case FULL:
				return;
			case TRANSLATE:
				_touchTwo = touchref;
				_startPosTwo = x;
				_currentPosOne = _startPosOne;
				_currentPosTwo = _startPosTwo;
				_scrubState = TimeLineScrubStates.FULL;
				return;
			case NONE:
				_touchOne = touchref;
				_startPosOne = x;
				_scrubState = TimeLineScrubStates.TRANSLATE;
				return;
			default:
				return;
		}
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
				if (_vOffset < 1.)
					_state = TimeLineState.ACTIVE;
				break;
			case ACTIVE : 
				_vOffset = 0.;
				break;
			case HIDING : 
				_vOffset += (_Y-_vOffset)/2.;
				if (_vOffset > _Y-1.)
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
		if (_state != TimeLineState.HIDDEN)
			_state = TimeLineState.HIDING;
	}
	
	public void show()
	{
		if (_state != TimeLineState.ACTIVE)
			_state = TimeLineState.SHOWING;
	}
}

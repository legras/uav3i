package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Rectangle2D;

import javax.sound.sampled.Line;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.FingerStroke;
import com.deev.interaction.common.ui.Gesture;
import com.deev.interaction.common.ui.TouchPoint;




public class FingerDrawing implements Animation
{
	private static int _LIFE_ = 7; // milliseconds ?
	
	public enum GestureDrawState {DRAWING, RELEASED, OVER};

	private GestureDrawState _state;
	private int _life;
	private Gesture _gesture;
	private Rectangle2D _box;

	private float[] _color = {1.f, 1.f, 1.f, .5f};
	
	public FingerDrawing(double x, double y)
	{
		_state = GestureDrawState.DRAWING;
		_gesture = new Gesture();
		_box = new Rectangle2D.Double(x, y, 0, 0);
		addPoint(x, y);
		Animator.addAnimation(this);
	}
	
	public FingerDrawing(double x, double y, Color color)
	{
		this(x, y);
		color.getColorComponents(_color);
	}

	
	public void addPoint(double x, double y)
	{
		int previous = _gesture.size()-1;
		if (previous > 0 && _gesture.get(previous).distance(x, y) < 3)
			return;
		
		_gesture.addPoint(x, y);
		_box.add(x, y);
	}
	
	public List<TouchPoint> getPoints()
	{
		return _gesture;
	}
	
	public Gesture getGesture()
	{
		return _gesture;
	}
	
	public void release()
	{
		switch (_state)
		{
			case DRAWING:
				_state = GestureDrawState.RELEASED;
				_life = _LIFE_*_gesture.size();
				break;
			default:
				break;
		}
	}
	
	@Override
	public int life()
	{
		switch (_state)
		{
			case DRAWING: 	return 1;
			case OVER: 		return -1;
			default: 		return _life;
		}
	
	}

	public void paintAnimation(Graphics2D g2)
	{
		int i, start = 0;
		Point2D.Double p;
				
		switch (_state)
		{
			case OVER: 		return;
			case DRAWING:	start = 0;
							break;
			case RELEASED:	start = (_LIFE_*_gesture.size()-_life)/_LIFE_;
							break;
		}
		
		if (start < 0) start = 0;
		if (start >= _gesture.size())
			start = _gesture.size()-1;
		
		GeneralPath path = new GeneralPath();
		p = _gesture.get(start);
		path.moveTo(p.x, p.y);
		for (i=start+1; i<_gesture.size(); i++)
		{
			p = _gesture.get(i);
			path.lineTo(p.x, p.y);
		}
		
		Point2D.Double s = _gesture.get(start);
		
		if (_state == GestureDrawState.DRAWING && !_gesture.isOpen())
		{
//			g2.setPaint(new Color(1.f, 1.f, 1.f, .1f));
//			g2.fill(new Ellipse2D.Double(p.x-Gesture.GAP, p.y-Gesture.GAP, 2.*Gesture.GAP, 2.*Gesture.GAP));
			
			g2.setPaint(new Color(1.f, 1.f, 1.f, .4f));
//			g2.setStroke(new BasicStroke(20.f, BasicStroke.CAP_ROUND,
//                BasicStroke.JOIN_ROUND, 1.f,
//                null, 0.f));
//			g2.draw(new Line2D.Double(s.x, s.y, p.x, p.y));
			
			g2.fill(_box);
		}
		
		g2.setStroke(new FingerStroke(20.f));
		g2.setPaint(new Color(_color[0], _color[1], _color[2], _color[3]));
		
		g2.draw(path);
		g2.setPaint(null);
	}

	@Override
	public int tick(int time)
	{		
		if (_state == GestureDrawState.RELEASED && _life < 0)
			_state = GestureDrawState.OVER;
				
		switch (_state)
		{
			case RELEASED:
				_life -= time;
				return _life;
			case DRAWING:
				return 1;
			default:
				return -1;
		}
	}
}

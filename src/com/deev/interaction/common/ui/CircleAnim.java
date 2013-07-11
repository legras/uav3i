package com.deev.interaction.common.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;



public class CircleAnim implements Animation
{
	private static int _LIFE_ = 1000; // milliseconds ?
	private static double _RADIUS_ = 100.;

	private float[] _color = {.2f, .2f, .2f, .5f};

	double _x;
	double _y;
	int _life;
	
	public CircleAnim(double x, double y)
	{
		_x = x;
		_y = y;
		_life = _LIFE_;
	}
	
	public CircleAnim(double x, double y, Color color)
	{
		this(x, y);
		color.getColorComponents(_color);
	}

	
	public void paintAnimation(Graphics2D g2)
	{
		if (_life < 0)
			return;
		
		double r;
		
		r = _RADIUS_*(1.- (double)_life/_LIFE_);

		g2.setStroke(new BasicStroke(4.0f));
		g2.setPaint(new Color(_color[0], _color[1], _color[2], (float)_color[3]*_life/_LIFE_));
		Ellipse2D.Double circle = new Ellipse2D.Double((double) _x-r, (double) _y-r, 2.*r, 2.*r);
		g2.draw(circle);
		g2.setPaint(null);
	}

	public int tick(int time)
	{
		return _life -= time;
	}

	@Override
	public int life()
	{
		return _life;
	}

}

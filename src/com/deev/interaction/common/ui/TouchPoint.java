package com.deev.interaction.common.ui;

import java.awt.geom.Point2D;

public class TouchPoint extends Point2D.Double
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5262800975007530302L;
	public long date;
	
	public TouchPoint(double x, double y, long t)
	{
		super(x, y);
		date = t;
	}
	
	public TouchPoint(double x, double y)
	{
		this(x, y, System.currentTimeMillis());
	}
	
	public static double dist2(Point2D.Double a, Point2D.Double b)
	{
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y);
	}
}
package com.deev.interaction.common.ui;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

public class BoundingRectangle
{
	public double height;
	public double width;
	public double theta;
	public double x;
	public double y;
	private ArrayList<Point2D.Double> _polygon;
	
	public BoundingRectangle()
	{
		height = 0;
		width = 0;
		theta = 0;
		x = 0;
		y = 0;
	}
	
	public BoundingRectangle(BoundingRectangle bgr)
	{
		height = bgr.height;
		width = bgr.width;
		theta = bgr.theta;
		x = bgr.x;
		y = bgr.y;
	}
	
	public BoundingRectangle(Polygon polygon)
	{
		this();
		
		_polygon = new ArrayList<Point2D.Double>();
		for (int i=0; i<polygon.npoints; i++)
			_polygon.add(new Point2D.Double(polygon.xpoints[i], polygon.ypoints[i]));
		
		ArrayList<Point2D.Double> hull = convexHull();
		
		BoundingRectangle ser = enclosingRectangleWithSegment(hull.get(hull.size()-1), hull.get(0), hull);
		
		Point2D.Double a, b;
		BoundingRectangle r;
		for (int i=0;i<hull.size()-1;i++)
		{
			a = hull.get(i);
			b = hull.get(i+1);
			r = enclosingRectangleWithSegment(a, b, hull);
			
			if (r.width*r.height < ser.width*ser.height)
				ser = r;
		}

		height = ser.height;
		width = ser.width;
		theta = ser.theta;
		x = ser.x;
		y = ser.y;
	}
	

	
	public BoundingRectangle(Polygon polygon, double padding)
	{
		this(polygon);
		
		this.width += 2.*padding;
		this.height += 2.*padding;
	}
	
	public BoundingRectangle(double angle, double uMin, double uMax, double vMin, double vMax)
	{
		theta = angle;
		width  = uMax-uMin;
		height = vMax-vMin;
		
		double u = (uMin+uMax)/2.;
		double v = (vMin+vMax)/2.;
		
		x = u*Math.cos(theta) - v*Math.sin(theta);
		y = u*Math.sin(theta) + v*Math.cos(theta);
	}
	
	
	public BoundingRectangle getSmallestEnclosingRectangle()
	{
		ArrayList<Point2D.Double> hull = convexHull();
		
		BoundingRectangle ser = enclosingRectangleWithSegment(hull.get(hull.size()-1), hull.get(0), hull);
		
		Point2D.Double a, b;
		BoundingRectangle r;
		for (int i=0;i<hull.size()-1;i++)
		{
			a = hull.get(i);
			b = hull.get(i+1);
			r = enclosingRectangleWithSegment(a, b, hull);
			
			if (r.width*r.height < ser.width*ser.height)
				ser = r;
		}
		
		return ser;
	}
	
	private BoundingRectangle enclosingRectangleWithSegment(Point2D.Double A, Point2D.Double B, ArrayList<Point2D.Double> hull)
	{
		double theta = -Math.atan2(B.x-A.x, B.y-A.y);
		
		Iterator<Point2D.Double> it = hull.iterator();
		Point2D.Double p;
		double u, v;
		
		p = it.next();
		u =  p.x*Math.cos(theta) + p.y*Math.sin(theta);
		v = -p.x*Math.sin(theta) + p.y*Math.cos(theta);
		
		double uMin = u;
		double uMax = u;
		double vMin = v;
		double vMax = v;
		
		while (it.hasNext())
		{
			p = it.next();
			u =  p.x*Math.cos(theta) + p.y*Math.sin(theta);
			v = -p.x*Math.sin(theta) + p.y*Math.cos(theta);
			
			uMin = u < uMin ? u : uMin;
			uMax = u > uMax ? u : uMax;
			vMin = v < vMin ? v : vMin;
			vMax = v > vMax ? v : vMax;
		}
		
		return new BoundingRectangle(theta, uMin, uMax, vMin, vMax);
	}
	
	/**
	 * Computes the convex hull of the points composing the gesture by using the gift wrap algorithm.
	 * @return an arraylist of Point2D.Double ordered CCW along the hull.
	 */
	public ArrayList<Point2D.Double> convexHull()
	{
		ArrayList<Point2D.Double> hull = new ArrayList<Point2D.Double>();
		
		Iterator<Point2D.Double> it = _polygon.iterator();
		Point2D.Double plow = it.next();
		Point2D.Double p = plow;
		while (it.hasNext())
		{
			p = it.next();
			if (p.y < plow.y)
				plow = p;
		}
		
		Point2D.Double pointOnHull = plow;
		
		MAIN: while (true)
		{
			it = _polygon.iterator();
			INNER: while (it.hasNext())
			{
				p = it.next();
				if (p == pointOnHull)
				{
					continue INNER;
				}
				if (areAllOnTheLeft(pointOnHull, p))
				{
					hull.add(pointOnHull);
					pointOnHull = p;
					if (pointOnHull == hull.get(0))
					{
						break MAIN;
					}
				}
			}
		}
		return hull;
	}
	
	private boolean areAllOnTheLeft(Point2D.Double p1, Point2D.Double p2)
	{
		Iterator<Point2D.Double> it = _polygon.iterator();
		Point2D.Double p;
		while (it.hasNext())
		{
			p = it.next();
			if (!isCCW(p1, p2, p))
				return false;
		}
		return true;
	}
	
	static private boolean isCCW(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2)
	{
		return 0. <=  p0.x*p1.y + p1.x*p2.y + p2.x*p0.y
					- p0.x*p2.y - p1.x*p0.y - p2.x*p1.y;
	}
	
	public String toString()
	{
		return "Center: "+x+" "+y+" Angle: "+theta+" H/W: "+height+"/"+width;
	}
}

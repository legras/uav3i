package com.deev.interaction.common.ui;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@SuppressWarnings("serial")
public class Gesture extends ArrayList<TouchPoint>
{	
	public static double GAP = 100.;
	protected boolean _large = false;
	
	public Gesture()
	{
		super();
	}
	
	public Gesture(List<TouchPoint> list)
	{
		super();
		
		TouchPoint tp;
		for (Iterator<TouchPoint> it = list.iterator(); it.hasNext();)
		{
			tp = it.next();
			addPoint(tp.x, tp.y, tp.date);
		}
	}

	public void addPoint(double x, double y)
	{
		addPoint(x, y, System.currentTimeMillis());
	}
	
	public void addPoint(double x, double y, long time)
	{
		add(new TouchPoint(x, y, time));
		if (size() > 1 && get(0).distance(x, y) > GAP)
			_large = true;
	}
	
	public long getDuration()
	{
		int size = this.size();
		
		if (size < 2)
			return 0;
		else
			return this.get(size-1).date - this.get(0).date; 
	}
	
	public boolean isOpen()
	{
		return !_large || isOpen(GAP);
	}
	
	public boolean isOpen(double opening)
	{
		int size = this.size();
		double d2 = TouchPoint.dist2(this.get(size-1), this.get(0));
		
		return d2 > opening*opening;
	}
	
	public Polygon getPolygon()
	{
		Polygon p = new Polygon();
		
		Iterator<TouchPoint> it = this.iterator();
		TouchPoint tp;
		while (it.hasNext())
		{
			tp = it.next();
			p.addPoint((int) tp.x, (int) tp.y);
		}
		
		return p;
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
		
		Iterator<TouchPoint> it = this.iterator();
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
			it = this.iterator();
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
		Iterator<TouchPoint> it = this.iterator();
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
	
	
	static private boolean doSegmentsABandCDIntersect(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D)
	{
		return doesLineABIntersectSegmentCD(A, B, C, D) && doesLineABIntersectSegmentCD(C, D, A, B);
	}
	
	static private boolean doesLineABIntersectSegmentCD(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D)
	{
		double tempAB = A.x*A.y + A.y*B.x - A.x*B.y - A.y*A.x;
		double tempC  = C.x*B.y + C.y*A.x - A.y*C.x - C.y*B.x;
		double tempD  = D.x*B.y + D.y*A.x - A.y*D.x - D.y*B.x;
		
		return (tempAB+tempC) * (tempAB+tempD) < 0.;
	}
	
	public Polygon getSimplifiedPolygon(double tolerance)
	{
		return simplifyPolygon(this.getPolygon(), tolerance);
	}
	
	static public Polygon simplifyPolygon(Polygon poly, double tolerance)
	{
		double totalArea = tolerance * area(poly);
		
		Polygon polygon = poly;
		int n = 0;
		int nbefore = 0;
		
		while (nbefore != polygon.npoints)
		{
			double A = totalArea;
			double a;

			for (int i = 0; i < polygon.npoints; i++)
			{
				a = splicedAreaAt(polygon, i);
				if (a < 0.)
					continue;
				if (a < A)
				{
					A = a;
					n = i;
				}
			}

			nbefore = polygon.npoints;
			if (A < totalArea)
				polygon = spliceSegmentAt(polygon, n);
		}
		
		return polygon;
	}
	
	static private double splicedAreaAt(Polygon poly, int i)
	{
		if (i<0 || i>=poly.npoints)
			return -1.;

		Point2D.Double O = new Point2D.Double();
		Point2D.Double A = getPointInPolygon(poly, i-1);
		Point2D.Double B = getPointInPolygon(poly, i);
		Point2D.Double C = getPointInPolygon(poly, i+1);
		Point2D.Double D = getPointInPolygon(poly, i+2);

		double a1 = area(A, B, C);
		double a2 = area(B, C, D);

		if (a1*a2 <= 0) // 'S' pattern
		{
			double a = 0.;
			a += area(O, A, B);
			a += area(O, B, C);
			a += area(O, C, D);
			a += area(O, D, A);
			
			a1 = Math.abs(a1);
			a2 = Math.abs(a2);
			
			if (a1 < a || a2 < a)
				return a1 < a2 ? a1 : a2;

			return a;
		}
		else // 'C' pattern
		{
			Point2D.Double p = intersectionOfTwoSegments(A, B, C, D);

			if (p == null)
				return -1.;
			
			double a = Math.abs(area(p, B, C));

			return a;
		}
	}
	
	/**
	 * Removes segment [i, i+1] from the polygon and returns an updated polygon. 
	 * the segment is spliced and either replaced  
	 * by a new vertex ('C' pattern) or by nothing ('S' pattern) or by [i] or [i+1] ('V' sub-pattern of 'S').
	 * @param poly
	 * @param i
	 * @param area
	 * @return the updated polygon
	 */
	static private Polygon spliceSegmentAt(Polygon poly, int i)
	{
		if (i<0 || i>=poly.npoints)
			return poly;
		
		Point2D.Double O = new Point2D.Double();
		Point2D.Double A = getPointInPolygon(poly, i-1);
		Point2D.Double B = getPointInPolygon(poly, i);
		Point2D.Double C = getPointInPolygon(poly, i+1);
		Point2D.Double D = getPointInPolygon(poly, i+2);
		
		double a1 = area(A, B, C);
		double a2 = area(B, C, D);
		
		if (a1*a2 <= 0) // 'S' pattern
		{
			Polygon polygon = new Polygon();
		
			double a = 0.;
			a += area(O, A, B);
			a += area(O, B, C);
			a += area(O, C, D);
			a += area(O, D, A);
			
			a1 = Math.abs(a1);
			a2 = Math.abs(a2);
			
			if (a < a1 && a < a2) // Complete 'S' pattern
			{	
				for (int k=0; k<poly.npoints; k++)
					if (k != i && k != (i+poly.npoints+1 % poly.npoints))
						polygon.addPoint(poly.xpoints[k], poly.ypoints[k]);

				return polygon;
			}
			else // 'V' sub-pattern
			{
				int m = i;
				if (a2 < a1)
					m = i+1;
				
				for (int k=0; k<poly.npoints; k++)
					if (k != m)
						polygon.addPoint(poly.xpoints[k], poly.ypoints[k]);

				return polygon;
			}
		}
		else // 'C' pattern
		{
			Point2D.Double p = intersectionOfTwoSegments(A, B, C, D);
			
			Polygon polygon = new Polygon();
			for (int k=0; k<poly.npoints; k++)
			{
				if (k == i)
				{
					polygon.addPoint((int) p.x, (int) p.y);
					continue;
				}
				
				if (k == ((i+poly.npoints+1) % poly.npoints))
					continue;
				
				polygon.addPoint(poly.xpoints[k], poly.ypoints[k]);
			}

			return polygon;
		}
	}
	
	// http://www.topcoder.com/tc?module=Static&d1=tutorials&d2=geometry2
	static public Point2D.Double intersectionOfTwoSegments(Point2D.Double A, Point2D.Double B, Point2D.Double C, Point2D.Double D)
	{
		double A1, B1, C1;
		A1 = B.y-A.y;
		B1 = A.x-B.x;
		C1 = A1*A.x + B1*A.y;
		
		double A2, B2, C2;
		A2 = D.y-C.y;
		B2 = C.x-D.x;
		C2 = A2*C.x + B2*C.y;
		
		double det = A1*B2 - A2*B1;
		
		if (det == 0.)
			return null;
		
		return new Point2D.Double((B2*C1 - B1*C2)/det, (A1*C2 - A2*C1)/det);
	}
	
	static public double area(Polygon poly)
	{
		double a = 0.;
		Point2D.Double O = new Point2D.Double();
		Point2D.Double A, B;
		int n = poly.npoints;
		
		B = new Point2D.Double(poly.xpoints[n-1], poly.ypoints[n-1]);
		for (int i=0; i<poly.npoints; i++)
		{
			A = B;
			B = new Point2D.Double(poly.xpoints[i], poly.ypoints[i]);
			a += area(O, A, B);
		}
		
		return Math.abs(a);
	}
	
	/**
	 * @param A
	 * @param B
	 * @param C
	 * @return (oriented) area of the triangle ABC i.e. AB x AC
	 */
	static public double area(Point2D.Double A, Point2D.Double B, Point2D.Double C)
	{
		return (B.x-A.x)*(C.y-A.y) - (C.x-A.x)*(B.y-A.y);
	}
	
	static private Point2D.Double getPointInPolygon(Polygon poly, int i)
	{
		int n = (i+poly.npoints) % poly.npoints;
		return new Point2D.Double(poly.xpoints[n], poly.ypoints[n]);
	}
}

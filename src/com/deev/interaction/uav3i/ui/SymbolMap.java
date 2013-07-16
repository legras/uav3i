package com.deev.interaction.uav3i.ui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

@SuppressWarnings("serial")
public class SymbolMap extends Map
{		
	private Manoeuver _manoeuver = null;
	
	private boolean _trueGrid = true;
	private double _gridstep = 1.;
		
	public SymbolMap(String domain)
	{
		super();
		
		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);	
	}

	public void setManoeuver(Manoeuver m)
	{
		_manoeuver = m;
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		paint(g2);
	}
	
	public synchronized void paint(Graphics2D g2)
	{	
		RenderingHints rh;
		rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setRenderingHints(rh);

//		// --------- GRID --------------------------------------------------
//		while (metersToPixels(_gridstep ) < 200.)
//			_gridstep *= 10.;
//
//		while (metersToPixels(_gridstep) > 400.)
//			_gridstep /= 10.;
//
//		double xmin, ymax; // meters
//
//		Point2D.Double origin = pixelsToMeters(new Point2D.Double(0., 0.));
//		xmin = _gridstep * (Math.floor(origin.x/_gridstep) - 1);
//		ymax = _gridstep * (Math.floor(origin.y/_gridstep) + 1);
//
////		if (_trueGrid)
////		{
////			g2.setPaint(Color.WHITE);
////			g2.setStroke(new BasicStroke(.5f));
////
////			Point2D.Double p = metersToPixels(new Point2D.Double(xmin, 0.));
////
////			for (double x=xmin; p.x < getWidth(); x+=_gridstep)
////			{
////				p = metersToPixels(new Point2D.Double(x, 0.));
////				g2.draw(new Line2D.Double(p.x, 0., p.x, getHeight()));
////
////				AffineTransform old = g2.getTransform();
////
////				g2.translate(p.x, getHeight());
////				g2.rotate(-Math.PI/2.);
////				g2.drawString(String.format(Locale.US, "%.0f", x), 2, -2);
////
////				g2.setTransform(old);
////			}
////
////			p = metersToPixels(new Point2D.Double(xmin, ymax));
////
////			for (double y=ymax; p.y < getHeight(); y-=_gridstep)
////			{		
////				p = metersToPixels(new Point2D.Double(xmin, y));
////				g2.draw(new Line2D.Double(0., p.y, getWidth(), p.y));
////
////				g2.drawString(String.format(Locale.US, "%.0f", y) + " m", 2, (int) p.y-2);
////			}
////		}
////		else
////		{
////			g2.setPaint(new Color(1.f, 1.f, 1.f, .3f));
////			g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
////			
////			Point2D.Double p;
////			double L = 6.;
////			
////			p  =  metersToPixels(new Point2D.Double(xmin, ymax));
////			for (double x=xmin; p.x < getWidth(); x+=_gridstep)
////			{
////				p = metersToPixels(new Point2D.Double(x, ymax));
////				for (double y=ymax; p.y < getHeight(); y-=_gridstep)
////				{
////					p = metersToPixels(new Point2D.Double(x, y));
////					g2.draw(new Line2D.Double(p.x-L, p.y, p.x+L, p.y));
////					g2.draw(new Line2D.Double(p.x, p.y-L, p.x, p.y+L));
////				}
////			}
////		}
//
//		g2.setPaint(new Color(1.f, 1.f, 1.f, .7f));
//		g2.setStroke(new BasicStroke(.5f));
//
//		Point2D.Double p = metersToPixels(new Point2D.Double(xmin, 0.));
//
//		for (double x=xmin; p.x < getWidth(); x+=_gridstep)
//		{
//			p = metersToPixels(new Point2D.Double(x, 0.));
//
//			AffineTransform old = g2.getTransform();
//
//			g2.translate(p.x, getHeight());
//			g2.rotate(-Math.PI/2.);
//			g2.drawString(String.format(Locale.US, "%.0f", x), 20, -2);
//
//			g2.setTransform(old);
//		}
//
//		p = metersToPixels(new Point2D.Double(xmin, ymax));
//
//		for (double y=ymax; p.y < getHeight(); y-=_gridstep)
//		{		
//			p = metersToPixels(new Point2D.Double(xmin, y));
//			g2.drawString(String.format(Locale.US, "%.0f", y), 4, (int) p.y-2);
//		}
//
//		g2.setPaint(new Color(1.f, 1.f, 1.f, .3f));
//		g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
//
//		double L = 6.;
//
//		p  =  metersToPixels(new Point2D.Double(xmin, ymax));
//		for (double x=xmin; p.x < getWidth(); x+=_gridstep)
//		{
//			p = metersToPixels(new Point2D.Double(x, ymax));
//			for (double y=ymax; p.y < getHeight(); y-=_gridstep)
//			{
//				p = metersToPixels(new Point2D.Double(x, y));
//				g2.draw(new Line2D.Double(p.x-L, p.y, p.x+L, p.y));
//				g2.draw(new Line2D.Double(p.x, p.y-L, p.x, p.y+L));
//			}
//		}


		
		// --------- COMPASS ------------------------------------------------

		final double r = 60.;
		final Point2D.Double cr = new Point2D.Double(getWidth()-r-100., r+200.);

		//Shape circle = new Ellipse2D.Double(cr.x-r, cr.y-r, 2.*r, 2.*r);
		GeneralPath left = new GeneralPath();
		left.moveTo(cr.x, cr.y+r/2.);
		left.lineTo(cr.x-r*Math.sin(Math.PI/4.), cr.y+r*Math.cos(Math.PI/4.));
		left.lineTo(cr.x, cr.y-r);

		GeneralPath right = new GeneralPath();
		right.moveTo(cr.x, cr.y+r/2.);
		right.lineTo(cr.x, cr.y-r);
		right.lineTo(cr.x+r*Math.sin(Math.PI/4.), cr.y+r*Math.cos(Math.PI/4.));
		right.closePath();

		Stroke stroke = new BasicStroke(4.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setPaint(new Color(0.f, 0.f, 0.f, .1f));
		g2.setStroke(stroke);
		//g2.draw(circle);
		g2.draw(left);
		g2.draw(right);
		g2.setPaint(new Color(1.f, 1.f, 1.f, .1f));
		g2.fill(right);
		g2.setPaint(new Color(1.f, 1.f, 1.f, .3f));
		stroke = new BasicStroke(2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g2.setStroke(stroke);
		//g2.draw(circle);
		g2.draw(left);
		g2.draw(right);
		
		// --------- Manoeuvers --------------------------------------------------
		synchronized(this)
		{
			if (_manoeuver != null)
					_manoeuver.paint(g2);
		}
		
	}
	
	public boolean adjustAtPx(double x, double y)
	{
		if (_manoeuver == null)
			return false;
		
		return _manoeuver.adjustAtPx(x, y);
	}
	
	public boolean isAdjusting()
	{
		if (_manoeuver == null)
			return false;
		
		return _manoeuver.isAdjusting();
	}
	
	public void stopAdjusting()
	{
		if (_manoeuver == null)
			return;
		
		_manoeuver.stopAdjusting();
	}
	
	
	@Override
	public void alignWith(Map map)
	{
		super.alignWith(map);
//		if (_zoneSTracker != null)
//			_zoneSTracker.align();
	}
	
}

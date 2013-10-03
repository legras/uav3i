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

import uk.me.jstott.jcoord.LatLng;

@SuppressWarnings("serial")
public class SymbolMap extends Map
{		
	private Manoeuver _manoeuver = null;
	
	private Trajectory _trajectory;
	private long _lastTrajectoryUpdate = 0;
		
	public SymbolMap(String domain)
	{
		super();
		
		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);	
		
		_trajectory = new Trajectory();
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
		long currentTime = System.currentTimeMillis();
		
		RenderingHints rh;
		rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g2.setRenderingHints(rh);
		
		// Update de trajectoire
		if (currentTime - _lastTrajectoryUpdate > 1000)
			_trajectory.update();
		
		// Tracé de trajectoire
		_trajectory.paint(this, g2);		
		
		// --------- Manoeuvers --------------------------------------------------
		synchronized(this)
		{
			if (_manoeuver != null)
					_manoeuver.paint(g2);
		}
		
	}
	
	public Point getScreenForLatLng(LatLng latlng)
	{		
		return MainFrame.OSMMap.getMapViewer().getMapPosition(latlng.getLat(), latlng.getLng(), false);
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

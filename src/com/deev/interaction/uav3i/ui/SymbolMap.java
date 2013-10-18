package com.deev.interaction.uav3i.ui;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import com.deev.interaction.common.ui.Touchable;
import com.deev.interaction.uav3i.model.UAVDataPoint;
import com.deev.interaction.uav3i.model.UAVDataStore;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.UAV3iSettings.Mode;
import uk.me.jstott.jcoord.LatLng;

@SuppressWarnings("serial")
public class SymbolMap extends Map implements Touchable
{		
	private Manoeuver _manoeuver = null;
	
	private Trajectory _trajectory;
	private long _lastTrajectoryUpdate = 0;
	

	// Dessin de UAV
	protected Path2D.Double _tri;
	protected static double D = 10.;
		
	public SymbolMap()
	{
		super();
		
		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);	
		
		_trajectory = new Trajectory();

		// Dessin de UAV
		_tri = new Path2D.Double();
		_tri.moveTo(D/2., 0.);
		_tri.lineTo(-D/2., D/2.);
		_tri.lineTo(-D/3., 0.);
		_tri.lineTo(-D/2., -D/2.);
		_tri.closePath();
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
		if (currentTime - _lastTrajectoryUpdate > 500)
		{
			_trajectory.update();
			_lastTrajectoryUpdate = currentTime;
		}
		
		// Tracé de trajectoire
		GeneralPath fullTrajectory = _trajectory.getFullPath(this);	
		
		if (fullTrajectory != null)
		{
			g2.setPaint(new Color(1.f, 1.f, 1.f, .5f));
			// g2.setPaint(new Color(0.f, 0.f, 0.f, .3f));
			g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_ROUND));
			g2.draw(fullTrajectory);
			g2.setPaint(Color.RED);
			g2.setStroke(new BasicStroke(2.f, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_ROUND));
			g2.draw(fullTrajectory);
		}

		// Dessin UAV
		AffineTransform old = g2.getTransform();	
		
		UAVDataPoint uavpoint = UAVDataStore.getDataPointAtTime(System.currentTimeMillis());
		if (uavpoint != null)
		{
			Point uav = getScreenForLatLng(uavpoint.latlng);
			double course = Math.PI/2. - uavpoint.course/180.*Math.PI;
			g2.translate(uav.x, uav.y);
			g2.rotate(-course);
			g2.setPaint(new Color(0.f, 0.f, 0.f, .35f));
			g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g2.draw(_tri);
			g2.setPaint(new Color(.3f, .6f, 1.f, 1.f));
			g2.fill(_tri);
			g2.setPaint(Color.WHITE);
			g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
			g2.draw(_tri);
		}
		g2.setTransform(old);
		
		// --------- Manoeuvers --------------------------------------------------
		synchronized(this)
		{
			if (_manoeuver != null)
					_manoeuver.paint(g2);
		}
		
	}
	
	@Override
	public double getPPM()
	{
		return 1. / MainFrame.OSMMap.getMapViewer().getMeterPerPixel();
	}
	
	public Point getScreenForLatLng(LatLng latlng)
	{		
		return MainFrame.OSMMap.getMapViewer().getMapPosition(latlng.getLat(), latlng.getLng(), false);
	}
	
	public LatLng getLatLngForScreen(double x, double y)
	{		
		Coordinate coord = MainFrame.OSMMap.getMapViewer().getPosition((int) x, (int) y);
		
		return new LatLng(coord.getLat(), coord.getLon());
	}
	
	/*
	public double getPixelPerDegree()
	{
		LatLng a_ll = getLatLngForScreen(0, 0);
		LatLng b_ll = getLatLngForScreen(1000, 0);
		
		return 1000./(b_ll.getLng() - a_ll.getLng());
	}
	*/
	
	public boolean adjustAtPx(double x, double y)
	{
		if (_manoeuver == null)
			return false;
		
		// On demande au manoeuver de s'ajuster et on récupère la valeur booléenne
		// du résultat pour la renvoyer ensuite même si elle n'est pas utilisée...
		boolean result = _manoeuver.adjustAtPx(x, y);
		
    // Si on est connecté à Paparazzi...
		if(UAV3iSettings.getMode() == Mode.IVY)
		{
		  switch (_manoeuver.getClass().getSimpleName())
      {
        case "CircleMnvr":
          // Signalement à Paparazzi de la modification du rayon.
          // TODO utilité de la transmission à chaque modification ? Attendre une à 2 secondes que le rayon soit stabilisé ?
          UAVDataStore.getIvyCommunication().setNavRadius(((CircleMnvr)_manoeuver).getCurrentRadius());
          break;
        default:
          break;
      }
		}
    
		
		return result;
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

	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_manoeuver != null && _manoeuver.isInterestedAtPx(x, y))
			return 20.f;
		else
			return -1.f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		adjustAtPx(x, y);
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		adjustAtPx(x, y);
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		stopAdjusting();
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		// TODO Auto-generated method stub
		
	}
	
}

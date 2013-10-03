package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import com.deev.interaction.uav3i.model.UAVDataPoint;
import com.deev.interaction.uav3i.model.UAVDataStore;

import uk.me.jstott.jcoord.LatLng;

/**
 * @author legras
 * Cette classe est destinée à stocker les points de la trajectoire du drone du côté de la vue. 
 * Avec donc une mise à jour régulière en interrogeant UAVDataStore.
 */
public class Trajectory
{
	private class TrajectoryPoint
	{
		public LatLng latlng;
		public long time;
		
		public TrajectoryPoint(LatLng ll, long t)
		{
			latlng = ll;
			time = t;
		}
	}
	
	private ArrayList<TrajectoryPoint> _points;
	
	public Trajectory()
	{
		_points = new ArrayList<Trajectory.TrajectoryPoint>();
	}
	
	public void update()
	{
		long time = System.currentTimeMillis();
		LatLng ll = UAVDataStore.getLatLngAtTime(time);
		
		_points.add(new TrajectoryPoint(ll, time));
	}

	public void paint(SymbolMap symbolMap, Graphics2D g2)
	{
		GeneralPath line;
		line = new GeneralPath();
		boolean start = true;
		Point p;
		
		if (_points.size() < 2)
			return;
		
		for (TrajectoryPoint tp : _points)
		{
			p = symbolMap.getScreenForLatLng(tp.latlng);
			
			if (start)
			{
				line.moveTo(p.x, p.y);
				start = false;
			}
			else
				line.lineTo(p.x, p.y);
		}
		
		g2.setPaint(Color.RED);
		g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g2.draw(line);
	}
}

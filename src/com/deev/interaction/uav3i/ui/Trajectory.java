package com.deev.interaction.uav3i.ui;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVModel;

import eu.telecom_bretagne.uav3i.UAV3iSettings;

/**
 * @author legras
 * Cette classe est destinée à stocker les points de la trajectoire du drone du côté de la vue. 
 * Avec donc une mise à jour régulière en interrogeant UAVDataStore.
 * 
 * Elle est capable de s'afficher sur une SymbolMap (à modifier pour être plus générique, ajouter 
 * une Interface screen/latlng). On ajoutera différents services de dessin, notamment avec la 
 * sortie de GeneralPath entre deux instants.
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
		if(UAVModel.isEmpty())
			return;

		long time = System.currentTimeMillis();
		LatLng ll = UAVModel.getLatLngAtTime(time);

		_points.add(new TrajectoryPoint(ll, time));

		// Centrage de la carte lors de l'insertion du premier point de la trajectoire.
		if(_points.size() == 1)
		{
			MainFrame.OSMMap.getMapViewer().setDisplayPositionByLatLon(ll.getLat(),
					ll.getLng(),
					UAV3iSettings.getTrajectoryZoom());
		}
	}

	public GeneralPath getFullPath(SymbolMap symbolMap)
	{
		GeneralPath line;
		line = new GeneralPath();
		boolean start = true;
		Point2D.Double p;

		if (_points.size() < 2)
			return  null;

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

		return line;
	}

	public GeneralPath getTrajectorySequence(SymbolMap symbolMap, long start, long end)
	{
		GeneralPath line;
		line = new GeneralPath();
		boolean started = true;
		Point2D.Double p;

		if (_points.size() < 2)
			return  null;

		trajectory:
		for (TrajectoryPoint tp : _points)
		{
			if (tp.time < start)
				continue trajectory;
			
			if (tp.time > end)
				break trajectory;
			
			p = symbolMap.getScreenForLatLng(tp.latlng);

			if (started)
			{
				line.moveTo(p.x, p.y);
				started = false;
			}
			else
				line.lineTo(p.x, p.y);
		}

		return line;
	}
}

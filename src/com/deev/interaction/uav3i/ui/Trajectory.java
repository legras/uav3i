package com.deev.interaction.uav3i.ui;

import javax.swing.JComponent;

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
	
	
}

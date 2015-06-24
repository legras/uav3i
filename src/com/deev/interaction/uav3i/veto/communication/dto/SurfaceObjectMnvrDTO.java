package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import com.deev.interaction.uav3i.veto.ui.Veto;

import uk.me.jstott.jcoord.LatLng;

public class SurfaceObjectMnvrDTO extends ManoeuverDTO
{
	private LatLng _center;
	private Point2D.Double _oldCenterPx = new Point2D.Double(-1,-1);
	private double _angle = 0.1;
	private Point2D.Double _lookAt;
	
	public SurfaceObjectMnvrDTO(int id, LatLng center, double angle, double lookX, double lookY)
	{
		this.id = id;
		_center = center;
		_angle = angle;
		_lookAt = new Point2D.Double(lookX, lookY);
	}
	
	
	@Override
	public void paint(Graphics2D g2)
	{
		
	}

	@Override
	public LatLng getCenter()
	{
		return _center;
	}

	@Override
	public void positionButtons()
	{
		Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
	    
	    if (buttons != null)
	    {
	    	// Est-ce que ça vaut le coup de recalculer la position des boutons ?
	    	if(!centerPx.equals(_oldCenterPx))
	    	{
	    		_oldCenterPx = centerPx;
	    		buttons.setPositions(centerPx, 292, Math.PI/2, true);
	    	}
	    }
	}

}

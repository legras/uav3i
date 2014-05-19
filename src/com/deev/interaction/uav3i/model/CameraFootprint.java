package com.deev.interaction.uav3i.model;

import java.util.ArrayList;

import uk.me.jstott.jcoord.LatLng;

public class CameraFootprint extends ArrayList<LatLng>
{

	public long time;
	
	/**
	 * Dummy footprint generated from the position of the UAV and some basic stuff.
	 * @param uavpos
	 * @param course in degrees
	 */
	CameraFootprint(LatLng uavpos, double course, long t)
	{
		super();
		
		LatLng right, left;
		double d = .002;
		double a = 15.;
		
		right = new LatLng(d * Math.cos((course+a)/180*Math.PI),  d * Math.sin((course+a)/180*Math.PI));
		left = new LatLng(d * Math.cos((course-a)/180*Math.PI),  d * Math.sin((course-a)/180*Math.PI));
		
		this.add(new LatLng(uavpos.getLat()+right.getLat(),   uavpos.getLng()+right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*right.getLat(), uavpos.getLng()+2*right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*left.getLat(),  uavpos.getLng()+2*left.getLng()));
		this.add(new LatLng(uavpos.getLat()+left.getLat(),    uavpos.getLng()+left.getLng()));
		
		this.time = t;
	}
}

package com.deev.interaction.uav3i.model;

import java.util.ArrayList;

import uk.me.jstott.jcoord.LatLng;

public class CameraFootprint extends ArrayList<LatLng>
{

	public long time;
	public LatLng center;
	
	/**
	 * Dummy footprint generated from the position of the UAV and some basic stuff.
	 * @param uavpos
	 * @param course in degrees
	 */
	CameraFootprint(LatLng uavpos, double course, long t)
	{
		super();
		
		double c = course + 45. * multisigmo(t);
		
		LatLng right, left;
		double d = .002;
		double a = 15.;
		
		right = new LatLng(d * Math.cos((c+a)/180*Math.PI),  d * Math.sin((c+a)/180*Math.PI));
		left = new LatLng(d * Math.cos((c-a)/180*Math.PI),  d * Math.sin((c-a)/180*Math.PI));
		
		this.add(new LatLng(uavpos.getLat()+right.getLat(),   uavpos.getLng()+right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*right.getLat(), uavpos.getLng()+2*right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*left.getLat(),  uavpos.getLng()+2*left.getLng()));
		this.add(new LatLng(uavpos.getLat()+left.getLat(),    uavpos.getLng()+left.getLng()));
		
		this.time = t;
	}
	
	static double multisigmo(long time)
	{
		final double stableT = 4000;
		final double moveT = 1000;
				
		double t = time % (3*(stableT+moveT));
		
		if (t < stableT)
			return 0.;
		
		t -= stableT;
		
		if (t < moveT)
			return .5 - .5 * Math.cos(t / moveT * Math.PI);
		
		t -= moveT;
		
		if (t < stableT)
			return 1.;
		
		t -= stableT;
		
		if (t < moveT)
			return Math.cos(t / moveT * Math.PI);
		
		t -= moveT;
		
		if (t < stableT)
			return -1.;
		
		t -= stableT;
		
		return -.5 - .5 * Math.cos(t / moveT * Math.PI);
		
	}
}




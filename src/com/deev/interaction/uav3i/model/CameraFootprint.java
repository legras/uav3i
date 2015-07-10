package com.deev.interaction.uav3i.model;

import java.awt.geom.Point2D;
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
		
		double c = course;
		
		LatLng right, left;
		double d = .0005;
		double a = 15.;
		
		right = new LatLng(d * Math.cos((c+a)/180*Math.PI),  d * Math.sin((c+a)/180*Math.PI));
		left = new LatLng(d * Math.cos((c-a)/180*Math.PI),  d * Math.sin((c-a)/180*Math.PI));
		
		this.add(new LatLng(uavpos.getLat()+right.getLat(),   uavpos.getLng()+right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*right.getLat(), uavpos.getLng()+2*right.getLng()));
		this.add(new LatLng(uavpos.getLat()+2*left.getLat(),  uavpos.getLng()+2*left.getLng()));
		this.add(new LatLng(uavpos.getLat()+left.getLat(),    uavpos.getLng()+left.getLng()));
		
		this.time = t;
	}
	
	/**
	 * Slightly less dummy footprint, centered on the target.
	 * @param uavposition
	 * @param camTarget
	 * @param t
	 */
	public CameraFootprint(LatLng uavposition, LatLng camTarget, long t)
	{
		super();
	
		LatLng pN = new LatLng(camTarget.getLat(), uavposition.getLng());
		LatLng pE = new LatLng(uavposition.getLat(), camTarget.getLng());
		
		double mpdNS = 1000. * uavposition.distance(pN) / Math.abs(pN.getLat()-uavposition.getLat());
		double mpdEW = 1000. * uavposition.distance(pE) / Math.abs(pE.getLng()-uavposition.getLng());
		
		// Planar approximation
		Point2D.Double camTm = new Point2D.Double(	mpdEW * (pE.getLng()-uavposition.getLng()), 
													mpdNS * (pN.getLat()-uavposition.getLat()));
		
		Point2D.Double Um = new Point2D.Double( .15 * camTm.x, .15 * camTm.y);
		Point2D.Double Vm = new Point2D.Double(-.15 * camTm.y, .15 * camTm.x);
		
		ArrayList<Point2D.Double> pm = new ArrayList<>();
		pm.add(new Point2D.Double(camTm.x - Um.x - Vm.x, camTm.y - Um.y - Vm.y));
		pm.add(new Point2D.Double(camTm.x + Um.x - Vm.x, camTm.y + Um.y - Vm.y));
		pm.add(new Point2D.Double(camTm.x + Um.x + Vm.x, camTm.y + Um.y + Vm.y));
		pm.add(new Point2D.Double(camTm.x - Um.x + Vm.x, camTm.y - Um.y + Vm.y));
		
		for (Point2D.Double point : pm)
		{
			this.add(new LatLng(uavposition.getLat() + point.y / mpdNS, 
								uavposition.getLng() + point.x / mpdEW));
		}
		
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




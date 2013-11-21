package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import com.deev.interaction.uav3i.model.UAVModel;

import sun.security.action.GetLongAction;
import uk.me.jstott.jcoord.LatLng;

public class CircleMnvr extends Manoeuver
{
	private LatLng _center;
	private double _currentRm = 500.;
	private double _lastRm;
	private boolean _isMoving = false;
	private Point2D.Double _offCenter;

	static double RPX = 10.;

	public CircleMnvr(SymbolMap map, LatLng c)
	{
		_center = c;
		_smap = map;
		
		// ********** ManoeuverButtons **********
		try
		{
			_buttons = new ManoeuverButtons(this);
			_buttons.addTo(MainFrame.clayer);
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			_buttons = null;
		}
		
		positionButtons();
	}


	@Override
	public void positionButtons()
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		double Rpx = _smap.getPPM() * _currentRm;
		
		if (_buttons != null)
			_buttons.setPositions(centerPx, 40+Rpx, Math.PI/2, true);
	}
	
	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();

		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		g2.translate(centerPx.x, centerPx.y);
		Ellipse2D.Double ell = new Ellipse2D.Double(-RPX, -RPX, 2*RPX, 2*RPX);

		paintFootprint(g2, ell, isSubmitted());

		double Rpx = _smap.getPPM() * _currentRm;

		ell = new Ellipse2D.Double(-Rpx, -Rpx, 2*Rpx, 2*Rpx);

		paintAdjustLine(g2, ell, isSubmitted(), _adjusting);

		g2.setTransform(old);
	}

	@Override
	public boolean adjustAtPx(double x, double y)
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		double Rm = centerPx.distance(new Point2D.Double(x, y))/_smap.getPPM();
		
		_buttons.show();

		if (_adjusting)
		{
			_currentRm += Rm - _lastRm;
			_lastRm = Rm;

			if (_currentRm < 2.*RPX/_smap.getPPM())
				_currentRm = 2.*RPX/_smap.getPPM();

			return true;
		}

		if (isAdjustmentInterestedAtPx(x, y))
		{
			_lastRm = Rm;
			_adjusting = true;
		}

		return _adjusting;
	}

	public boolean isAdjustmentInterestedAtPx(double x, double y)
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		double Rm = centerPx.distance(new Point2D.Double(x, y))/_smap.getPPM();

		return Math.abs(_currentRm-Rm) < GRIP/_smap.getPPM();
	}

	public double getCurrentRadius()
	{
		return _currentRm;
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		if (!_isMoving && centerPx.distance(x, y) < 2*RPX)
			return getMoveInterest();
		else
			return -1.f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		if (_isMoving)
			return;

		super.addTouch(x, y, touchref);
		
		if (!isModifiable())
		{
			cancelTouch(touchref);
			return;
		}
		
		if (!isModifiable())
		{
			cancelTouch(touchref);
			return;
		}
		
		_isMoving = true;
		
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		_offCenter = new Point2D.Double(x-centerPx.x, y-centerPx.y);
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		super.updateTouch(x, y, touchref);
		
		if (_offCenter == null)
		{
			cancelTouch(touchref);
			return;
		}
		
		Point2D.Double centerPx = new Point2D.Double(x-_offCenter.x, y-_offCenter.y);
		
		_center = _smap.getLatLngForScreen(centerPx.x, centerPx.y);
		
		positionButtons();
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		_isMoving = false;
		_offCenter = null;
		
		positionButtons();
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_isMoving = false;
		_offCenter = null;
		
		positionButtons();
	}
}

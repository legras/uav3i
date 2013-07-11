package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class LineMnvr extends Manoeuver
{
	private Point2D.Double _Am, _Bm;
	
	private SymbolMap _smap;
	private double _currentRm = 80.;
	private double _lastRm;
	private Point2D.Double _um, _vm;
	
	static double RPX = 10.;

	public LineMnvr(SymbolMap map, double xA, double yA, double xB, double yB)
	{
		_Am = new Point2D.Double(xA, yA);
		_Bm = new Point2D.Double(xB, yB);
		_smap = map;
		
		double d = _Am.distance(_Bm);
		_um = new Point2D.Double((xB-xA)/d, (yB-yA)/d);
		_vm = new Point2D.Double(-_um.y, _um.x);
	}

	
	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();
		
		Point2D.Double Apx = _smap.metersToPixels(_Am);
		Point2D.Double Bpx = _smap.metersToPixels(_Bm);
		
		Area area = new Area();
		BasicStroke stroke = new BasicStroke((float) RPX*2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		GeneralPath line;
		line = new GeneralPath();
		line.moveTo(Apx.x, Apx.y);
		line.lineTo(Bpx.x, Bpx.y);
		
		area.add(new Area(stroke.createStrokedShape(line)));
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(1.0f, 0.f, 0.f, 1.0f));
		g2.draw(area);
		g2.setPaint(new Color(1.0f, 0.1f, 0.1f, 0.2f));
		g2.fill(area);
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(0.0f, 0.f, 1.0f, 1.0f));
		
		double Rpx = _smap.getPPM() * _currentRm;
		
		Point2D.Double LApx = new Point2D.Double(Apx.x + _vm.x * Rpx,
												 Apx.y - _vm.y * Rpx);
		
		Point2D.Double LBpx = new Point2D.Double(Bpx.x + _vm.x * Rpx,
												 Bpx.y - _vm.y * Rpx);
		
		Line2D.Double l = new Line2D.Double(LApx, LBpx);
		
		g2.setStroke(new BasicStroke(2.f*(float)GRIP));
		g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.5f));
		g2.draw(l);
		
		g2.setStroke(new BasicStroke(4.f));
		g2.setPaint(new Color(0.0f, 0.f, 1.0f, 1.0f));
		g2.draw(l);
		
		g2.setTransform(old);
	}
	
	@Override
	public boolean adjustAtPx(double x, double y)
	{
		// On projette pour se repérer par rapport au segment
		Point2D.Double pm = _smap.pixelsToMeters(x, y);
		pm.x -= _Am.x;
		pm.y -= _Am.y;
		
		double Um = pm.x*_um.x + pm.y*_um.y;
		double Vm = pm.x*_vm.x + pm.y*_vm.y;
		
		if (_adjusting)
		{
			_currentRm += Vm - _lastRm;
			_lastRm = Vm;
			
			return true;
		}
		
		if (Vm > _currentRm-GRIP && Vm < _currentRm+GRIP)// && Um > 0 && Um < _Am.distance(_Bm))
		{
			_lastRm = Vm;
			_adjusting = true;
		}
		
		return _adjusting;
	}

}

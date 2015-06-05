package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.model.UAVModel;

public class Ruler implements Touchable
{
	Point2D.Double _A, _B;

	private static double _HALFW = 20.;
	private static double _MIN_SEG_WIDTH = 100;
	
	private enum RulerMoveStates {NONE, TRANSLATE, MOVE_A, MOVE_B, MOVE_AB};
	private RulerMoveStates _moveState = RulerMoveStates.NONE;

	private Object _touchOne;
	private Object _touchTwo;
	
	private static float _RULER_INTEREST = 30.f;

	// TRANSLATE
	private Point2D.Double _offsetA;
	private Point2D.Double _offsetB;

	private Point2D.Double _u, _v;
	
	SymbolMap _smap;
	
	private double _smallestDistancePx;
	
	public Ruler(SymbolMap map)
	{
		_smap = map;
		
		_A = new Point2D.Double(100., 800.);
		_B = new Point2D.Double(100., 100.);
		
		double d = _A.distance(_B);
		_u = new Point2D.Double((_B.x-_A.x)/d, (_B.y-_A.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
	}
	
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();
		
		final double timeSegmentsDurations[] = {2*10, 2*30, 2*60, 2*5*60, 2*10*60, 2*60*60};
		int timeSegmentIndex = 0;
		
		while (timeSegmentIndex < timeSegmentsDurations.length-1 && timeSegmentsDurations[timeSegmentIndex] * _smap.getPPM() * UAVModel.getReferenceCruiseSpeed() < _MIN_SEG_WIDTH)
		{
			timeSegmentIndex++;
		}
		
		final double distSegmentsLengths[] = {5., 10., 25., 50., 100., 200., 500., 1000., 5000., 10000., 50000., 100000.};
		int distSegmentIndex = 0;
		
		while (distSegmentIndex < distSegmentsLengths.length-1 && distSegmentsLengths[distSegmentIndex] * _smap.getPPM() < _MIN_SEG_WIDTH)
		{
			distSegmentIndex++;
		}
				
		double angle = Math.atan2(_B.y-_A.y, _B.x-_A.x);
		
		if (Math.cos(angle) > 0)
		{
			g2.translate(_A.x, _A.y);
			g2.rotate(angle);
		}
		else
		{
			g2.translate(_B.x, _B.y);
			g2.rotate(angle+Math.PI);
		}

		double maxL = _A.distance(_B);
		
		Rectangle2D.Double rect = new Rectangle2D.Double(-_HALFW, -_HALFW, maxL+_HALFW, 2*_HALFW);
		Ellipse2D.Double e1 = new Ellipse2D.Double(-_HALFW, -_HALFW, 2*_HALFW, 2*_HALFW);
		Ellipse2D.Double e2 = new Ellipse2D.Double(maxL-_HALFW, -_HALFW, 2*_HALFW, 2*_HALFW);
		Area outside = new Area(rect);
		outside.subtract(new Area(e1));
		outside.subtract(new Area(e2));
		g2.setClip(outside);
		
		paintRuler(g2, maxL, distSegmentsLengths[distSegmentIndex]*_smap.getPPM(), _smap.getPPM(), true);
		g2.translate(0, -_HALFW);
		paintRuler(g2, maxL, timeSegmentsDurations[timeSegmentIndex] * _smap.getPPM() * UAVModel.getReferenceCruiseSpeed(), _smap.getPPM() * UAVModel.getReferenceCruiseSpeed(), false);
		g2.translate(0, _HALFW);
		
		g2.setClip(null);

		double thick = 2.;
		e1 = new Ellipse2D.Double(-_HALFW+thick, -_HALFW+thick, 2*_HALFW-2*thick, 2*_HALFW-2*thick);
		e2 = new Ellipse2D.Double(maxL-_HALFW+thick, -_HALFW+thick, 2*_HALFW-2*thick, 2*_HALFW-2*thick);
		g2.setPaint(new Color(1.f, 1.f, 1.f, .5f));
		g2.setStroke(new BasicStroke((float) (2*thick)));
		g2.draw(e1);
		g2.draw(e2);
		
		g2.setTransform(old);
		
		_smallestDistancePx = distSegmentsLengths[distSegmentIndex]*_smap.getPPM();
	}
	
	private double paintRuler(Graphics2D g2, double length, double segmentLengthPx, double pixelsPerUnit, boolean meters)
	{
		final Color white = new Color(1.f, 1.f, 1.f, .5f);
		final Color black = new Color(0.f, 0.f, 0.f, .3f);
		
		double x;
		int color = 0;
		
		FontRenderContext frc = g2.getFontRenderContext();
		Font f = new Font("Futura", Font.PLAIN, 14);
		TextLayout textTl;
		Shape outline;
		
		for (x=0.; x<=length; x+=segmentLengthPx)
		{
			g2.setPaint(color%2==0 ? white : black);
			g2.fill(new Rectangle2D.Double(x, 0., segmentLengthPx, _HALFW));
			
			int n = (int) Math.round((x+segmentLengthPx)/pixelsPerUnit);
			
			String text = "";
			
			if (meters)
			{
				if (n%1000 == 0)
					text = n/1000+"km";
				else
					text = n+"m";
			}
			else
			{
				if (n%3600 == 0)
					text = n/3600+"h";
				else if (n%60 == 0)
					text = n/60+"min";
				else
					text = n+"s";
			}
			
			textTl = new TextLayout(text, f, frc);
			outline = textTl.getOutline(null);
			
			AffineTransform old = g2.getTransform();
			g2.translate(x+segmentLengthPx-outline.getBounds().width-2, _HALFW-4);
			g2.setPaint(color%2==0 ? new Color(0.f, 0.f, 0.f, .4f) : new Color(1.f, 1.f, 1.f, .6f));
			g2.fill(outline);
			g2.setTransform(old);
			
			color++;
		}
		
		return x;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_moveState == RulerMoveStates.MOVE_AB)
			return -1.f;

		double X = x-_A.x;
		double Y = y-_A.y;

		double u = X*_u.x + Y*_u.y;
		double v = X*_v.x + Y*_v.y;

		if (v > -2*_HALFW && v < 2*_HALFW && u > -2*_HALFW && u < _A.distance(_B)+2*_HALFW)
			return _RULER_INTEREST;
		else
			return -1.f;
	}

	public boolean isNearA(float x, float y)
	{
		return _A.distance(x, y) < 2*_HALFW;
	}

	public boolean isNearB(float x, float y)
	{
		return _B.distance(x, y) < 2*_HALFW;
	}
	
	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		switch (_moveState)
		{
			case NONE:
				if (isNearA(x, y))
				{
					_touchOne = touchref;
					_offsetA = new Point2D.Double(x-_A.x, y-_A.y);
					_moveState = RulerMoveStates.MOVE_A;
				}
				else if (isNearB(x, y))
				{
					_touchTwo = touchref;
					_offsetB = new Point2D.Double(x-_B.x, y-_B.y);
					_moveState = RulerMoveStates.MOVE_B;
				}
				else
				{
					_touchOne = touchref;
					_offsetA = new Point2D.Double(x-_A.x, y-_A.y);
					_offsetB = new Point2D.Double(x-_B.x, y-_B.y);
					_moveState = RulerMoveStates.TRANSLATE;
				}
				break;
				
			case MOVE_A:
				if (isNearB(x, y))
				{
					_touchTwo = touchref;
					_offsetB = new Point2D.Double(x-_B.x, y-_B.y);
					_moveState = RulerMoveStates.MOVE_AB;
				}
				break;
				
			case MOVE_B:
				if (isNearA(x, y))
				{
					_touchOne = touchref;
					_offsetA = new Point2D.Double(x-_A.x, y-_A.y);
					_moveState = RulerMoveStates.MOVE_AB;
				}
				break;
			
			case TRANSLATE:	
			default:
				break;
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		if (touchref != _touchOne && touchref != _touchTwo)
			return;
		
		switch (_moveState)
		{
			case TRANSLATE:
				if (touchref == _touchOne)
				{
					_A = new Point2D.Double(x-_offsetA.x, y-_offsetA.y);
					_B = new Point2D.Double(x-_offsetB.x, y-_offsetB.y);
				}
				break;
				
			case MOVE_A:
			case MOVE_B:
			case MOVE_AB:
					if (touchref == _touchOne)
					{
						_A = new Point2D.Double(x-_offsetA.x, y-_offsetA.y);
					}
					
					if (touchref == _touchTwo)
					{
						_B = new Point2D.Double(x-_offsetB.x, y-_offsetB.y);
					}
					
					break;
				
			case NONE:
			default:
				break;
		}
		
		double d = _A.distance(_B);
		_u = new Point2D.Double((_B.x-_A.x)/d, (_B.y-_A.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		switch (_moveState)
		{
			case TRANSLATE:
				_touchOne = null;
				_moveState = RulerMoveStates.NONE;
				break;
				
			case MOVE_A:
				_touchOne = null;
				_moveState = RulerMoveStates.NONE;
				break;
				
			case MOVE_B:
				_touchTwo = null;
				_moveState = RulerMoveStates.NONE;
				break;
				
			case MOVE_AB:
				
					if (touchref == _touchOne)
					{
						_touchOne = null;
						_moveState = RulerMoveStates.MOVE_B;
					}
					
					if (touchref == _touchTwo)
					{
						_touchTwo = null;
						_moveState = RulerMoveStates.MOVE_A;
					}
					
					break;
				
			case NONE:
			default:
				break;
		}
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_moveState = RulerMoveStates.NONE;
	}

	public double getSmallestPxStep()
	{
		return _smallestDistancePx;
	}

}

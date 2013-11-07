package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class LineMnvr extends Manoeuver
{
	// Points de la zone à regarder
	private LatLng _A, _B;

	private SymbolMap _smap;

	// Distance entre la zone à regarder et la trajectoire
	private double _currentRm = 500.;
	private double _lastRm;

	// Codage de l'orientation de la droite dans le plan ?
	private Point2D.Double _u, _v;

	static double RPX = 10.;

	private enum LineMnvrMoveStates {NONE, TRANSLATE, FULL};
	private LineMnvrMoveStates _moveState = LineMnvrMoveStates.NONE;

	private Object _touchOne;
	private Object _touchTwo;

	// TRANSLATE
	private Point2D.Double _offsetA;
	private Point2D.Double _offsetB;
	
	// FULL
	private LatLng _startA;
	private LatLng _startB;
	private Point2D.Double _startPosOne;
	private Point2D.Double _startPosTwo;
	private Point2D.Double _currentPosOne;
	private Point2D.Double _currentPosTwo;
	
	
// Inutile ??
//	/**
//	 * @param map
//	 * @param xA Point A x-coordinate (screen)
//	 * @param yA Point A y-coordinate (screen)
//	 * @param xB Point B x-coordinate (screen)
//	 * @param yB Point B y-coordinate (screen)
//	 */
//	public LineMnvr(SymbolMap map, double xA, double yA, double xB, double yB)
//	{
//		_A = map.getLatLngForScreen(xA, yA);
//		_B = map.getLatLngForScreen(xB, yB);
//
//		_smap = map;
//
//		double d = Point2D.Double.distance(xA, yA, xB, yB);
//		_u = new Point2D.Double((xB-xA)/d, (yB-yA)/d);
//		_v = new Point2D.Double(-_u.y, _u.x);
//
//	}

	public LineMnvr(SymbolMap map, LatLng A, LatLng B)
	{
		Point2D.Double a = map.getScreenForLatLng(A);
		Point2D.Double b = map.getScreenForLatLng(B);

		_A = A;
		_B = B;

		_smap = map;

		double d = a.distance(b);
		_u = new Point2D.Double((b.x-a.x)/d, (b.y-a.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
	}

	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();

		Point2D.Double Apx = _smap.getScreenForLatLng(_A);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_B);

		Area area = new Area();
		BasicStroke stroke = new BasicStroke((float) RPX*2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

		// Zone à regarder : ligne rouge
		GeneralPath line;
		line = new GeneralPath();
		line.moveTo(Apx.x, Apx.y);
		line.lineTo(Bpx.x, Bpx.y);

		area.add(new Area(stroke.createStrokedShape(line)));

		paintFootprint(g2, area);

		double Rpx = _smap.getPPM() * _currentRm;

		Point2D.Double LApx = new Point2D.Double(Apx.x + _v.x * Rpx,
				Apx.y + _v.y * Rpx);

		Point2D.Double LBpx = new Point2D.Double(Bpx.x + _v.x * Rpx,
				Bpx.y + _v.y * Rpx);

		// Trajectoire du drone : ligne bleue
		Line2D.Double l = new Line2D.Double(LApx, LBpx);
		paintAdjustLine(g2, l);

		g2.setTransform(old);
	}

	private Point2D.Double getUVforPx(double x, double y)
	{
		Point2D.Double Apx = _smap.getScreenForLatLng(_A);

		double X = x-Apx.x;
		double Y = y-Apx.y;

		return new Point2D.Double(X*_u.x + Y*_u.y, X*_v.x + Y*_v.y);
	}

	@Override
	public boolean adjustAtPx(double x, double y)
	{
		// x,y : coordonnée du toucher écran
		// On projette tout en screen
		// Coordonnées écran des deux points (zone rouge)
		Point2D.Double Apx = _smap.getScreenForLatLng(_A);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_B);

		// Parallélisme avec la zone à regarder
		Point2D.Double p = getUVforPx(x, y);
		double u = p.x;
		double v = p.y;

		double currentRpx = _currentRm * _smap.getPPM();
		double lastRpx = _lastRm * _smap.getPPM();

		if (_adjusting)
		{
			currentRpx += v - lastRpx;
			lastRpx = v;

			_currentRm = currentRpx / _smap.getPPM();
			_lastRm = lastRpx / _smap.getPPM();

			return true;
		}

		if (isAdjustmentInterestedAtPx(x, y))
		{
			lastRpx = v;
			_adjusting = true;
		}

		_currentRm = currentRpx / _smap.getPPM();
		_lastRm = lastRpx / _smap.getPPM();

		return _adjusting;
	}

	public boolean isAdjustmentInterestedAtPx(double x, double y)
	{
		// On projette tout en screen
		Point2D.Double Apx = _smap.getScreenForLatLng(_A);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_B);

		double X = x-Apx.x;
		double Y = y-Apx.y;

		double u = X*_u.x + Y*_u.y;
		double v = X*_v.x + Y*_v.y;

		double currentRpx = _currentRm * _smap.getPPM();

		return v > currentRpx-GRIP && v < currentRpx+GRIP && u > -GRIP && u < Apx.distance(Bpx)+GRIP;
	}

	private LatLng getOffsetPoint(LatLng point)
	{
		UTMRef utm = _A.toUTMRef();
		char letter = utm.getLatZone();
		int number = utm.getLngZone();
		
		double north = utm.getEasting() + _currentRm * _v.y;
		double east = utm.getNorthing() + _currentRm * _v.x;
		
		UTMRef utmOff = new UTMRef(north, east, letter, number);
		
		return utmOff.toLatLng();
	}
	
	/**
	 * Getter pour le point A de la trajectoire du drone (et non de la zone à regarder).
	 * 
	 * @return coordonnée en {@link LatLng} du point.
	 */
	public LatLng getTrajA()
	{
		return getOffsetPoint(_A);
	}

	/**
	 * Getter pour le point B de la trajectoire du drone (et non de la zone à regarder).
	 * 
	 * @return coordonnée en {@link LatLng} du point.
	 */
	public LatLng getTrajB()
	{
		return getOffsetPoint(_B);
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_moveState == LineMnvrMoveStates.FULL)
			return -1.f;

		// On projette tout en screen
		Point2D.Double Apx = _smap.getScreenForLatLng(_A);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_B);

		double X = x-Apx.x;
		double Y = y-Apx.y;

		double u = X*_u.x + Y*_u.y;
		double v = X*_v.x + Y*_v.y;

		if (v > -2*RPX && v < 2*RPX && u > -2*RPX && u < Apx.distance(Bpx)+2*RPX)
			return getGeneralInterest();
		else
			return -1.f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		super.addTouch(x, y, touchref);
		
		switch (_moveState)
		{
			case FULL:
				return;
			case TRANSLATE:
				_touchTwo = touchref;
				_startA = _A;
				_startB = _B;
				_startPosTwo = new Point2D.Double(x, y);
				_currentPosOne = _startPosOne;
				_currentPosTwo = _startPosTwo;
				_moveState = LineMnvrMoveStates.FULL;
				return;
			case NONE:
				_touchOne = touchref;
				_startPosOne = new Point2D.Double(x, y);
				Point2D.Double pA = _smap.getScreenForLatLng(_A);
				_offsetA = new Point2D.Double(x-pA.x, y-pA.y);
				Point2D.Double pB = _smap.getScreenForLatLng(_B);
				_offsetB = new Point2D.Double(x-pB.x, y-pB.y);
				_moveState = LineMnvrMoveStates.TRANSLATE;
				return;
			default:
				return;
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		if (touchref != _touchOne && touchref != _touchTwo)
			return;
		
		super.updateTouch(x, y, touchref);
		
		switch (_moveState)
		{
			case FULL:				
				if (touchref == _touchOne)
					_currentPosOne = new Point2D.Double(x, y);
				else
					_currentPosTwo = new Point2D.Double(x, y);
				updateGeometry();
				return;
				
			case TRANSLATE:
				if (touchref == _touchOne)
				{
					_A = _smap.getLatLngForScreen(x-_offsetA.x, y-_offsetA.y);
					_B = _smap.getLatLngForScreen(x-_offsetB.x, y-_offsetB.y);
					_startPosOne = new Point2D.Double(x, y);
				}
				return;
				
			case NONE:
			default:
				return;
		}
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		_moveState = LineMnvrMoveStates.NONE;
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_moveState = LineMnvrMoveStates.NONE;
	}

	private void updateGeometry()
	{
		Array2DRowRealMatrix x = new Array2DRowRealMatrix(new double[][]
				{
					{ 0, _startPosOne.getX(), _startPosTwo.getX() },
					{ 0, _startPosOne.getY(), _startPosTwo.getY() },
					{ 1, 1, 1 }
				});

		Array2DRowRealMatrix y = new Array2DRowRealMatrix(new double[][]
				{
					{ 0, _currentPosOne.getX(), _currentPosTwo.getX() },
					{ 0, _currentPosOne.getY(), _currentPosTwo.getY() },
					{ 0, 0, 0 }
				});
		
		DecompositionSolver solver = new LUDecomposition(x).getSolver();
		if (!solver.isNonSingular())
			return;
		double[][] data = y.multiply(solver.getInverse()).getData();
		
		AffineTransform t = new AffineTransform(new double[] { data[0][0], data[1][0], data[0][1], data[1][1], data[0][2], data[1][2] });
	
		Point2D.Double Apx = _smap.getScreenForLatLng(_startA);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_startB);
		
		t.transform(Apx, Apx);
		t.transform(Bpx, Bpx);

		_A = _smap.getLatLngForScreen(Apx.x, Apx.y);
		_B = _smap.getLatLngForScreen(Bpx.x, Bpx.y);
		
		double d = Apx.distance(Bpx);
		_u = new Point2D.Double((Bpx.x-Apx.x)/d, (Bpx.y-Apx.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
	}

}





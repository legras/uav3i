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

import com.deev.interaction.touch.Touchable;

public class Ruler implements Touchable
{
	Point2D.Double _A, _B;

	static double RPX = 10.;
	
	private enum RulerMoveStates {NONE, TRANSLATE, FULL};
	private RulerMoveStates _moveState = RulerMoveStates.NONE;

	private Object _touchOne;
	private Object _touchTwo;
	
	private static float _RULER_INTEREST = 30.f;

	// TRANSLATE
	private Point2D.Double _offsetA;
	private Point2D.Double _offsetB;
	
	// FULL
	private Point2D.Double _startA;
	private Point2D.Double _startB;
	private Point2D.Double _startPosOne;
	private Point2D.Double _startPosTwo;
	private Point2D.Double _currentPosOne;
	private Point2D.Double _currentPosTwo;

	private Point2D.Double _u, _v;
	
	SymbolMap _smap;
	
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

		GeneralPath p = new GeneralPath();		
		
		p.moveTo(_A.x, _A.y);
		p.lineTo(_B.x,  _B.y);
		
		g2.setPaint(Color.RED);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(p);

		g2.setTransform(old);
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_moveState == RulerMoveStates.FULL)
			return -1.f;

		double X = x-_A.x;
		double Y = y-_A.y;

		double u = X*_u.x + Y*_u.y;
		double v = X*_v.x + Y*_v.y;

		if (v > -2*RPX && v < 2*RPX && u > -2*RPX && u < _A.distance(_B)+2*RPX)
			return _RULER_INTEREST;
		else
			return -1.f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
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
				_moveState = RulerMoveStates.FULL;
				return;
			case NONE:
				_touchOne = touchref;
				_startPosOne = new Point2D.Double(x, y);
				_offsetA = new Point2D.Double(x-_A.x, y-_A.y);
				_offsetB = new Point2D.Double(x-_B.x, y-_B.y);
				_moveState = RulerMoveStates.TRANSLATE;
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
		
		switch (_moveState)
		{
			case FULL:				
				if (touchref == _touchOne)
					_currentPosOne = new Point2D.Double(x, y);
				else
					_currentPosTwo = new Point2D.Double(x, y);
				updateGeometry();
				break;
				
			case TRANSLATE:
				if (touchref == _touchOne)
				{
					_A = new Point2D.Double(x-_offsetA.x, y-_offsetA.y);
					_B = new Point2D.Double(x-_offsetB.x, y-_offsetB.y);
					_startPosOne = new Point2D.Double(x, y);
				}
				break;
				
			case NONE:
			default:
				break;
		}
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		_moveState = RulerMoveStates.NONE;
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_moveState = RulerMoveStates.NONE;
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
	
		Point2D.Double Apx = new Point2D.Double(_startA.x, _startA.y);
		Point2D.Double Bpx = new Point2D.Double(_startB.x, _startB.y);
		
		t.transform(Apx, Apx);
		t.transform(Bpx, Bpx);

		_A = Apx;
		_B = Bpx;		
		
		double d = _A.distance(_B);
		_u = new Point2D.Double((_B.x-_A.x)/d, (_B.y-_A.y)/d);
		_v = new Point2D.Double(-_u.y, _u.x);
	}

}

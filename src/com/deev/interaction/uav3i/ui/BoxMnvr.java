package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;

import uk.me.jstott.jcoord.LatLng;

public class BoxMnvr extends Manoeuver
{
	// Points de la zone à regarder
	private LatLng _A, _B;

	private enum BoxMnvrMoveStates {NONE, TRANSLATE, FULL};
	private BoxMnvrMoveStates _moveState = BoxMnvrMoveStates.NONE;

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

	public BoxMnvr(SymbolMap map, LatLng A, LatLng B)
	{
		_A = A;
		_B = B;

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
		Rectangle2D.Double box = getBoxOnScreen();

		if (_buttons != null)
			_buttons.setPositions(new Point2D.Double(box.getCenterX(), box.getMaxY()), 40, Math.PI/2, false);
	}

	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();
		
		Rectangle2D.Double box = getBoxOnScreen();
		
		paintFootprint(g2, box, isSubmitted());
		
		g2.setTransform(old);
	}

	private Rectangle2D.Double getBoxOnScreen()
	{
		Point2D.Double Apx = _smap.getScreenForLatLng(_A);
		Point2D.Double Bpx = _smap.getScreenForLatLng(_B);
		
		Rectangle2D.Double box = new Rectangle2D.Double(Apx.x, Apx.y, 0, 0);
		box.add(Bpx);
		
		return box;
	}
	
	@Override
	public boolean adjustAtPx(double x, double y)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (_moveState == BoxMnvrMoveStates.FULL)
			return -1.f;
		
		Rectangle2D.Double box = getBoxOnScreen();
		
		if (box.contains(x, y))
			return getMoveInterest();
		else
			return -1.f;
	}



	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		super.addTouch(x, y, touchref);
		
		if (!isModifiable())
		{
			cancelTouch(touchref);
			return;
		}
		
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
				_moveState = BoxMnvrMoveStates.FULL;
				return;
			case NONE:
				_touchOne = touchref;
				_startPosOne = new Point2D.Double(x, y);
				Point2D.Double pA = _smap.getScreenForLatLng(_A);
				_offsetA = new Point2D.Double(x-pA.x, y-pA.y);
				Point2D.Double pB = _smap.getScreenForLatLng(_B);
				_offsetB = new Point2D.Double(x-pB.x, y-pB.y);
				_moveState = BoxMnvrMoveStates.TRANSLATE;
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
				break;
				
			case TRANSLATE:
				if (touchref == _touchOne)
				{
					_A = _smap.getLatLngForScreen(x-_offsetA.x, y-_offsetA.y);
					_B = _smap.getLatLngForScreen(x-_offsetB.x, y-_offsetB.y);
					_startPosOne = new Point2D.Double(x, y);
				}
				break;
				
			case NONE:
			default:
				break;
		}
		
		positionButtons();
	}
	
	private void updateGeometry()
	{		
		Rectangle2D.Double startbox = new Rectangle2D.Double(_startPosOne.x, _startPosOne.y, 0, 0);
		startbox.add(_startPosTwo);
		
		AffineTransform t = new AffineTransform();
		
		Point2D.Double A = _smap.getScreenForLatLng(_startA);
		Point2D.Double B = _smap.getScreenForLatLng(_startB);
		
		Point2D.Double Ap = new Point2D.Double(A.x, A.y);
		Point2D.Double Bp = new Point2D.Double(B.x, B.y);
		
		if (startbox.width > startbox.height/2) // EST-OUEST
		{
			if (_currentPosOne.x == _currentPosTwo.x)
				return;
			
			Ap.x = _currentPosOne.x + (_currentPosTwo.x-_currentPosOne.x)/(_startPosTwo.x-_startPosOne.x)*(A.x-_startPosOne.x);
			Bp.x = _currentPosOne.x + (_currentPosTwo.x-_currentPosOne.x)/(_startPosTwo.x-_startPosOne.x)*(B.x-_startPosOne.x);
		}
		
		if (startbox.height > startbox.width/2) // NORD-SUD
		{
			if (_currentPosOne.y == _currentPosTwo.y)
				return;
			
			Ap.y = _currentPosOne.y + (_currentPosTwo.y-_currentPosOne.y)/(_startPosTwo.y-_startPosOne.y)*(A.y-_startPosOne.y);
			Bp.y = _currentPosOne.y + (_currentPosTwo.y-_currentPosOne.y)/(_startPosTwo.y-_startPosOne.y)*(B.y-_startPosOne.y);
		}
		

		_A = _smap.getLatLngForScreen(Ap.x, Ap.y);
		_B = _smap.getLatLngForScreen(Bp.x, Bp.y);
	}
	
	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		_moveState = BoxMnvrMoveStates.NONE;
		
		positionButtons();
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		_moveState = BoxMnvrMoveStates.NONE;
		
		positionButtons();
	}

	@Override
	public boolean isAdjustmentInterestedAtPx(double x, double y)
	{
		return false;
	}

}

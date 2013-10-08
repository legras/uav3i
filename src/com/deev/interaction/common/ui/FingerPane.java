package com.deev.interaction.common.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.CircleMnvr;
import com.deev.interaction.uav3i.ui.LineMnvr;
import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;
import com.deev.interaction.uav3i.ui.SymbolMap;

@SuppressWarnings("serial")
public class FingerPane extends JComponent implements Touchable
{	
	private SymbolMap _smap;
	private HashMap<Object, FingerDrawing> _drawings;
	
	public FingerPane()
	{
		super();
				
		_drawings = new HashMap<Object, FingerDrawing>();
		
		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{	
		Graphics2D g2 = (Graphics2D) g;

		synchronized (_drawings)
		{
			Iterator<FingerDrawing> itr = _drawings.values().iterator();
			
			while(itr.hasNext())
				itr.next().paintAnimation(g2);
		}
	}

	
	public void interpret(Gesture gesture)
	{		
		BoundingRectangle rectangle = gesture.getSmallestEnclosingRectangle();
		
		double delta = 40.;
		
		if (rectangle.width < delta && rectangle.height < delta)
		{
			LatLng p = _smap.getLatLngForScreen((float) rectangle.x, (float) rectangle.y);
			_smap.setManoeuver(new CircleMnvr(_smap, p));
			
			return;
		}
		
		if (rectangle.height < delta)
		{
			Point2D.Double Am = _smap.pixelsToMeters(rectangle.x + rectangle.width/2.*Math.sin(-rectangle.theta),
											         rectangle.y + rectangle.width/2.*Math.cos(-rectangle.theta));
			
			Point2D.Double Bm = _smap.pixelsToMeters(rectangle.x - rectangle.width/2.*Math.sin(-rectangle.theta),
					 								 rectangle.y - rectangle.width/2.*Math.cos(-rectangle.theta));
			
			_smap.setManoeuver(new LineMnvr(_smap, Am.x, Am.y, Bm.x, Bm.y));
			
			return;
		}
		
		if (rectangle.width < delta)
		{
			Point2D.Double Am = _smap.pixelsToMeters(rectangle.x + rectangle.height/2.*Math.sin(-rectangle.theta),
											         rectangle.y + rectangle.height/2.*Math.cos(-rectangle.theta));
			
			Point2D.Double Bm = _smap.pixelsToMeters(rectangle.x - rectangle.height/2.*Math.sin(-rectangle.theta),
					 								 rectangle.y - rectangle.height/2.*Math.cos(-rectangle.theta));
			
			_smap.setManoeuver(new LineMnvr(_smap, Am.x, Am.y, Bm.x, Bm.y));
			
			return;
		}
	}
	
	public void setTopMap(SymbolMap map)
	{
		_smap = map;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		if (MainFrame.getAppState() == MainFrameState.COMMAND)
			return 20.f;
		else
			return -1.f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		synchronized (_drawings)
		{
			_drawings.put(touchref, new FingerDrawing(x, y));
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		synchronized (_drawings)
		{
			_drawings.get(touchref).addPoint(x, y);
		}
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		synchronized (_drawings)
		{
			FingerDrawing fd = _drawings.get(touchref);
			interpret(new Gesture(fd.getPoints()));
			fd.release();	
			Animator.addAnimation(fd);
		}
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		Animator.removeAnimation(_drawings.get(touchref));
		_drawings.remove(touchref);
	}
	
	
}

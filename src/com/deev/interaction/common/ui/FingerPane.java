package com.deev.interaction.common.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVDataStore;
import com.deev.interaction.uav3i.ui.CircleMnvr;
import com.deev.interaction.uav3i.ui.LineMnvr;
import com.deev.interaction.uav3i.ui.LineMnvr;
import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;
import com.deev.interaction.uav3i.ui.SymbolMap;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.UAV3iSettings.Mode;

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
			//_smap.setManoeuver(new CircleMnvr(_smap, p));
			CircleMnvr circleMnvr = new CircleMnvr(_smap, p);
			_smap.setManoeuver(circleMnvr);
			
			// Si on est connecté à Paparazzi...
			if(UAV3iSettings.getMode() == Mode.IVY)
			{
	      // Déplacement du "CircleCenter" prédéterminé au point désiré. 
	      UAVDataStore.getIvyCommunication().moveWayPointCircleCenter(p);
	      // Rayon du "CircleCenter"
	      UAVDataStore.getIvyCommunication().setNavRadius(circleMnvr.getCurrentRadius());
	      // Demande de l'exécution après paramétrage
	      UAVDataStore.getIvyCommunication().jumpToCircle();
			}
			
			return;
		}
		
		if (rectangle.height < delta)
		{
			LatLng A = _smap.getLatLngForScreen(rectangle.x + rectangle.width/2.*Math.sin(-rectangle.theta),
											         rectangle.y + rectangle.width/2.*Math.cos(-rectangle.theta));
			
			LatLng B = _smap.getLatLngForScreen(rectangle.x - rectangle.width/2.*Math.sin(-rectangle.theta),
					 								 rectangle.y - rectangle.width/2.*Math.cos(-rectangle.theta));
			
			_smap.setManoeuver(new LineMnvr(_smap, A, B));
			
			return;
		}
		
		if (rectangle.width < delta)
		{
			LatLng A = _smap.getLatLngForScreen(rectangle.x + rectangle.height/2.*Math.sin(-rectangle.theta),
											         rectangle.y + rectangle.height/2.*Math.cos(-rectangle.theta));
			
			LatLng B = _smap.getLatLngForScreen(rectangle.x - rectangle.height/2.*Math.sin(-rectangle.theta),
					 								 rectangle.y - rectangle.height/2.*Math.cos(-rectangle.theta));
			
			_smap.setManoeuver(new LineMnvr(_smap, A, B));
			
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
			return 10.f;
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

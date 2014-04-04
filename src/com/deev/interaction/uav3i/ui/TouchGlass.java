package com.deev.interaction.uav3i.ui;


import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.BoundingRectangle;
import com.deev.interaction.touch.CircleAnim;
import com.deev.interaction.touch.ComponentLayer;
import com.deev.interaction.touch.Gesture;
import com.deev.interaction.touch.Touchable;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;


/**
 * @author legras
 *
 */
@SuppressWarnings("serial")
public class TouchGlass extends JComponent implements Touchable, TuioListener, MouseListener, MouseMotionListener
{		
	private ArrayList<Touchable> _touchables;
	private HashMap<Object, Touchable> _touched;
			
	ArrayList<Point2D.Double> _hull = null;
	
	public TouchGlass()
	{
		super();
		
		setOpaque(false);	
		
		_touchables = new ArrayList<Touchable>();
		_touched = new HashMap<Object, Touchable>();
				
		if (UAV3iSettings.getTUIO())
		{
			TuioClient tuio = new TuioClient();
			tuio.addTuioListener(this);
			tuio.connect();
			LoggerUtil.LOG.log(Level.CONFIG, "TUIO OK");
		}
		
		if (UAV3iSettings.getFullscreen())
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			setCursor(toolkit.createCustomCursor(new BufferedImage(3, 3,
					BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "NO_CURSOR"));
		}
	}
	
	public void addTouchable(Touchable t)
	{
		synchronized (_touchables)
		{
			_touchables.add(t);
		}
	}

	@Override
	public void addTuioCursor(TuioCursor arg0)
	{
		addTouch(arg0.getX()*getBounds().width, arg0.getY()*getBounds().height, arg0);
	}

	@Override
	public void refresh(TuioTime arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTuioCursor(TuioCursor arg0)
	{
		removeTouch(arg0.getX()*getBounds().width, arg0.getY()*getBounds().height, arg0);
	}

	@Override
	public void updateTuioCursor(TuioCursor arg0)
	{
		updateTouch(arg0.getX()*getBounds().width, arg0.getY()*getBounds().height, arg0);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		addTouch(e.getXOnScreen(), e.getYOnScreen(), this);
		updateTouch(e.getXOnScreen(), e.getYOnScreen(), this);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		updateTouch(e.getXOnScreen(), e.getYOnScreen(), this);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		removeTouch(e.getXOnScreen(), e.getYOnScreen(), this);
	}

	@Override
	public void addTuioObject(TuioObject arg0) {}

	@Override
	public void updateTuioObject(TuioObject arg0) {}

	@Override
	public void removeTuioObject(TuioObject arg0) {}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		synchronized (_touchables)
		{
			float interest = Float.NEGATIVE_INFINITY;
			Touchable T = null;
			
			Iterator<Touchable> itr = _touchables.iterator();
			while(itr.hasNext())
			{
				Touchable t = itr.next();
				float i = t.getInterestForPoint(x, y);
				if (i > interest)
				{
					T = t;
					interest = i;
				}
			}
			
			T.addTouch(x, y, touchref);
			_touched.put(touchref, T);
		}
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		synchronized (_touched)
		{
			Touchable T = _touched.get(touchref);
			
			T.updateTouch(x, y, touchref);
		}
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		synchronized (_touched)
		{
			Touchable T = _touched.get(touchref);
			
			T.removeTouch(x, y, touchref);
		}
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		synchronized (_touched)
		{
			Touchable T = _touched.get(touchref);
			
			T.cancelTouch(touchref);
		}
	}
}

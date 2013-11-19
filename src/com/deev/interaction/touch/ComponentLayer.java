package com.deev.interaction.touch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class ComponentLayer extends JComponent implements Touchable
{	
	public ComponentLayer()
	{
		super();
				
		setVisible(true);
		setOpaque(false);
		Color back = new Color(0.f, 0.f, 0.f, .0f);
		setBackground(back);
	}
	
	public boolean press(int x, int y, long time)
	{
		Component c = SwingUtilities.getDeepestComponentAt(this, x, y);
		MouseEvent e;
		Point p;
		
		if (c == null)
			return false;
		
		p = SwingUtilities.convertPoint(this, x,  y, c);
		
		e = new MouseEvent(c, MouseEvent.MOUSE_PRESSED, time, InputEvent.BUTTON1_MASK, p.x, p.y, 1, false);
		c.dispatchEvent(e);
				
		return !c.equals(this);
	}
	
	public void release(int x, int y, long time)
	{
		Component c = SwingUtilities.getDeepestComponentAt(this, x, y);
		MouseEvent e;
		Point p;
		
		p = SwingUtilities.convertPoint(this, x,  y, c);
		
		e = new MouseEvent(c, MouseEvent.MOUSE_RELEASED, time, InputEvent.BUTTON1_MASK,
				p.x, p.y, 0, false);
		c.dispatchEvent(e);
		
		e = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, time+5, InputEvent.BUTTON1_MASK,
				p.x, p.y, 1, false);
		c.dispatchEvent(e);
	}

	@Override
	public float getInterestForPoint(float x, float y)
	{
		Component c = SwingUtilities.getDeepestComponentAt(this, (int) x, (int) y);
		
		return c != this ? 1000.f : -1f;
	}

	@Override
	public void addTouch(float x, float y, Object touchref)
	{
		press((int) x, (int) y, System.currentTimeMillis());
	}

	@Override
	public void updateTouch(float x, float y, Object touchref)
	{
		
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		release((int) x, (int) y, System.currentTimeMillis());
	}

	@Override
	public void cancelTouch(Object touchref)
	{
		// TODO Auto-generated method stub
		
	}
}

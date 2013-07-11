package com.deev.interaction.common.ui;

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
public class ComponentLayer extends JComponent
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
		
		if (c == null)
			return false;
		
		e = new MouseEvent(c, MouseEvent.MOUSE_PRESSED, time, InputEvent.BUTTON1_MASK,
				x-c.getX(), y-c.getY(), 1, false);
		c.dispatchEvent(e);
				
		return !c.equals(this);
	}
	
	public void release(int x, int y, long time)
	{
		Component c = SwingUtilities.getDeepestComponentAt(this, x, y);
		MouseEvent e;
		
		e = new MouseEvent(c, MouseEvent.MOUSE_RELEASED, time, InputEvent.BUTTON1_MASK,
				x-c.getX(), y-c.getY(), 0, false);
		c.dispatchEvent(e);
		
		e = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, time+5, InputEvent.BUTTON1_MASK,
				x-c.getX(), y-c.getY(), 1, false);
		c.dispatchEvent(e);
	}
}

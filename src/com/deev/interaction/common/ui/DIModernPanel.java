package com.deev.interaction.common.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DIModernPanel extends JPanel
{
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		
		Color light, dark;
	
		light = new Color(.3f, .3f, .3f, 1.f);
		dark = new Color(.15f, .15f, .15f, 1.f);
		
		GradientPaint grad = new GradientPaint(new Point2D.Double(0., 0.), light,
											   new Point2D.Double(0., getHeight()), dark);
		
		g2.setPaint(grad);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		g2.setPaint(Color.BLACK);
		g2.drawRect(0, 0, getWidth()-1, getHeight()-1);	
		
		g2.setPaint(light);
		g2.drawRect(1, 1, getWidth()-3, getHeight()-3);
	}
}

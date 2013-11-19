package com.deev.interaction.touch;


import java.awt.*;
import java.awt.geom.*;
import javax.swing.plaf.synth.*;


public class RoundedBoxPainter extends SynthPainter
{
	int _corner = 0;
	Color _borderColor = Color.BLUE;
	Color _backgroundColor = Color.RED;
	
	public void setCorner(int c)
	{
		_corner = c;
	}
	
	public void setBorderColor(String c)
	{
		_borderColor = Color.decode(c);
	}

	public void setBackgroundColor(String c)
	{
		_backgroundColor = Color.decode(c);
	}		
	
	public void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., (h-5.), (h-5.));
		
		g2.setPaint(_backgroundColor);		
		g2.fill(rect);
		g2.setPaint(_borderColor);		
		g2.draw(rect);
		
		g2.setPaint(null);
	}

	public void paintToggleButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., _corner, _corner);
		
		g2.setPaint(_backgroundColor);		
		g2.fill(rect);
		g2.setPaint(_borderColor);		
		g2.draw(rect);
		
		g2.setPaint(null);
	}
	
	public void paintLabelBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
	}
	
	public void paintCheckBoxBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., _corner, _corner);
		
		g2.setPaint(_backgroundColor);		
		g2.fill(rect);
		
		g2.setPaint(null);
	}
		
	public void paintRadioButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., _corner, _corner);
		
		g2.setPaint(_backgroundColor);		
		g2.fill(rect);
		
		g2.setPaint(null);
	}	
	
	public void paintArrowButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		paintToggleButtonBackground(context, g, x, y, w, h);
	}		
	
	public void paintScrollBarThumbBackground(SynthContext context, Graphics g, int x, int y, int w, int h,
	 																			int orientation)
	{
		paintToggleButtonBackground(context, g, x, y, w, h);
	}
		
	public void paintListBorder(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		Area a = new Area(new Rectangle2D.Double(x, y, w, h));
		Area rect = new Area (new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., _corner, _corner));
		a.exclusiveOr(rect);
		
		g2.setPaint(_backgroundColor);		
		g2.fill(a);
		g2.setPaint(_borderColor);		
		g2.draw(rect);
		
		g2.setPaint(null);
	}	
	
	public void paintProgressBarForeground(SynthContext context, Graphics g, int x, int y, int w, int h, int orientation)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		Rectangle2D.Double rect = new Rectangle2D.Double(x+1., y+1., w-2., h-2.);
		
		g2.setPaint(_backgroundColor);		
		g2.fill(rect);
		g2.setPaint(_borderColor);		
		g2.draw(rect);
		
		g2.setPaint(null);
	}
		
	public void paintProgressBarBorder(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh;
		rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,	RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,		RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
				
		g2.setStroke(new BasicStroke(2.0f));
		
		Area a = new Area(new Rectangle2D.Double(x, y, w, h));
		Area rect = new Area (new RoundRectangle2D.Double(x+2., y+2., w-4., h-4., _corner, _corner));
		a.exclusiveOr(rect);
		
		g2.setPaint(Color.BLACK);		
		g2.fill(a);
		g2.setPaint(_borderColor);		
		g2.draw(rect);
		
		g2.setPaint(null);
	}	
		
}

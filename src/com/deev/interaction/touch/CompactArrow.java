package com.deev.interaction.touch;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class CompactArrow
{
	protected static Path2D.Double _arrowR = null;
	protected static Path2D.Double _arrowL = null;
	
	public static void drawRight(Graphics2D g2, String string, double size)
	{
		FontRenderContext frc = g2.getFontRenderContext();
	    Font f = new Font("HelveticaNeue-bold", Font.PLAIN, (int) size-6);
	    TextLayout textTl;
	    Shape tol;

	    AffineTransform o = g2.getTransform();
	    
	    textTl = new TextLayout(string, f, frc);
	    tol = textTl.getOutline(null);
	    g2.translate(size/5, (size-6)/2-5);
	    g2.fill(tol);
	    
	    g2.translate(tol.getBounds().width, 0);
	    g2.translate(size/5, -(size-6)/2+5);
	    g2.fill(CompactArrow.getArrowR());
	    
		g2.setTransform(o);
	}
	
	public static void drawLeft(Graphics2D g2, String string, double size)
	{
		FontRenderContext frc = g2.getFontRenderContext();
	    Font f = new Font("HelveticaNeue-bold", Font.PLAIN, (int) size-6);
	    TextLayout textTl;
	    Shape tol;

	    AffineTransform o = g2.getTransform();
	    
	    textTl = new TextLayout(string, f, frc);
	    tol = textTl.getOutline(null);
	    g2.translate(-size/5 - tol.getBounds().width, (size-6)/2-5);
	    g2.fill(tol);
	    
	    g2.translate(-size/5, -(size-6)/2+5);
	    g2.fill(CompactArrow.getArrowL());
	    
		g2.setTransform(o);
	}
	
	public static Path2D.Double getArrowR()
	{
		if (_arrowR != null)
			return _arrowR;
		
		_arrowR = new Path2D.Double();
		_arrowR.moveTo(0, 0);
		_arrowR.lineTo(0, 2);
		_arrowR.lineTo(15, 2);
		_arrowR.lineTo(9, 8);
		_arrowR.lineTo(12, 11);
		_arrowR.lineTo(23, 0);
		_arrowR.lineTo(12, -11);
		_arrowR.lineTo(9, -8);
		_arrowR.lineTo(15, -2);
		_arrowR.lineTo(0, -2);
		_arrowR.closePath();
		
		return _arrowR;
	}
	
	public static Path2D.Double getArrowL()
	{
		if (_arrowL != null)
			return _arrowL;
		
		_arrowL = new Path2D.Double();
		_arrowL.moveTo(0, 0);
		_arrowL.lineTo(0, 2);
		_arrowL.lineTo(-15, 2);
		_arrowL.lineTo(-9, 8);
		_arrowL.lineTo(-12, 11);
		_arrowL.lineTo(-23, 0);
		_arrowL.lineTo(-12, -11);
		_arrowL.lineTo(-9, -8);
		_arrowL.lineTo(-15, -2);
		_arrowL.lineTo(0, -2);
		_arrowL.closePath();
		
		return _arrowL;
	}
}

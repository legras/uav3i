package com.deev.interaction.common.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.text.ParseException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthPainter;

// Style		light	dark
// std			#666666	#404040
// stdPressed	#989898	#595959
// blue			#0F86FF	#0F59A6
// bluePressed
// toggled		#FFF200	#D98C00

public class DIModernPlaf extends SynthPainter
{
	Color _light = Color.WHITE;
	Color _dark = Color.BLACK;

	public static void initModernLookAndFeel()
	{
   		SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();

   		// SynthLookAndFeel load() method throws a checked exception
   		// (java.text.ParseException) so it must be handled
   		try
 		{
      		lookAndFeel.load(DIModernPlaf.class.getResourceAsStream("DIModernPlaf.xml"), DIModernPlaf.class);
			UIManager.setLookAndFeel(lookAndFeel);
   		} 
   		catch (ParseException e)
 		{
      		System.err.println("Couldn't get specified look and feel ("
                                   	+ lookAndFeel
                                    + "), for some reason.");
      		System.err.println("Using the default look and feel.");
      		e.printStackTrace();
   		}
	   	catch (UnsupportedLookAndFeelException e)
	 	{
   			System.err.println("Couldn't get specified look and feel ("
	                                  	+ lookAndFeel
	                                    + "), for some reason.");
	      	System.err.println("Using the default look and feel.");
	      	e.printStackTrace();
	   	}
	}

	public static void initTestLookAndFeel()
	{
   		SynthLookAndFeel lookAndFeel = new SynthLookAndFeel();

   		// SynthLookAndFeel load() method throws a checked exception
   		// (java.text.ParseException) so it must be handled
   		try
 		{
      		lookAndFeel.load(DIModernPlaf.class.getResourceAsStream("synthDemo.xml"), DIModernPlaf.class);
			UIManager.setLookAndFeel(lookAndFeel);
   		} 
   		catch (ParseException e)
 		{
      		System.err.println("Couldn't get specified look and feel ("
                                   	+ lookAndFeel
                                    + "), for some reason.");
      		System.err.println("Using the default look and feel.");
      		e.printStackTrace();
   		}
	   	catch (UnsupportedLookAndFeelException e)
	 	{
   			System.err.println("Couldn't get specified look and feel ("
	                                  	+ lookAndFeel
	                                    + "), for some reason.");
	      	System.err.println("Using the default look and feel.");
	      	e.printStackTrace();
	   	}
	}
	
	public void setLightColor(String c)
	{
		_light = Color.decode(c);
	}

	public void setDarkColor(String c)
	{
		_dark = Color.decode(c);
	}
	
	// Nécessaire de faires des méthodes diffénentes pour les différents widgets, même si elles ont le même code...
	public void paintButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		paintGenericButtonBackground(context, g, x, y, w, h);
	}
	
	public void paintToggleButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		paintGenericButtonBackground(context, g, x, y, w, h);
	}

	
	public void paintGenericButtonBackground(SynthContext context, Graphics g, int x, int y, int w, int h)
	{
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
		g2.setStroke(new BasicStroke(1));
		
		GradientPaint grad = new GradientPaint(new Point2D.Double(0., 0.), _light,
				 							 new Point2D.Double(0., h), _dark);
		
		g2.setPaint(grad);
		g2.fillRect(0, 0, w, h);
		
		g2.setPaint(Color.BLACK);
		g2.drawRect(0, 0, w-1, h-1);
		
		g2.setPaint(_light);
		g2.drawRect(1, 1, w-3, h-3);	
		
		g2.setPaint(null);
	}
}

package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import com.deev.interaction.touch.Palette;

import eu.telecom_bretagne.uav3i.util.log.LoggerUtil;

public enum Palette3i 
{	
	BUTTON_WHITE_BG(1.f, 1.f, 1.f, .5f),
	BUTTON_DISABLED_BG("img/stripesGray32.png", 16),
	TIME_LIGHT(.9f, .9f, .9f, .5f),
	TIME_LIGHTER(1.f, 1.f, 1.f, .5f),
	TIME_DARK(.9f, .9f, .9f, .5f),
	TIME_DARKER(.9f, .9f, .9f, .5f);
	
	private Paint _paint;
	
	Palette3i(float r, float g, float b, float a)
	{
		_paint = new Color(r, g, b, a);
	}
	
	Palette3i(String filename, int size)
	{
		try
		{
			URL url = Palette3i.class.getResource(filename);
			_paint = Palette.makeTexture(url, 16);
		}
		catch (IOException e)
		{
			LoggerUtil.LOG.log(Level.SEVERE, "Could not load "+filename+", using ugly solid color.");
		}
	}
	
	public Paint getPaint()
	{
		return _paint;
	}
	
	public static Paint getPaint(Palette3i p)
	{
		return p.getPaint();
	}
	
}

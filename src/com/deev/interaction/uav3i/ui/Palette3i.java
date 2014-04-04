package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;

import com.deev.interaction.touch.Palette;

import com.deev.interaction.uav3i.util.log.LoggerUtil;

public enum Palette3i 
{	
	BUTTON_WHITE_BG(1.f, 1.f, 1.f, .5f),
	BUTTON_DISABLED_BG("img/stripesGray32.png", 16),
	BUTTON_DELAY_BG(0.f, 0.f, 0.f, .1f),
	TIME_LIGHT(.75f, .75f, .75f, .7f),
	TIME_LIGHTER(.80f, .80f, .80f, .7f),
	TIME_LIGHT_TEXT(.5f, .5f, .5f, 1.f),
	TIME_DARK(.4f, .4f, .4f, .7f),
	TIME_DARKER(.3f, .3f, .3f, .7f),
	TIME_DARK_TEXT(.9f, .9f, .9f, 1.f),
	TIME_DOT(.3f, .3f, .3f, 1.f),
	TIME_CURSOR(.95f, .07f, .07f, 1.f),
	TIME_CURSOR_FILL(.95f, .07f, .07f, .5f);
	
	
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

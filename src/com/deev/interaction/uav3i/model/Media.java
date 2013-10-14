package com.deev.interaction.uav3i.model;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Touchable;

// TODO J'ai dû passer les attributs en public car j'ai passé Media dans un package dédié. C'est mal. Il faut prévoir des getters (voir des setters).

// TODO begin et endFocus n'ont rien à faire là, on est dans le modèle.

public class Media
{

	public long start;
	public long length;
	
	public long beginFocus;
	public long endFocus;
	
	public String name;
	
	public enum MediaType {LOCAL, DOWNLOADING};
	public MediaType _type;
	
	public Media(String title)
	{
		super();
		this.name = title;
		_type = MediaType.LOCAL;
	}
	
	public void setType(MediaType type)
	{
		_type = type;
	}
	
	public MediaType getType()
	{
		return _type;
	}
	
	public void join(Media m)
	{
		long endMe = start + length;
		long endHim = m.start + m.length;
		
		start = m.start < start ? m.start : start;
		endMe = endMe < endHim ? endHim : endMe;
		
		length = endMe - start;
	}

	public boolean doesTouch(Media m)
	{
		return isTimeInside(m.start) || isTimeInside(m.start+m.length) ||
			m.isTimeInside(start) || m.isTimeInside(start+length);
	}
	
	public boolean isTimeInside(long time)
	{
		return start <= time && time <= start+length;
	}
}

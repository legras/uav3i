package com.deev.interaction.uav3i.replay;


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

@SuppressWarnings("serial")
public class Marqueur
{

	long time;
	
	enum side {DEBUT, FIN};
	boolean side;

	public Marqueur(long time, boolean side)
	{
		
		super();
		this.time = time;
		this.side = side;
		
	}
	


}

package com.deev.interaction.touch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JToggleButton;

public class ZeroRoundWToggle extends JToggleButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6827723936968056808L;
	
	private static Color _ALPHA_WHITE = new Color(1.f, 1.f, 1.f, .5f);
	private static TexturePaint _DISABLED_BG = null;
	
	private BufferedImage _onIcon = null;
	private BufferedImage _offIcon = null;

	
	public ZeroRoundWToggle(BufferedImage on, BufferedImage off)
	{
		super();
		setPreferredSize(new Dimension(on.getWidth(), on.getHeight()));
		setOpaque(false);
		setBorderPainted(false);
		
		_onIcon = on;
		_offIcon = off;
		
		if (_DISABLED_BG == null)
		{
			BufferedImage stripes;
			try
			{
				stripes = ImageIO.read(this.getClass().getResource("stripesGray32.png"));

				_DISABLED_BG = new TexturePaint(stripes, new Rectangle2D.Double(0, 0, 16, 16));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		if (isEnabled())
			g2.setColor(_ALPHA_WHITE);
		else
			g2.setPaint(_DISABLED_BG);
		g.fillOval(0, 0, getWidth(), getHeight());
		
		if (isSelected())
		{
			g2.drawImage(_onIcon, 0, 0, null);
		}
		else
		{
			g2.drawImage(_offIcon, 0, 0, null);
		}
	}
}

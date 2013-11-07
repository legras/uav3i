package com.deev.interaction.common.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;

public class ZeroRoundWToggle extends JToggleButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6827723936968056808L;
	
	private static Color _ALPHA_WHITE = new Color(1.f, 1.f, 1.f, .5f);
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
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(_ALPHA_WHITE);
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

package com.deev.interaction.common.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;


public class Zero80ToggleButton extends JToggleButton
{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8791329042235551399L;
	private BufferedImage _onIcon = null;
	private BufferedImage _offIcon = null;
	private BufferedImage _altIcon = null;
	private boolean _isAlt = true;
		
	public Zero80ToggleButton(BufferedImage on, BufferedImage off)
	{
		super();
		setPreferredSize(new Dimension(80, 80));
		setOpaque(false);
		setBorderPainted(false);
		
		_onIcon = on;
		_offIcon = off;
	}
	
	public Zero80ToggleButton(BufferedImage on, BufferedImage off, BufferedImage alt_on)
	{
		this(on, off);
		_altIcon = alt_on;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		// g.setColor(Color.BLUE);
		// g.fillRect(0, 0, 80, 80);
		
		if (isSelected())
		{
			if (_altIcon != null && _isAlt)
				g2.drawImage(_altIcon, 0, 0, null);
			else
				g2.drawImage(_onIcon, 0, 0, null);
		}
		else
		{
			g2.drawImage(_offIcon, 0, 0, null);
		}
	}
	
	public void setAlt(boolean alt)
	{
		_isAlt = alt;
	}
	
	public boolean getAlt()
	{
		return _isAlt;
	}
}

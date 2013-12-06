package com.deev.interaction.touch;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import javax.swing.JToggleButton;

import com.deev.interaction.uav3i.ui.Palette3i;

import eu.telecom_bretagne.uav3i.util.log.LoggerUtil;

public class RoundToggleButton extends JToggleButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6827723936968056808L;
		
	private BufferedImage _onIcon = null;
	private BufferedImage _offIcon = null;
	private BufferedImage _altIcon = null;
	private boolean _isAlt = true;

	
	public RoundToggleButton(BufferedImage on, BufferedImage off)
	{
		super("dummy");
		
		LoggerUtil.LOG.log(Level.INFO, "inside");
		
		if (on == null || off == null)
			LoggerUtil.LOG.log(Level.WARNING, "Making toggle button, null image");
		
		LoggerUtil.LOG.log(Level.INFO, "Making toggle button, size "+ on.getWidth() +"x"+ on.getHeight());
		setPreferredSize(new Dimension(on.getWidth(), on.getHeight()));
		setOpaque(false);
		setBorderPainted(false);
		
		_onIcon = on;
		_offIcon = off;
	}

	
	public RoundToggleButton(BufferedImage on, BufferedImage off, BufferedImage alt_on)
	{
		this(on, off);
		_altIcon = alt_on;
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		if (isEnabled())
			g2.setPaint(Palette3i.getPaint(Palette3i.BUTTON_WHITE_BG));
		else
			g2.setPaint(Palette3i.getPaint(Palette3i.BUTTON_DISABLED_BG));
		
		g2.fill(new Ellipse2D.Double(0, 0, _onIcon.getWidth()-1, _onIcon.getHeight()-1));
		
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

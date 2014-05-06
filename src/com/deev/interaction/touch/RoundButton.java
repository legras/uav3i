package com.deev.interaction.touch;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import javax.swing.JButton;

import com.deev.interaction.uav3i.ui.Palette3i;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

public class RoundButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6827723936968056808L;
		
  private BufferedImage _icon = null;
	private double _pie = -1.;
	
  public RoundButton(BufferedImage icon)
	{
		super();

		if (icon == null)
			LoggerUtil.LOG.log(Level.WARNING, "Making toggle button, null image");
		
    setPreferredSize(new Dimension(icon.getWidth(), icon.getHeight()));
		setOpaque(false);
		setBorderPainted(false);
		
		_icon = icon;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		if (isEnabled())
			g2.setPaint(Palette3i.getPaint(Palette3i.BUTTON_WHITE_BG));
		else
			g2.setPaint(Palette3i.getPaint(Palette3i.BUTTON_DISABLED_BG));
		
		g2.fill(new Ellipse2D.Double(0, 0, _icon.getWidth()-1, _icon.getHeight()-1));
		
		if (_pie > 0.)
		{
			g2.setPaint(Palette3i.getPaint(Palette3i.BUTTON_DELAY_BG));
			double S = _icon.getWidth()/2.;
			g2.fill(new Ellipse2D.Double(S*_pie, S*_pie, 2*S*(1-_pie), 2*S*(1-_pie)));
		}
		
		g2.drawImage(_icon, 0, 0, null);
	}
	
	public void setPie(double pie)
	{
		_pie = pie;
	}
}

package com.deev.interaction.uav3i.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.RoundToggleButton;

public class Splash3i extends JComponent implements Animation, ActionListener
{
	private enum Splash3iStates {SHOWING, HIDING, IDLE};
	private static double _RATE = .3;
	private static BufferedImage _splashImage = null;
	private Splash3iStates _state = Splash3iStates.IDLE;
	private double _offset = 3000;
	private Rectangle _refBounds;
	
	public Splash3i(JComponent layer) throws IOException
	{
		if (_splashImage == null)	
			_splashImage = ImageIO.read(this.getClass().getResource("/img/splash.png"));
				
		class Splash3iSwingBuilder implements Runnable
		{
			Splash3i _splash;
			JComponent _layer;
			
			public Splash3iSwingBuilder(final Splash3i splash, final JComponent layer)
			{
				super();
				
				_splash = splash;
				_layer = layer;
				_layer.add(splash);
			}
			
			public void run()
			{
								
				setVisible(true);
			}
		}
		
		SwingUtilities.invokeLater(new Splash3iSwingBuilder(this, layer));
				
		Animator.addAnimation(this);
	}
	
	public void setCenter(int x, int y)
	{
		_refBounds = new Rectangle(
				x-_splashImage.getWidth()/2,
				y-_splashImage.getHeight()/2,
				_splashImage.getWidth(),
				_splashImage.getHeight());
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(_splashImage, 0, 0, null);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (_state != Splash3iStates.IDLE)
			return;
		
		if (_offset > 1500.)
			_state = Splash3iStates.SHOWING;
		else
		{
			_offset = 1.;
			_state = Splash3iStates.HIDING;
		}
	}

	@Override
	public int tick(int time)
	{
		switch (_state)
		{
			case SHOWING:
				_offset *= _RATE;
				if (_offset < 1)
				{
					_offset = 0;
					_state = Splash3iStates.IDLE;
				}
				setBounds();
				break;
				
			case HIDING:
				_offset /= _RATE;
				if (_offset > 3000)
				{
					_state = Splash3iStates.IDLE;
				}
				setBounds();
				break;
				
			case IDLE:
				
			default:
				break;
		}
		
		return 1;
	}

	@Override
	public int life()
	{
		return 1;
	}

	private void setBounds()
	{
		final Splash3i splash = this;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{	
				splash.setBounds(_refBounds.x, _refBounds.y+(int)_offset, _refBounds.width, _refBounds.height);
			}
		});
	}
	
}

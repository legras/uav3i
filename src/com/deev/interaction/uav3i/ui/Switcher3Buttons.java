package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;

import com.deev.interaction.touch.TintedBufferedImage;
import com.deev.interaction.touch.ZeroPanel;
import com.deev.interaction.touch.RoundToggleButton;
import com.deev.interaction.uav3i.model.VideoModel;


public class Switcher3Buttons extends ZeroPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524343907872790149L;
	
	public enum Switcher3ButtonsMode {COMMAND, MAP, REPLAY};
	
	private RoundToggleButton _commandButton;
	private RoundToggleButton _mapButton;
	private RoundToggleButton _replayButton;
	private ButtonGroup _group;
	
	
	private static int _d = 12;
	
	public Switcher3Buttons(ActionListener listener)
	{
		super();
		
		setSize(new Dimension(3*80+2*_d, 80));
		
		Color blue = new Color(0.f, .5f, 1.f, 1.f);
		Color gray = new Color(.3f, .3f, .3f, 1.f);

		BufferedImage command_on = null;
		BufferedImage command_off = null;
		BufferedImage map_on = null;
		BufferedImage map_off = null;
		BufferedImage replay_on = null;
		BufferedImage replay_off = null;
		BufferedImage replay_alt = null;
		
		try
		{
			command_on = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/command_on.png")), blue);
			command_off = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/command_off.png")), gray);
			map_on = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/map_on.png")), blue);
			map_off = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/map_off.png")), gray);
			replay_on = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/replay_on.png")), blue);
			replay_off = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/replay_off.png")), gray);
			replay_alt = new TintedBufferedImage(ImageIO.read(this.getClass().getResource("/img/replay_pause.png")), blue);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		_commandButton = new RoundToggleButton(command_on, command_off);
		_mapButton = new RoundToggleButton(map_on, map_off);
		_replayButton = new RoundToggleButton(replay_on, replay_off, replay_alt);
				
		_commandButton.addActionListener(this);
		_mapButton.addActionListener(this);
		_replayButton.addActionListener(this);
		
		_commandButton.addActionListener(listener);
		_mapButton.addActionListener(listener);
		_replayButton.addActionListener(listener);
		
		_group = new ButtonGroup();
		_group.add(_commandButton);
		_group.add(_mapButton);
		_group.add(_replayButton);
		
		this.setLayout(null);
		
		this.add(_commandButton);
		this.add(_mapButton);
		this.add(_replayButton);
		
		_commandButton.setBounds(0, 0, 80, 80);
		_mapButton.setBounds(80+_d, 0, 80, 80);
		_replayButton.setBounds(160+2*_d, 0, 80, 80);
		
		setVisible(true);
		
		_mapButton.setSelected(true);
	}
	
	public Switcher3ButtonsMode getMode()
	{
		if (_commandButton.isSelected()) return Switcher3ButtonsMode.COMMAND;
		if (_replayButton.isSelected()) return Switcher3ButtonsMode.REPLAY;
		return Switcher3ButtonsMode.MAP;
	}
	

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() != _replayButton)
			_replayButton.setAlt(true);
		else
		{
			_replayButton.setAlt(!_replayButton.getAlt());
			
			if (_replayButton.getAlt())
			{
				VideoModel.video.play();
			}
			else
			{
				VideoModel.video.pause();
			}
		}
	}
	
	public void resetToMap()
	{
		_replayButton.setAlt(false);
		_mapButton.setSelected(true);
	}
	
	public void resetPlay()
	{
		_replayButton.setAlt(false);
	}
}







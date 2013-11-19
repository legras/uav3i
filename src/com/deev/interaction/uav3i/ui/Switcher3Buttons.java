package com.deev.interaction.uav3i.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;

import com.deev.interaction.touch.Zero80ToggleButton;
import com.deev.interaction.touch.ZeroPanel;


public class Switcher3Buttons extends ZeroPanel implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524343907872790149L;
	
	public enum Mode {COMMAND, MAP, REPLAY};
	
	private Zero80ToggleButton _commandButton;
	private Zero80ToggleButton _mapButton;
	private Zero80ToggleButton _replayButton;
	private ButtonGroup _group;
	
	
	private static int _d = 2;
	
	public Switcher3Buttons(ActionListener listener)
	{
		super();
		
		setSize(new Dimension(3*80+2*_d, 80));

		BufferedImage command_on = null;
		BufferedImage command_off = null;
		BufferedImage map_on = null;
		BufferedImage map_off = null;
		BufferedImage replay_on = null;
		BufferedImage replay_off = null;
		BufferedImage replay_alt = null;
		
		try
		{
			command_on = ImageIO.read(this.getClass().getResource("img/command_on.png"));
			command_off = ImageIO.read(this.getClass().getResource("img/command_off.png"));
			map_on = ImageIO.read(this.getClass().getResource("img/map_on.png"));
			map_off = ImageIO.read(this.getClass().getResource("img/map_off.png"));
			replay_on = ImageIO.read(this.getClass().getResource("img/replay_on.png"));
			replay_off = ImageIO.read(this.getClass().getResource("img/replay_off.png"));
			replay_alt = ImageIO.read(this.getClass().getResource("img/replay_pause.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		_commandButton = new Zero80ToggleButton(command_on, command_off);
		_mapButton = new Zero80ToggleButton(map_on, map_off);
		_replayButton = new Zero80ToggleButton(replay_on, replay_off, replay_alt);
				
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
	
	public Mode getMode()
	{
		if (_commandButton.isSelected()) return Mode.COMMAND;
		if (_replayButton.isSelected()) return Mode.REPLAY;
		return Mode.MAP;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() != _replayButton)
			_replayButton.setAlt(true);
		else
			_replayButton.setAlt(!_replayButton.getAlt());
	}
}







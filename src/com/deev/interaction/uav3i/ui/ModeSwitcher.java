package com.deev.interaction.uav3i.ui;

import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import com.deev.interaction.common.ui.DIModernPanel;


public class ModeSwitcher extends DIModernPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524343907872790149L;
	
	public enum Mode {COMMAND, MAP, REPLAY};
	
	private JToggleButton _commandButton;
	private JToggleButton _mapButton;
	private JToggleButton _replayButton;
	private ButtonGroup _group;
	
	public ModeSwitcher(ActionListener listener)
	{
		super();
		
		_commandButton = new JToggleButton("Command");
		_mapButton = new JToggleButton("Map");
		_replayButton = new JToggleButton("Replay");
		
		_mapButton.setSelected(true);
		
		_commandButton.addActionListener(listener);
		_mapButton.addActionListener(listener);
		_replayButton.addActionListener(listener);
		
		_group = new ButtonGroup();
		_group.add(_commandButton);
		_group.add(_mapButton);
		_group.add(_replayButton);
		
		this.add(_commandButton);
		this.add(_mapButton);
		this.add(_replayButton);
	}
	
	public Mode getMode()
	{
		if (_commandButton.isSelected()) return Mode.COMMAND;
		if (_replayButton.isSelected()) return Mode.REPLAY;
		return Mode.MAP;
	}
}

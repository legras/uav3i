package com.deev.interaction.uav3i.ui;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import com.deev.interaction.common.ui.DIModernPanel;


public class ModeSwitcher extends DIModernPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4524343907872790149L;
	
	private JToggleButton _commandButton;
	private JToggleButton _mapButton;
	private JToggleButton _replayButton;
	private ButtonGroup _group;
	
	public ModeSwitcher()
	{
		super();
		
		_commandButton = new JToggleButton("Command");
		_mapButton = new JToggleButton("Map");
		_replayButton = new JToggleButton("Replay");
		
		_group = new ButtonGroup();
		_group.add(_commandButton);
		_group.add(_mapButton);
		_group.add(_replayButton);
		
		this.add(_commandButton);
		this.add(_mapButton);
		this.add(_replayButton);
	}
	
}

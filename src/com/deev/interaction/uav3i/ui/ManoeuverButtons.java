package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.deev.interaction.common.ui.TintedBufferedImage;
import com.deev.interaction.common.ui.ZeroPanel;
import com.deev.interaction.common.ui.ZeroRoundWToggle;

public class ManoeuverButtons extends ZeroPanel
{
	private ZeroRoundWToggle _jumpButton;	
	private ZeroRoundWToggle _submitButton;
	private ZeroRoundWToggle _pinButton;
	private ZeroRoundWToggle _deleteButton;

	private static int _pad = 4;
	
	private Manoeuver _manoeuver = null;

	public ManoeuverButtons(ActionListener listener) throws IOException
	{
		super();
		
		Color blue = new Color(0.f, .5f, 1.f, 1.f);
		
		_jumpButton = new ZeroRoundWToggle(
				getImage("uavIconOn.png", blue), 
				getImage("uavIconOff.png", Color.BLACK));
		_jumpButton.addActionListener(listener);

		_submitButton = new ZeroRoundWToggle(
				getImage("submitIconOn.png", blue), 
				getImage("submitIconOff.png", Color.BLACK));
		_submitButton.addActionListener(listener);

		_pinButton = new ZeroRoundWToggle(
				getImage("pinIconOn.png", blue), 
				getImage("pinIconOff.png", Color.BLACK));
		_pinButton.addActionListener(listener);

		_deleteButton = new ZeroRoundWToggle(
				getImage("deleteIcon.png", blue), 
				getImage("deleteIcon.png", Color.BLACK));
		_deleteButton.addActionListener(listener);

		this.setLayout(null);

		add(_jumpButton);
		add(_submitButton);
		add(_pinButton);
		add(_deleteButton);

		int w = _jumpButton.getPreferredSize().width;
		int h = _jumpButton.getPreferredSize().height;

		setSize(new Dimension(4*w + 3*_pad, h));

		_jumpButton.setBounds(0, 0, w, h);
		_submitButton.setBounds(w+_pad, 0, w, h);
		_pinButton.setBounds(2*(w+_pad), 0, w, h);
		_deleteButton.setBounds(3*(w+_pad), 0, w, h);

		setVisible(true);
	}
	
	public void attach(Manoeuver mnvr)
	{
		if (mnvr == _manoeuver)
			return;
		
		_manoeuver = mnvr;
	}

	private  BufferedImage getImage(String name, Color tint) throws IOException
	{
		return new TintedBufferedImage(ImageIO.read(this.getClass().getResource(name)), tint);
	}
}

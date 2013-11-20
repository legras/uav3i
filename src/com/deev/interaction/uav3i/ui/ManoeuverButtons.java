package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.TintedBufferedImage;
import com.deev.interaction.touch.ZeroPanel;
import com.deev.interaction.touch.ZeroRoundWToggle;
import com.deev.interaction.uav3i.model.UAVModel;

public class ManoeuverButtons implements Animation, ActionListener
{
	private enum ManoeuverButtonsStates {SHOWING, HIDING, IDLE};
	private static double _RATE = .3;
	private static ManoeuverButtons _BUTTONS_SHOWN = null;
	
	private ZeroRoundWToggle _jumpButton;	
	private ZeroRoundWToggle _submitButton;
	private ZeroRoundWToggle _pinButton;
	private ZeroRoundWToggle _deleteButton;

	private int _size;
	
	private ManoeuverButtonsStates _state = ManoeuverButtonsStates.IDLE;
	
	private static int _PAD = 13;
	
	private Manoeuver _manoeuver = null;
	private JComponent _home;
	private boolean _isDead = false;
	private long _deleteEnabledTime = -1;
	
	private Point2D.Double _positions[] = {null, null, null, null};
	private Point2D.Double _posVect[] = {null, null, null, null};
	private double _offset = 3000;

	public ManoeuverButtons(Manoeuver mnvr) throws IOException
	{		
		Color blue = new Color(0.f, .5f, 1.f, 1.f);
		Color gray = new Color(.3f, .3f, .3f, 1.f);
		
		_manoeuver = mnvr;
		
		_jumpButton = new ZeroRoundWToggle(
				getImage("img/uavIconOn.png", blue), 
				getImage("img/uavIconOff.png", gray));
		_jumpButton.addActionListener(this);

		_submitButton = new ZeroRoundWToggle(
				getImage("img/submitIconOn.png", blue), 
				getImage("img/submitIconOff.png", gray));
		_submitButton.addActionListener(this);

		_pinButton = new ZeroRoundWToggle(
				getImage("img/pinIconOn.png", blue), 
				getImage("img/pinIconOff.png", gray));
		_pinButton.addActionListener(this);

		_deleteButton = new ZeroRoundWToggle(
				getImage("img/deleteIcon.png", Color.RED), 
				getImage("img/deleteIcon.png", gray));
		_deleteButton.addActionListener(this);
		
		Animator.addAnimation(this);
		
		show();
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == _jumpButton)
		{
			setJump(_jumpButton.isSelected());
			
			if (_jumpButton.isSelected())
				UAVModel.jumpToManoeuver(_manoeuver);
		}
		else if (e.getSource() == _submitButton)
		{
			setSubmitted(_submitButton.isSelected());
			
			if (_submitButton.isSelected())
				UAVModel.submitManoeuver(_manoeuver);
		}
		else if (e.getSource() == _pinButton)
		{
			
		}
		else if (e.getSource() == _deleteButton)
		{
			if (!_deleteButton.isSelected())
			{
				_isDead = true;
				_manoeuver.delete();
				hide();
			}
			else
			{
				_deleteEnabledTime = System.currentTimeMillis();
			}
		}
	}
	
	public boolean isSubmitted()
	{
		return _submitButton.isSelected();
	}
	
	public void setSubmitted(boolean sub)
	{
		_submitButton.setSelected(sub);
		_submitButton.setEnabled(!sub);
	}
	
	public void setJump(boolean jump)
	{
		setSubmitted(jump);
		_jumpButton.setSelected(jump);
		_jumpButton.setEnabled(!jump);
	}
	
	public void show()
	{
		_state = ManoeuverButtonsStates.SHOWING;
		
		if (_BUTTONS_SHOWN != this && _BUTTONS_SHOWN != null)
			_BUTTONS_SHOWN.hide();
		
		_BUTTONS_SHOWN = this;
	}
	
	public void hide()
	{
		if (_offset < 1)
			_offset = 1;
		_state = ManoeuverButtonsStates.HIDING;
	}

	public void addTo(JComponent component)
	{
		component.add(_jumpButton);
		component.add(_submitButton);
		component.add(_pinButton);
		component.add(_deleteButton);

		_jumpButton.setBounds(0, 0, _size, _size);
		_submitButton.setBounds(0, 0, _size, _size);
		_pinButton.setBounds(0, 0, _size, _size);
		_deleteButton.setBounds(0, 0, _size, _size);
		
		_home = component;
	}
	
	public void remove()
	{
		if (_home == null)
			return;
		
		_home.remove(_jumpButton);
		_home.remove(_submitButton);
		_home.remove(_pinButton);
		_home.remove(_deleteButton);
		
		_home = null;
	}

	public void setPositions(Point2D.Double ref, double distance, double theta, boolean isArc)
	{
		if (isArc)
		{
			double delta = (_size + _PAD) / distance;
			
			for (int i=0; i<4; i++)
			{
				double a = theta + (-1.5+i)*delta;
				_positions[i] = new Point2D.Double(ref.x + distance * Math.cos(a), ref.y + distance * Math.sin(a));
			}
		}
		else
		{	
			Point2D.Double middle = new Point2D.Double(ref.x, ref.y);

			middle.x += distance * Math.cos(theta);
			middle.y += distance * Math.sin(theta);

			double u = - (_size + _PAD) * Math.sin(theta);
			double v =   (_size + _PAD) * Math.cos(theta);

			double l;

			for (int i=0; i<4; i++)
			{
				l = -1.5 + i;
				_positions[i] = new Point2D.Double(middle.x+l*u, middle.y+l*v);
			}
		}

		for (int i=0; i<4; i++)	
		{
			_posVect[i] = new Point2D.Double(_positions[i].x-ref.x, _positions[i].y-ref.y);
			_posVect[i].x /= _positions[i].distance(ref);
			_posVect[i].y /= _positions[i].distance(ref);
		}
		
		setBounds();
	}

	private void setBounds()
	{
		_jumpButton.setBounds(
				(int) _positions[0].x-_size/2 + (int) (_offset * _posVect[0].x),
				(int) _positions[0].y-_size/2 + (int) (_offset * _posVect[0].y),
				_size, _size);
		
		_submitButton.setBounds(
				(int) _positions[1].x-_size/2 + (int) (_offset * _posVect[1].x),
				(int) _positions[1].y-_size/2 + (int) (_offset * _posVect[1].y),
				_size, _size);
		
		_pinButton.setBounds(
				(int) _positions[2].x-_size/2 + (int) (_offset * _posVect[2].x),
				(int) _positions[2].y-_size/2 + (int) (_offset * _posVect[2].y),
				_size, _size);
		
		_deleteButton.setBounds(
				(int) _positions[3].x-_size/2 + (int) (_offset * _posVect[3].x),
				(int) _positions[3].y-_size/2 + (int) (_offset * _posVect[3].y),
				_size, _size);	
	}
	
	private  BufferedImage getImage(String name, Color tint) throws IOException
	{
		BufferedImage image = ImageIO.read(this.getClass().getResource(name));
		_size = image.getWidth();
		
		return new TintedBufferedImage(image, tint);
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
					_state = ManoeuverButtonsStates.IDLE;
				}
				setBounds();
				break;
				
			case HIDING:
				_offset /= _RATE;
				if (_offset > 3000)
				{
					_state = ManoeuverButtonsStates.IDLE;
				}
				setBounds();
				break;
			case IDLE:
				if (_isDead)
					remove();
				
				if (_deleteEnabledTime > 0 && System.currentTimeMillis() - _deleteEnabledTime > 2000)
				{
					_deleteButton.setSelected(false);
					_deleteEnabledTime = -1;
				}
				
			default:
				break;
		}
		
		return 1;
	}

	@Override
	public int life()
	{
		return _isDead ? 0 : 1;
	}
}

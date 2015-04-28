package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.TintedBufferedImage;
import com.deev.interaction.touch.RoundToggleButton;
import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;


public class ManoeuverButtons implements Animation, ActionListener
{
	private enum ManoeuverButtonsStates {SHOWING, HIDING, IDLE};
	private static double _RATE = .3;
	private static long _DELETE_DELAY = 2000;
	private static ManoeuverButtons _BUTTONS_SHOWN = null;

	private static BufferedImage _uavIconOn = null;
	private static BufferedImage _uavIconOff = null;
	private static BufferedImage _submitIconOn = null;
	private static BufferedImage _submitIconOff = null;
	private static BufferedImage _pinIconOn = null;
	private static BufferedImage _pinIconOff = null;
	private static BufferedImage _deleteIcon = null;
	private static BufferedImage _deleteIconWait = null;

	RoundToggleButton jumpButton;	
	RoundToggleButton submitButton;
	RoundToggleButton pinButton;
	RoundToggleButton deleteButton;

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


	public ManoeuverButtons(Manoeuver mnvr, JComponent layer) throws IOException
	{
		Color blue = new Color(0.f, .5f, 1.f, 1.f);
		Color gray = new Color(.3f, .3f, .3f, 1.f);

		if (_uavIconOn == null) 		_uavIconOn 		= getImage("/img/uavIconOn.png", blue);
		if (_uavIconOff == null) 		_uavIconOff 	= getImage("/img/uavIconOff.png", gray);
		if (_submitIconOn == null) 		_submitIconOn 	= getImage("/img/submitIconOn.png", blue);
		if (_submitIconOff == null) 	_submitIconOff 	= getImage("/img/submitIconOff.png", gray);
		if (_pinIconOn == null) 		_pinIconOn 		= getImage("/img/pinIconOn.png", blue);
		if (_pinIconOff == null) 		_pinIconOff 	= getImage("/img/pinIconOff.png", gray);
		if (_deleteIcon == null) 		_deleteIcon 	= getImage("/img/deleteIcon.png", Color.RED);
		if (_deleteIconWait == null) 	_deleteIconWait = getImage("/img/deleteIcon.png", gray);

		_manoeuver = mnvr;
		_home = layer;
		_size = _uavIconOn.getWidth();

		class ManoeuverButtonsSwingBuilder implements Runnable
		{
			ManoeuverButtons _buttons;
			JComponent _layer;

			public ManoeuverButtonsSwingBuilder(final ManoeuverButtons buttons, final JComponent layer)
			{
				_buttons = buttons;
				_layer = layer;
			}

			public void run()
			{
				jumpButton = new RoundToggleButton(_uavIconOn, _uavIconOff);
				submitButton = new RoundToggleButton(_submitIconOn, _submitIconOff);
				pinButton = new RoundToggleButton(_pinIconOn, _pinIconOff);
				deleteButton = new RoundToggleButton(_deleteIcon, _deleteIconWait);

				_layer.add(jumpButton);
				_layer.add(submitButton);
				_layer.add(pinButton);
				_layer.add(deleteButton);

				jumpButton.setBounds(0, 0, _size, _size);
				submitButton.setBounds(0, 0, _size, _size);
				pinButton.setBounds(0, 0, _size, _size);
				deleteButton.setBounds(0, 0, _size, _size);

				jumpButton.setVisible(true);
				submitButton.setVisible(true);
				pinButton.setVisible(true);
				deleteButton.setVisible(true);
				
				if (MainFrame.getSymbolMap().areShareAndAskLocked())
				{
					submitButton.setEnabled(false);
					jumpButton.setEnabled(false);
				}

				_buttons.addActionListener(_buttons);

				show();
			}
		}

		SwingUtilities.invokeLater(new ManoeuverButtonsSwingBuilder(this, layer));

		Animator.addAnimation(this);
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == jumpButton)
		{			
			if (jumpButton.isSelected())
				MainFrame.getSymbolMap().askExecutionManoeuver(_manoeuver);
		}
		else if (e.getSource() == submitButton)
		{			
			if (submitButton.isSelected())
				MainFrame.getSymbolMap().shareManoeuver(_manoeuver);
			else
				MainFrame.getSymbolMap().shareManoeuver(null);			
		}
		else if (e.getSource() == pinButton)
		{

		}
		else if (e.getSource() == deleteButton)
		{
			if (!deleteButton.isSelected())
			{
				_isDead = true;
				if (_manoeuver.isShared())
					UAVModel.clearManoeuver();
				_manoeuver.delete();
			}
			else
			{
				_deleteEnabledTime = System.currentTimeMillis();
			}
		}
	}

	public boolean isPinned()
	{
		if (pinButton == null)
			return false;

		return pinButton.isSelected();
	}

	public boolean isModifiable()
	{
		if (submitButton == null)
			return false;

		return !isPinned();
	}

	public boolean isSubmitted()
	{
		if (submitButton == null)
			return false;

		return submitButton.isSelected();
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

	public void addActionListener(ActionListener listener)
	{
		jumpButton.addActionListener(listener);
		submitButton.addActionListener(listener);
		pinButton.addActionListener(listener);
		deleteButton.addActionListener(listener);		
	}

	public void remove()
	{
		if (_home == null)
			return;

		_home.remove(jumpButton);
		_home.remove(submitButton);
		_home.remove(pinButton);
		_home.remove(deleteButton);

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

	protected void setBounds()
	{
		if (_positions[0] == null || _posVect[0] == null)
			return;

		if (jumpButton == null || submitButton == null || pinButton == null || deleteButton == null)
			return;

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{	
				jumpButton.setBounds(
						(int) _positions[0].x-_size/2 + (int) (_offset * _posVect[0].x),
						(int) _positions[0].y-_size/2 + (int) (_offset * _posVect[0].y),
						_size, _size);

				submitButton.setBounds(
						(int) _positions[1].x-_size/2 + (int) (_offset * _posVect[1].x),
						(int) _positions[1].y-_size/2 + (int) (_offset * _posVect[1].y),
						_size, _size);

				pinButton.setBounds(
						(int) _positions[2].x-_size/2 + (int) (_offset * _posVect[2].x),
						(int) _positions[2].y-_size/2 + (int) (_offset * _posVect[2].y),
						_size, _size);

				deleteButton.setBounds(
						(int) _positions[3].x-_size/2 + (int) (_offset * _posVect[3].x),
						(int) _positions[3].y-_size/2 + (int) (_offset * _posVect[3].y),
						_size, _size);
			}
		});

	}

	private BufferedImage getImage(String name, Color tint) throws IOException
	{
		BufferedImage image = ImageIO.read(this.getClass().getResource(name));

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

			if (_deleteEnabledTime > 0)
			{
				if (System.currentTimeMillis() - _deleteEnabledTime > _DELETE_DELAY)
				{
					deleteButton.setSelected(false);
					deleteButton.setPie(-1.);
					_deleteEnabledTime = -1;
				}
				else
				{
					deleteButton.setPie((double) (System.currentTimeMillis() - _deleteEnabledTime) / _DELETE_DELAY);
				}
			}

		default:
			break;
		}

		return 1;
	}

	public boolean isShown()
	{
		return this == _BUTTONS_SHOWN;
	}

	@Override
	public int life()
	{
		return _isDead ? 0 : 1;
	}
}

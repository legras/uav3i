package com.deev.interaction.uav3i.ui;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class SurfaceObjectMnvr extends Manoeuver
{
	private LatLng _center;
	private double _course;
	
	private enum SurfaceObjectMnvrStates {NONE, TRANSLATE, ROTATE};
	private SurfaceObjectMnvrStates _moveState = SurfaceObjectMnvrStates.NONE;

	private static BufferedImage _imgObjectBig= null;
	private static BufferedImage _imgDataBig= null;
	private static BufferedImage _imgObjectSmall= null;
	private static BufferedImage _imgDataSmall= null;
	
	public SurfaceObjectMnvr(SymbolMap map, LatLng c)
	{
		_center = c;
		_smap = map;
			
		try
		{
			if (_imgObjectBig == null)
			{
				_imgObjectBig = ImageIO.read(this.getClass().getResource("/img/surfo obj big.png"));
				_imgDataBig = ImageIO.read(this.getClass().getResource("/img/surfo data big.png"));
				_imgObjectSmall = ImageIO.read(this.getClass().getResource("/img/surfo obj small.png"));
				_imgDataSmall = ImageIO.read(this.getClass().getResource("/img/surfo data small.png"));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// ********** ManoeuverButtons **********
		try
		{
			_buttons = new ManoeuverButtons(this, MainFrame.clayer);
		}
		catch (IOException e1)
		{
			LoggerUtil.LOG.log(Level.WARNING, "could not create buttons for manoeuver");
			_buttons = null;
		}

		positionButtons();
	}
	
	public double getRadius()
	{
		return isBigOnScreen() ? 256. : 64.;
	}
	
	public boolean isBigOnScreen()
	{
		return false;
	}
	
	@Override
	public float getInterestForPoint(float x, float y)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeTouch(float x, float y, Object touchref)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelTouch(Object touchref)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void paint(Graphics2D g2)
	{
		AffineTransform old = g2.getTransform();

		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		g2.translate(centerPx.x, centerPx.y);
		
		if (isBigOnScreen())
		{
			g2.drawImage(_imgDataBig, 384-_imgDataBig.getWidth(), (int) (-_imgDataBig.getHeight()/2.), null);
			g2.rotate(_course);
			g2.drawImage(_imgObjectBig, (int) (-_imgObjectBig.getWidth()/2.), (int) (-_imgObjectBig.getHeight()/2.), null);
		}
		else
		{
			g2.drawImage(_imgDataSmall, 192-_imgDataSmall.getWidth(), (int) (-_imgDataSmall.getHeight()/2.), null);
			g2.rotate(_course);
			g2.drawImage(_imgObjectSmall, (int) (-_imgObjectSmall.getWidth()/2.), (int) (-_imgObjectSmall.getHeight()/2.), null);
		}

		g2.setTransform(old);
	}

	@Override
	public ManoeuverDTO toDTO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void positionButtons()
	{
		Point2D.Double centerPx = _smap.getScreenForLatLng(_center);
		double Rpx = _smap.getPPM() * getRadius();
		
		if (_buttons != null)
			_buttons.setPositions(centerPx, 40+Rpx, Math.PI/2, true);
	}

	@Override
	public boolean adjustAtPx(double x, double y)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAdjustmentInterestedAtPx(double x, double y)
	{
		// TODO Auto-generated method stub
		return false;
	}

}

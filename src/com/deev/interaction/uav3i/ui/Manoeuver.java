package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;

public abstract class Manoeuver implements Touchable, Animation
{
	protected enum ManoeuverStates {READY, SUBMITTED, REJECTED, FADING};
	protected ManoeuverStates _mnvrState = ManoeuverStates.READY;
	protected ManoeuverButtons _buttons;

	protected SymbolMap _smap;
	
	private static float _ADJUST_INTEREST = 20.f;
	private static float _MOVE_INTEREST = 15.f;
	
	protected boolean _adjusting = false;
	protected static double GRIP = 30.;
	private static TexturePaint _hashGW = null;

	private static Color _GREEN = new Color(.3f, .7f, 0.f, 1.f);
	private static Color _YELLOW = new Color(1.f, 1.f, 0.f, 1.f);
	
	public abstract void paint(Graphics2D g2);
	
	public void paintFootprint(Graphics2D g2, Shape footprint, boolean blink)
	{
		if (_hashGW == null)
		{
			BufferedImage stripes;
			try
			{
				stripes = ImageIO.read(this.getClass().getResource("img/squaresGY.png"));

				_hashGW = new TexturePaint(stripes, new Rectangle2D.Double(0, 0, 32, 32));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		g2.setPaint(_hashGW);
		g2.fill(footprint);
		g2.setStroke(new BasicStroke(3.f));
		g2.setPaint(_GREEN);
		g2.draw(footprint);
	}
	
	public void delete()
	{
		_smap.deleteManoeuver(this);
	}
	
	public void paintAdjustLine(Graphics2D g2, Shape line, boolean blink)
	{
		float phase = blink ? (float) (System.currentTimeMillis() % 200)/10 : 0.f;

		final float dash1[] = {10.0f};
	    final BasicStroke dashed =
	        new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);
	    
	    final BasicStroke plain =
	        new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	    
	    final BasicStroke fat =
	        new BasicStroke((float) GRIP*2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				
		if (_adjusting)
		{
			g2.setStroke(fat);
			g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.5f));
			g2.draw(line);
		}
		
		g2.setPaint(_YELLOW);
	    g2.setStroke(plain);
		g2.draw(line);
		
		g2.setPaint(_GREEN);
	    g2.setStroke(dashed);
		g2.draw(line);
	}
	
	public abstract void positionButtons();
	
	public void hidebuttons()
	{
		if (_buttons != null)
			_buttons.hide();
	}
	
	public abstract boolean adjustAtPx(double x, double y);
	
	public boolean isAdjusting()
	{
		return _adjusting;
	}
	
	public void stopAdjusting()
	{
		_adjusting = false;
	}
	
	public abstract boolean isAdjustmentInterestedAtPx(double x, double y);
	
	/**
	 * @return common value for manoeuvers concerning moves 
	 */
	protected float getMoveInterest()
	{
		if (isSubmitted())
			return -1.f;
		
		if (MainFrame.getAppState() == MainFrameState.COMMAND)
			return _MOVE_INTEREST;
		else
			return -1.f;
	}
	
	/**
	 * @return common value for manoeuvers concerning adjustment 
	 */
	protected float getAdjustInterest()
	{
		if (isSubmitted())
			return -1.f;
		
		if (MainFrame.getAppState() == MainFrameState.COMMAND)
			return _ADJUST_INTEREST;
		else
			return -1.f;
	}
	
	
	protected void didShake()
	{
		System.out.println("THIS WAS SHAKEN!!!!");
	}

	public void addTouch(float x, float y, Object touchref)
	{		
		_buttons.show();
	}

	public void updateTouch(float x, float y, Object touchref)
	{	
		positionButtons();
	}

	public boolean isSubmitted()
	{
		return _buttons.isSubmitted();
	}

	public boolean isModifiable()
	{
		return _buttons.isModifiable();
	}
	
	@Override
	public int tick(int time)
	{		
		return 1;
	}
	
	@Override
	public int life()
	{
		return 1;
	}
}

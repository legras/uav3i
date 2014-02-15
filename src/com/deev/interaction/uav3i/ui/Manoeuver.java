package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Palette;
import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.model.UAVDataPoint;
import com.deev.interaction.uav3i.model.UAVModel;
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
	private long _killTime = -1;
	private boolean _killed = false;
	
	private static long _DEATH_LENGTH = 1000;

	private static Color _GREEN = new Color(.3f, .7f, 0.f, 1.f);
	private static Color _YELLOW = new Color(1.f, 1.f, 0.f, 1.f);
	private static Color _RED = new Color(.9f, .3f, 0.f, 1.f);
	private static Color _M_GREY = new Color(.3f, .3f, .3f, 1.f);
	private static Color _M_WHITE = new Color(1.f, 1.f, 1.f, .2f);
	
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
		
		float lineWidth = isFocusedMnvr() ? 3.f : 1.f;
		
		g2.setPaint(_hashGW);
		g2.fill(footprint);
		g2.setStroke(new BasicStroke(lineWidth));
		g2.setPaint(_GREEN);
		g2.draw(footprint);
	}
	
	public void kill()
	{
		_killTime = _DEATH_LENGTH;
		_killed = true;
		hidebuttons();
	}
	
	public boolean isDying()
	{
		return _killed;
	}
	
	public void delete()
	{
		_smap.deleteManoeuver(this);
		hidebuttons();
	}
	
	public void paintAdjustLine(Graphics2D g2, Shape line, boolean blink, boolean adjust)
	{
		float phase = blink ? (float) (System.currentTimeMillis() % 200)/10 : 0.f;
		float lineWidth = isFocusedMnvr() ? 3.f : 1.f;
		
		final float dash1[] = {10.0f};
	    final BasicStroke dashed =
	        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);
	    
	    final BasicStroke plain =
	        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
	    
	    final BasicStroke fat =
	        new BasicStroke((float) GRIP*2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
				
		if (adjust)
		{
			g2.setStroke(fat);
			g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.5f));
			g2.draw(line);
		}
		
		g2.setPaint(_YELLOW);
	    g2.setStroke(plain);
		g2.draw(line);
		
		if (isDying())
			g2.setPaint(Palette.blendColors(_YELLOW, (int) _killTime, (int) _DEATH_LENGTH, _RED));
		else
			g2.setPaint(_GREEN);
	    g2.setStroke(dashed);
		g2.draw(line);
	}
	
	public void drawLabelledLineAbove(Graphics2D g2, Point2D.Double A, Point2D.Double B, String label, boolean opposite)
	{
		final double headL = 80.;
		final double pp = .9;
		
		double L = A.distance(B);
		Point2D.Double u = new Point2D.Double();
		Point2D.Double v = new Point2D.Double();
		
		u.x = (B.x-A.x)/L;
		u.y = (B.y-A.y)/L;
		
		v.x = -u.y;
		v.y =  u.x;
		
		if (v.y > 0 != opposite)
		{
			v.x *= -1;
			v.y *= -1;
		}
		
		AffineTransform old = g2.getTransform();	// PUSH
		
		g2.translate(A.x,  A.y);
		
		GeneralPath p = new GeneralPath();		
		
		p.moveTo(0, 0);
		p.lineTo(headL*v.x, headL*v.y);
		p.moveTo(headL*v.x*pp, headL*v.y*pp);
		p.lineTo(headL*v.x*pp + L*u.x, headL*v.y*pp + L*u.y);
		p.moveTo(L*u.x, L*u.y);
		p.lineTo(headL*v.x + L*u.x, headL*v.y + L*u.y);
		
		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(p);
		
		g2.setPaint(_M_GREY);
		g2.setStroke(new BasicStroke(.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2.draw(p);

		// Label
		FontRenderContext frc = g2.getFontRenderContext();
	    Font f = new Font("HelveticaNeue-UltraLight", Font.PLAIN, 24);
	    TextLayout textTl;
	    Shape outline;
	    
		textTl = new TextLayout(label, f, frc);
		outline = textTl.getOutline(null);
	    Rectangle2D b = outline.getBounds2D();
	   
	    g2.translate(L*u.x/2+headL*v.x*pp, L*u.y/2+headL*v.y*pp);
	    g2.rotate(Math.atan2(u.y, u.x));
	    g2.translate(-b.getWidth()/2, -4);
	    
		g2.fill(outline);
		
		g2.setTransform(old);						// UNPUSH
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
	
	public boolean isFocusedMnvr()
	{
		return _buttons.isShown();
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
		_killTime -= time;
		
		if (isDying() && _killTime < 0)
		{
			delete();
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int life()
	{
		return 1;
	}
}

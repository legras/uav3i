package com.deev.interaction.uav3i.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.channels.spi.AbstractSelectionKey;

import javax.imageio.ImageIO;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Palette;
import com.deev.interaction.touch.Touchable;
import com.deev.interaction.uav3i.ui.MainFrame.MainFrameState;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public abstract class Manoeuver implements Touchable, Animation
{
	public enum ManoeuverRequestedStatus {NONE, ASKED, REFUSED, ACCEPTED};
	protected ManoeuverRequestedStatus _mnvrReqStatus= ManoeuverRequestedStatus.NONE;
	
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
	
	protected static Color _M_GREY = new Color(.3f, .3f, .3f, 1.f);
	protected static Color _M_WHITE = new Color(1.f, 1.f, 1.f, .4f);
	
	protected int id;
	
	public boolean isPinned()
	{
		return _buttons.isPinned();
	}

	public boolean isSelected()
	{
		return _buttons.isShown();
	}
	
	public boolean isShared()
	{
		return _buttons.isSubmitted();
	}
	
	public void setShared(boolean shared)
	{
		_buttons.submitButton.setSelected(shared);
	}
	
	public void setAsked(boolean asked)
	{
		_buttons.jumpButton.setSelected(asked);
	}
	
	public ManoeuverRequestedStatus getRequestedStatus()
	{
		return _mnvrReqStatus;
	}

	public void setRequestedStatus(ManoeuverRequestedStatus status)
	{
	  _mnvrReqStatus = status;
	}
	
	public void lockShareAndAsk(boolean lock)
	{
		_buttons.submitButton.setEnabled(!lock);
		_buttons.jumpButton.setEnabled(!lock);
	}
	
	public void lockDelete(boolean lock)
	{
		_buttons.deleteButton.setEnabled(!lock);
	}
	
	public abstract void paint(Graphics2D g2);

	public abstract ManoeuverDTO toDTO();

	public void wasModified()
	{
		_mnvrReqStatus = ManoeuverRequestedStatus.NONE;
	}

	public void paintFootprint(Graphics2D g2, Shape footprint, boolean blink)
	{
		float lineWidth = isShared() ? 3.f : 2.f;
		float fatWidth = isSelected() ? 10.f : 4.f;

		Paint paint = Palette3i.MNVR_DEFAULT.getPaint();
		if (_mnvrReqStatus == ManoeuverRequestedStatus.REFUSED)
			paint = Palette3i.MNVR_REFUSED.getPaint();
		if (_mnvrReqStatus == ManoeuverRequestedStatus.ACCEPTED)
			paint = Palette3i.MNVR_ACCEPTED.getPaint();
		

		g2.setStroke(new BasicStroke(lineWidth+2.f*fatWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.setPaint(Palette3i.WHITE_BG.getPaint());
		g2.draw(footprint);
		g2.setPaint(Palette3i.MNVR_FOOT_BGRND.getPaint());
		g2.fill(footprint);
		g2.setStroke(new BasicStroke(lineWidth));
		g2.setPaint(paint);
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
		float lineWidth = isShared() ? 3.f : 2.f;
		float fatWidth = isSelected() ? 10.f : 4.f;

		final float dash1[] = {10.f, 5.f};
		final BasicStroke dashed =
				new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);

		final BasicStroke plain =
				new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

		final BasicStroke fat =
				new BasicStroke(lineWidth+(adjust?4.f:2.f)*fatWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		g2.setStroke(fat);
		g2.setPaint(Palette3i.WHITE_BG.getPaint());
		g2.draw(line);
		
		Paint bgPaint = isPinned() ? Palette3i.MNVR_BACK_PINNED.getPaint() : Palette3i.MNVR_BACK_NOT_PINNED.getPaint();
		
		Paint paint = Palette3i.MNVR_DEFAULT.getPaint();
		if (_mnvrReqStatus == ManoeuverRequestedStatus.REFUSED)
      paint = Palette3i.MNVR_REFUSED.getPaint();
		if (_mnvrReqStatus == ManoeuverRequestedStatus.ACCEPTED)
      paint = Palette3i.MNVR_ACCEPTED.getPaint();

		g2.setPaint(bgPaint);
		g2.setStroke(plain);
		g2.draw(line);

		g2.setPaint(paint);
		g2.setStroke(dashed);
		g2.draw(line);
	}

	public void drawLabelledLine(Graphics2D g2, Point2D.Double A, Point2D.Double B, String label, boolean below)
	{
		if (A.x > B.x)
		{
			drawLabelledLine(g2, B, A, label, below);
			return;
		}

		final double headL = 50.;
		final double pp = 60.;

		double L = A.distance(B);
		Point2D.Double u = new Point2D.Double();
		Point2D.Double v = new Point2D.Double();

		u.x = (B.x-A.x)/L;
		u.y = (B.y-A.y)/L;

		v.x = -u.y;
		v.y =  u.x;

		if (!below)
		{
			v.x *= -1;
			v.y *= -1;
		}

		AffineTransform old = g2.getTransform();	// PUSH

		g2.translate(A.x,  A.y);

		GeneralPath p = new GeneralPath();		

		p.moveTo(0, 0);
		p.lineTo(headL*v.x, headL*v.y);
		p.lineTo(headL*v.x + (pp-headL)*(-u.x+v.x), headL*v.y + (pp-headL)*(-u.y+v.y));

		p.moveTo(pp*v.x, pp*v.y);
		p.lineTo(pp*v.x + L*u.x, pp*v.y + L*u.y);

		p.moveTo(L*u.x, L*u.y);
		p.lineTo(headL*v.x + L*u.x, headL*v.y + L*u.y);
		p.lineTo(headL*v.x + L*u.x + (pp-headL)*(u.x+v.x), headL*v.y + L*u.y + (pp-headL)*(u.y+v.y));

		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(p);

		g2.setPaint(_M_GREY);
		g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		g2.draw(p);

		// Label
		FontRenderContext frc = g2.getFontRenderContext();
		Font f = new Font("Futura", Font.PLAIN, 20);
		TextLayout textTl;
		Shape outline;

		textTl = new TextLayout(label, f, frc);
		outline = textTl.getOutline(null);
		Rectangle2D b = outline.getBounds2D();

		g2.translate(L*u.x/2+v.x*pp, L*u.y/2+v.y*pp);
		g2.rotate(Math.atan2(u.y, u.x));
		g2.translate(-b.getWidth()/2, -4);

		g2.setPaint(_M_WHITE);
		g2.setStroke(new BasicStroke(5.f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL));
		g2.draw(outline);

		g2.setPaint(_M_GREY);
		g2.fill(outline);

		g2.setTransform(old);						// UNPUSH
	}

	public abstract void positionButtons();

	public void instaMoveButtons()
	{
		positionButtons();
		_buttons.setBounds();
	}
	
	public void hidebuttons()
	{
		if (_buttons != null)
			_buttons.hide();
	}

	public abstract boolean adjustAtPx(double x, double y);


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
		if (getRequestedStatus() != ManoeuverRequestedStatus.ASKED &&
			MainFrame.getAppState() != MainFrameState.REPLAY)
			return _MOVE_INTEREST;
		else
			return -1.f;
	}

	/**
	 * @return common value for manoeuvers concerning adjustment 
	 */
	protected float getAdjustInterest()
	{
		if (getRequestedStatus() != ManoeuverRequestedStatus.ASKED &&
			MainFrame.getAppState() != MainFrameState.REPLAY)
			return _ADJUST_INTEREST;
		else
			return -1.f;
	}

	public void addTouch(float x, float y, Object touchref)
	{		
		_buttons.show();
		setRequestedStatus(ManoeuverRequestedStatus.NONE);
	}

	public void updateTouch(float x, float y, Object touchref)
	{	
		positionButtons();
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

  /**
   * @return the number
   */
  public int getId()
  {
    return id;
  }

  /**
   * @param number the number to set
   */
  public void setId(int id)
  {
    this.id = id;
  }
}

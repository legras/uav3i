package com.deev.interaction.uav3i.veto.communication.dto;

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
import java.io.IOException;
import java.io.Serializable;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.Palette3i;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.VetoManoeuverButtons;

public abstract class ManoeuverDTO implements Serializable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -3496424950260659940L;
  
  protected VetoManoeuverButtons buttons;
  
  protected static double RPX = 10.;
  protected static double GRIP = 30.;
  
  private static Color        _GREEN   = new Color(.3f, .7f, 0.f, 1.f);
  private static Color        _YELLOW  = new Color(1.f, 1.f, 0.f, 1.f);
  private static Color        _RED     = new Color(.9f, .3f, 0.f, 1.f);
  private static Color        _M_GREY  = new Color(.3f, .3f, .3f, 1.f);
  private static Color        _M_WHITE = new Color(1.f, 1.f, 1.f, .4f);
  private static TexturePaint _hashGW  = null;
  
  protected int id;
  //-----------------------------------------------------------------------------
  public abstract void paint(Graphics2D g2);
  //-----------------------------------------------------------------------------
  public abstract LatLng getCenter();
  //-----------------------------------------------------------------------------
  public void addButtons()
  {
    try
    {
      buttons = new VetoManoeuverButtons(this, Veto.getComponentLayer());
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
      buttons = null;
    }
    positionButtons();
  }
  //-----------------------------------------------------------------------------
  public void hidebuttons()
  {
    buttons.hide();
    buttons = null;
  }
  //-----------------------------------------------------------------------------
  public VetoManoeuverButtons getButtons()
  {
    return buttons;
  }
  //-----------------------------------------------------------------------------
  public abstract void positionButtons();
  //-----------------------------------------------------------------------------
  /**
   * Dessin de la ligne d'ajoustement : choix NS dans la box, éloignement de
   * la trajectoire dans la ligne ou cercle.
   * 
   * @param g2
   * @param line
   */
  public void paintAdjustLine(Graphics2D g2, Shape line)
  {
    //float phase = (float) (System.currentTimeMillis() % 200)/10;
    float phase = 0.f;
    float lineWidth = 3.f;
    float fatWidth = 4.f;

    final float dash1[] = {10.f, 5.f};
    final BasicStroke dashed =
        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);

    final BasicStroke plain =
        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

    final BasicStroke fat =
        new BasicStroke(lineWidth+4.f*fatWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    g2.setStroke(fat);
    g2.setPaint(Palette3i.WHITE_BG.getPaint());
    g2.draw(line);
    
    Paint bgPaint = Palette3i.MNVR_BACK_NOT_PINNED.getPaint();
    
    Paint paint = Palette3i.MNVR_DEFAULT.getPaint();
//    if (_mnvrReqStatus == ManoeuverRequestedStatus.REFUSED)
//      paint = Palette3i.MNVR_REFUSED.getPaint();
//    if (_mnvrReqStatus == ManoeuverRequestedStatus.ACCEPTED)
//      paint = Palette3i.MNVR_ACCEPTED.getPaint();

    g2.setPaint(bgPaint);
    g2.setStroke(plain);
    g2.draw(line);

    g2.setPaint(paint);
    g2.setStroke(dashed);
    g2.draw(line);
  }
  //-----------------------------------------------------------------------------
  /**
   * Dessin de l'emprise (zone à observer) de la manoeuvre.
   * 
   * @param g2
   * @param footprint
   */
  public void paintFootprint(Graphics2D g2, Shape footprint)
  {
//    float lineWidth = isShared() ? 3.f : 2.f;
//    float fatWidth = isSelected() ? 10.f : 4.f;
    float lineWidth = 3.f;
    float fatWidth = 10.f;

//    Paint paint = Palette3i.MNVR_DEFAULT.getPaint();
//    if (_mnvrReqStatus == ManoeuverRequestedStatus.REFUSED)
//      paint = Palette3i.MNVR_REFUSED.getPaint();
//    if (_mnvrReqStatus == ManoeuverRequestedStatus.ACCEPTED)
//      paint = Palette3i.MNVR_ACCEPTED.getPaint();
    Paint paint = Palette3i.MNVR_DEFAULT.getPaint();
    

    g2.setStroke(new BasicStroke(lineWidth+2.f*fatWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2.setPaint(Palette3i.WHITE_BG.getPaint());
    g2.draw(footprint);
    g2.setPaint(Palette3i.MNVR_FOOT_BGRND.getPaint());
    g2.fill(footprint);
    g2.setStroke(new BasicStroke(lineWidth));
    g2.setPaint(paint);
    g2.draw(footprint);
  }
  //-----------------------------------------------------------------------------
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

    AffineTransform old = g2.getTransform();  // PUSH

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
    Font f = new Font("HelveticaNeue-UltraLight", Font.PLAIN, 24);
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

    g2.setTransform(old);           // UNPUSH
  }
  //-----------------------------------------------------------------------------
//  public void paintAdjustLine(Graphics2D g2, Shape line, boolean blink, boolean adjust)
//  {
//    float phase = blink ? (float) (System.currentTimeMillis() % 200)/10 : 0.f;
//    //float lineWidth = isSelected() ? 3.f : 1.f;
//    float lineWidth = 3.f;
//
//    final float dash1[] = {10.0f};
//    final BasicStroke dashed =
//        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);
//
//    final BasicStroke plain =
//        new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
//
//    final BasicStroke fat =
//        new BasicStroke((float) GRIP*2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
//
//    if (adjust)
//    {
//      g2.setStroke(fat);
//      g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.5f));
//      g2.draw(line);
//    }
//
//    g2.setPaint(_YELLOW);
//    g2.setStroke(plain);
//    g2.draw(line);
//
////    if (isDying())
////      g2.setPaint(Palette.blendColors(_YELLOW, (int) _killTime, (int) _DEATH_LENGTH, _RED));
////    else
//      g2.setPaint(_GREEN);
//    g2.setStroke(dashed);
//    g2.draw(line);
//  }
  //-----------------------------------------------------------------------------
  /**
   * @return the id
   */
  public int getId() { return id; }
  /**
   * @param id the id to set
   */
  public void setId(int id) { this.id = id; }
  //-----------------------------------------------------------------------------
}

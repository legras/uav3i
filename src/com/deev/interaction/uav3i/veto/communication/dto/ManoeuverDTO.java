package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.Serializable;

import com.deev.interaction.touch.Palette;

public abstract class ManoeuverDTO implements Serializable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -3496424950260659940L;
  
  protected static double RPX = 10.;
  
  private static Color _GREEN   = new Color(.3f, .7f, 0.f, 1.f);
  private static Color _YELLOW  = new Color(1.f, 1.f, 0.f, 1.f);
  private static Color _RED     = new Color(.9f, .3f, 0.f, 1.f);
  private static Color _M_GREY  = new Color(.3f, .3f, .3f, 1.f);
  private static Color _M_WHITE = new Color(1.f, 1.f, 1.f, .4f);

  //-----------------------------------------------------------------------------
  public abstract void paint(Graphics2D g2);
  //-----------------------------------------------------------------------------
  public void paintAdjustLine(Graphics2D g2, Shape line)
  {
    //float phase = blink ? (float) (System.currentTimeMillis() % 200)/10 : 0.f;
    float phase = (float) (System.currentTimeMillis() % 200)/10;
    float lineWidth = 3.f;
    
    final float dash1[] = {10.0f};
    final BasicStroke dashed = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, phase);
    final BasicStroke plain  = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
    //final BasicStroke fat    = new BasicStroke((float) GRIP*2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        
//    if (adjust)
//    {
//      g2.setStroke(fat);
//      g2.setPaint(new Color(1.0f, 1.0f, 1.0f, 0.5f));
//      g2.draw(line);
//    }
    
    g2.setPaint(_YELLOW);
      g2.setStroke(plain);
    g2.draw(line);
    
//    if (isDying())
//      g2.setPaint(Palette.blendColors(_YELLOW, (int) _killTime, (int) _DEATH_LENGTH, _RED));
//    else
      g2.setPaint(_GREEN);
      g2.setStroke(dashed);
    g2.draw(line);
  }
  //-----------------------------------------------------------------------------
}

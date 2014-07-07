package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.ui.Veto;

import uk.me.jstott.jcoord.LatLng;

public class CircleMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 8781703792280948005L;
  //-----------------------------------------------------------------------------
  private LatLng _center;
  private double _currentRm = 500.;
  //-----------------------------------------------------------------------------
  public CircleMnvrDTO(int id, LatLng center, double currentRm)
  {
    this.id    = id;
    _center    = center;
    _currentRm = currentRm;
  }
  //-----------------------------------------------------------------------------
  public LatLng getCenter()    { return _center;    }
  public double getCurrentRm() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public void paint(Graphics2D g2)
  {
    AffineTransform old = g2.getTransform();

    Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
    g2.translate(centerPx.x, centerPx.y);
    Ellipse2D.Double ell = new Ellipse2D.Double(-RPX, -RPX, 2*RPX, 2*RPX);

    paintFootprint(g2, ell);

    
    double Rpx = Veto.getSymbolMapVeto().getPPM() * _currentRm;

    ell = new Ellipse2D.Double(-Rpx, -Rpx, 2*Rpx, 2*Rpx);

    paintAdjustLine(g2, ell);

    String largS = Math.round(Math.abs(_currentRm))+" m";
    Point2D.Double zero = new Point2D.Double();
    Point2D.Double ll = new Point2D.Double(-Rpx*Math.cos(Math.PI/6), Rpx*Math.sin(Math.PI/6));
    drawLabelledLine(g2, zero, ll, largS, false);

//    if (isSelected())
//    {
//      String largS = Math.round(Math.abs(_currentRm))+" m";
//      Point2D.Double zero = new Point2D.Double();
//      Point2D.Double ll = new Point2D.Double(-Rpx*Math.cos(Math.PI/6), Rpx*Math.sin(Math.PI/6));
//      drawLabelledLine(g2, zero, ll, largS, false);
//    }

    g2.setTransform(old);

  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "CircleMnvrDTO [id=" + id + ", _center=" + _center + ", _currentRm=" + _currentRm + "]";
  }
  //-----------------------------------------------------------------------------
}

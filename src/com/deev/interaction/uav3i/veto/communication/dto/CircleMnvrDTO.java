package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.veto.ui.Veto;

public class CircleMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 8781703792280948005L;
  //-----------------------------------------------------------------------------
  private LatLng _center;
//  private double _currentRm = 500.;
  private double _currentRm = 30.;
  private Point2D.Double oldCenterPx = new Point2D.Double(-1,-1);
  private double oldRpx = -1;
  //-----------------------------------------------------------------------------
  public CircleMnvrDTO(int id, LatLng center, double currentRm)
  {
    this.id    = id;
    _center    = center;
    _currentRm = currentRm;
  }
  //-----------------------------------------------------------------------------
  @Override
  public LatLng getCenter()        { return _center;    }
  public double getCurrentRadius() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public void positionButtons()
  {
    Point2D.Double centerPx = Veto.getSymbolMapVeto().getScreenForLatLng(_center);
    double rpx = Veto.getSymbolMapVeto().getPPM() * _currentRm;
    
    if (buttons != null)
    {
      // Est-ce que ça vaut le coup de recalculer la position des boutons ?
      if(!centerPx.equals(oldCenterPx) && oldRpx != rpx)
      {
        oldCenterPx = centerPx;
        oldRpx = rpx;
        buttons.setPositions(centerPx, 40+rpx, Math.PI/2, true);
      }
    }
  }
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

    paintAdjustLine(g2, ell, mnvrReqStatus == ManoeuverRequestedStatus.ASKED);

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

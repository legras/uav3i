package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.deev.interaction.uav3i.veto.ui.Veto;

import uk.me.jstott.jcoord.LatLng;

public class LineMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -3630611531901790503L;
  // Points de la zone à regarder
  private LatLng _A, _B;
  // Distance entre la zone à regarder et la trajectoire
  private double _currentRm = 500.;
  // Codage de l'orientation de la droite dans le plan ?
  private Point2D.Double _u, _v;
  //-----------------------------------------------------------------------------
  public LineMnvrDTO(LatLng A, LatLng B,
                     double currentRm,
                     Point2D.Double u, Point2D.Double v)
  {
    _A         = A;
    _B         = B;
    _currentRm = currentRm;
    _u         = u;
    _v         = v;
  }
  //-----------------------------------------------------------------------------
  public LatLng getA()         { return _A;         }
  public LatLng getB()         { return _B;         }
  public double getCurrentRm() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public void paint(Graphics2D g2)
  {
    AffineTransform old = g2.getTransform();

    Point2D.Double Apx = Veto.getSymbolMapVeto().getScreenForLatLng(_A);
    Point2D.Double Bpx = Veto.getSymbolMapVeto().getScreenForLatLng(_B);

    Area area = new Area();
    BasicStroke stroke = new BasicStroke((float) RPX*2.f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);

    // Zone à regarder
    GeneralPath line;
    line = new GeneralPath();
    line.moveTo(Apx.x, Apx.y);
    line.lineTo(Bpx.x, Bpx.y);

    area.add(new Area(stroke.createStrokedShape(line)));

    //paintFootprint(g2, area, isSubmitted());

    double Rpx = Veto.getSymbolMapVeto().getPPM() * _currentRm;

    Point2D.Double LApx = new Point2D.Double(Apx.x + _v.x * Rpx, Apx.y + _v.y * Rpx);

    Point2D.Double LBpx = new Point2D.Double(Bpx.x + _v.x * Rpx, Bpx.y + _v.y * Rpx);

    // Trajectoire du drone
    Line2D.Double l = new Line2D.Double(LApx, LBpx);
    //paintAdjustLine(g2, l, isSubmitted(), _adjusting);
    paintAdjustLine(g2, l);
    
//    if (isFocusedMnvr())
//    {
//      String distS = Math.round(Apx.distance(Bpx)/_smap.getPPM())+" m";
//      drawLabelledLine(g2, LApx, LBpx, distS, LApx.y > Apx.y);
//      
//      String largS = Math.round(Math.abs(_currentRm))+" m";
//      if (LApx.y < LBpx.y)
//        drawLabelledLine(g2, Apx, LApx, largS, false);
//      else
//        drawLabelledLine(g2, Bpx, LBpx, largS, false);
//    }

    g2.setTransform(old);
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "LineMnvrDTO [_A=" + _A + ", _B=" + _B + ", _currentRm=" + _currentRm + "]";
  }
  //-----------------------------------------------------------------------------
}

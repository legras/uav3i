package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.IOException;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.ui.ManoeuverButtons;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.VetoManoeuverButtons;

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
  public LineMnvrDTO(int id,
                     LatLng A, LatLng B,
                     double currentRm,
                     Point2D.Double u, Point2D.Double v)
  {
    this.id   = id;
    _A         = A;
    _B         = B;
    _currentRm = currentRm;
    _u         = u;
    _v         = v;
    
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
  public LatLng get_A()        { return _A;                 }
  public LatLng getTrajA()     { return getOffsetPoint(_A); }
  public LatLng get_B()        { return _B;                 }
  public LatLng getTrajB()     { return getOffsetPoint(_B); }
  public double getCurrentRm() { return _currentRm;         }
  //-----------------------------------------------------------------------------
  @Override
  public LatLng getCenter()
  {
    double latitude  = (_A.getLat() + _B.getLat()) / 2;
    double longitude = (_A.getLng() + _B.getLng()) / 2;
    return new LatLng(latitude, longitude);
  }
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

    paintFootprint(g2, area);

    double Rpx = Veto.getSymbolMapVeto().getPPM() * _currentRm;

    Point2D.Double LApx = new Point2D.Double(Apx.x + _v.x * Rpx, Apx.y + _v.y * Rpx);

    Point2D.Double LBpx = new Point2D.Double(Bpx.x + _v.x * Rpx, Bpx.y + _v.y * Rpx);

    // Trajectoire du drone
    Line2D.Double l = new Line2D.Double(LApx, LBpx);
    //paintAdjustLine(g2, l, isSubmitted(), _adjusting);
    paintAdjustLine(g2, l);

    String distS = Math.round(Apx.distance(Bpx)/Veto.getSymbolMapVeto().getPPM())+" m";
    drawLabelledLine(g2, LApx, LBpx, distS, LApx.y > Apx.y);
    
    String largS = Math.round(Math.abs(_currentRm))+" m";
    if (LApx.y < LBpx.y)
      drawLabelledLine(g2, Apx, LApx, largS, false);
    else
      drawLabelledLine(g2, Bpx, LBpx, largS, false);

    
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
  private LatLng getOffsetPoint(LatLng pointLatLng)
  {
    Point2D.Double pointPixels = Veto.getSymbolMapVeto().getScreenForLatLng(pointLatLng);
    double rpx = Veto.getSymbolMapVeto().getPPM() * _currentRm;
    Point2D.Double pointOffsetPixels = new Point2D.Double(pointPixels.x + _v.x * rpx,
                                                          pointPixels.y + _v.y * rpx);
    return Veto.getSymbolMapVeto().getLatLngForScreen(pointOffsetPixels.x, pointOffsetPixels.y);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void positionButtons()
  {
    Point2D.Double Apx = Veto.getSymbolMapVeto().getScreenForLatLng(_A);
    Point2D.Double Bpx = Veto.getSymbolMapVeto().getScreenForLatLng(_B);
    double side = _currentRm > 0 ? 1 : 0;

    if (buttons != null)
      buttons.setPositions(new Point2D.Double((Apx.x+Bpx.x)/2, (Apx.y+Bpx.y)/2),
                           40+RPX,
                           Math.atan2(_v.y, _v.x) + side*Math.PI,
                           false);
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "LineMnvrDTO [id=" + id + ", _A=" + _A + ", _B=" + _B + ", _currentRm=" + _currentRm + ", _u=" + _u + ", _v=" + _v + "]";
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_A == null) ? 0 : _A.hashCode());
    result = prime * result + ((_B == null) ? 0 : _B.hashCode());
    return result;
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)                   return true;
    if (obj == null)                   return false;
    if (!(obj instanceof LineMnvrDTO)) return false;

    LineMnvrDTO other = (LineMnvrDTO) obj;
    
    if (_A == null)
    {
      if (other._A != null)
        return false;
    }
    
    // equals(...) is not defined inside the LatLng class so the equality
    // between values for latitude ang longitude are tested.
    
    //else if (!_A.equals(other._A))
    else if(!(_A.getLat() == other._A.getLat() && _A.getLng() == _A.getLng()))  
      return false;
    
    if (_B == null)
    {
      if (other._B != null)
        return false;
    }
    //else if (!_B.equals(other._B))
    else if (!(_B.getLat() == other._B.getLat() && _B.getLng() == _B.getLng()))
      return false;
    
    return true;
  }
  //-----------------------------------------------------------------------------
}

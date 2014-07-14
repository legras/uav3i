package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.deev.interaction.uav3i.veto.ui.Veto;

import uk.me.jstott.jcoord.LatLng;

public class BoxMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -4968937130195723726L;
  // Points de la zone à regarder
  private LatLng _A, _B;
  private boolean _isNorthSouth = true; // else is East-West
  
  private enum BoxMnvrHandles
  {
    NORTH(.5, .0), 
    EAST(1., .5), 
    SOUTH(.5, 1.), 
    WEST(.0, .5);
    
    double _x, _y;
    
    BoxMnvrHandles(double x, double y)
    {
      _x = x;
      _y = y;
    }
    
    public double distance(double x, double y, BoxMnvrDTO bmnvr)
    {
      Rectangle2D.Double box = bmnvr.getBoxOnScreen();
      Point2D.Double h = new Point2D.Double(box.x + _x*box.width, box.y + _y*box.height);
      
      return h.distance(x, y);
    }
    
    public BoxMnvrHandles getOpposite()
    {
      switch (this)
      {
        case NORTH: return SOUTH;
        case EAST: return WEST;
        case SOUTH: return NORTH;
        case WEST:
        default:
          return EAST;
      }
    }
    
    public boolean isNorthsouth()
    {
      switch (this)
      {
        case NORTH: 
        case SOUTH: return true;
        case EAST: 
        case WEST:
        default:
          return false;
      }
    }
    
    public GeneralPath getPath(double length, BoxMnvrDTO bmnvr)
    {
      final double step = 30;
      
      if (length < 10) length = 10;
      
      GeneralPath p = new GeneralPath();
      Rectangle2D.Double box = bmnvr.getBoxOnScreen();
      
      switch (this)
      {
        case NORTH:
          p.append(new Arc2D.Double(box.x+box.width/2, box.y-step/2, step, step, 0, 180, Arc2D.OPEN), false);
          p.lineTo(box.x+box.width/2, box.y+length);
          break;
        case EAST:
          p.append(new Arc2D.Double(box.x+box.width-step/2, box.y+box.height/2, step, step, -90, 180, Arc2D.OPEN), false);
          p.lineTo(box.x+box.width-length, box.y+box.height/2);
          break;
        case SOUTH:
          p.moveTo(box.x+box.width/2, box.y+box.height-length);
          p.append(new Arc2D.Double(box.x+box.width/2-step, box.y+box.height-step/2, step, step, 0, -180, Arc2D.OPEN), true);
          break;
        case WEST:
          p.moveTo(box.x+length, box.y+box.height/2);
          p.append(new Arc2D.Double(box.x-step/2, box.y+box.height/2-step, step, step, -90, -180, Arc2D.OPEN), true);
          break;
      }
      
      return p;
    }
  };

  //-----------------------------------------------------------------------------
  public BoxMnvrDTO(int id, LatLng A, LatLng B, boolean isNorthSouth)
  {
    this.id       = id;
    _A            = A;
    _B            = B;
    _isNorthSouth = isNorthSouth;
  }
  //-----------------------------------------------------------------------------
  public LatLng  getBoxA()      { return _A;            }
  public LatLng  getBoxB()      { return _B;            }
  public boolean isNorthSouth() { return _isNorthSouth; }
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
    Rectangle2D.Double box = getBoxOnScreen();
    paintFootprint(g2, box);

    for (BoxMnvrHandles boxhndl : BoxMnvrHandles.values())
    {
      boolean fat = true;
      double al = 10;
      double length = 0;
      double max = _isNorthSouth ? box.height : box.width;

        if (_isNorthSouth && boxhndl.isNorthsouth())
          length = max/2;

        if (!_isNorthSouth && !boxhndl.isNorthsouth())
          length = max/2;
//      }
      
        //paintAdjustLine(g2, boxhndl.getPath(length, this), isShared(), fat);
        paintAdjustLine(g2, boxhndl.getPath(length, this));
    }
    
    String widthS = Math.round(box.width/Veto.getSymbolMapVeto().getPPM())+" m";
    String heightS = Math.round(box.height/Veto.getSymbolMapVeto().getPPM())+" m";
    Point2D.Double TL = new Point2D.Double(box.x, box.y);
    Point2D.Double TR = new Point2D.Double(box.x+box.width, box.y);
    Point2D.Double BL = new Point2D.Double(box.x, box.y+box.height);
    drawLabelledLine(g2, TL, TR, widthS, false);
    drawLabelledLine(g2, BL, TL, heightS, false);

    
//    if (isSelected())
//    {
//      String widthS = Math.round(box.width/_smap.getPPM())+" m";
//      String heightS = Math.round(box.height/_smap.getPPM())+" m";
//      Point2D.Double TL = new Point2D.Double(box.x, box.y);
//      Point2D.Double TR = new Point2D.Double(box.x+box.width, box.y);
//      Point2D.Double BL = new Point2D.Double(box.x, box.y+box.height);
//      drawLabelledLine(g2, TL, TR, widthS, false);
//      drawLabelledLine(g2, BL, TL, heightS, false);
//    }
    
    g2.setTransform(old);
  }
  //-----------------------------------------------------------------------------
  private Rectangle2D.Double getBoxOnScreen()
  {
    Point2D.Double Apx = Veto.getSymbolMapVeto().getScreenForLatLng(_A);
    Point2D.Double Bpx = Veto.getSymbolMapVeto().getScreenForLatLng(_B);
    
    Rectangle2D.Double box = new Rectangle2D.Double(Apx.x, Apx.y, 0, 0);
    box.add(Bpx);
    
    return box;
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "BoxMnvrDTO [id=" + id + ", _A=" + _A + ", _B=" + _B + ", _isNorthSouth=" + _isNorthSouth + "]";
  }
  //-----------------------------------------------------------------------------
}

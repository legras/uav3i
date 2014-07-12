package com.deev.interaction.uav3i.veto.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;

public class UAVScope implements MapMarker
{
  //-----------------------------------------------------------------------------
  private int    maxDistanceFromHome;
  private LatLng startPoint;
  //-----------------------------------------------------------------------------
  public UAVScope()
  {
    this.startPoint          = FlightPlanFacade.getInstance().getStartPoint();
    this.maxDistanceFromHome = FlightPlanFacade.getInstance().getMaxDistanceFromHome();
  }
  //-----------------------------------------------------------------------------
  @Override
  public Color getBackColor()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Color getColor()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Font getFont()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Layer getLayer()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public String getName()
  {
    return "UAVScope";
    //return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Stroke getStroke()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Style getStyle()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public Style getStyleAssigned()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public boolean isVisible()
  {
    return true;
    //return false;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void setLayer(Layer layer)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public void setLat(double lat)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public void setLon(double lon)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public Coordinate getCoordinate()
  {
    return new Coordinate(startPoint.getLat(), startPoint.getLng());
  }
  //-----------------------------------------------------------------------------
  @Override
  public double getLat()
  {
    return startPoint.getLat();
  }
  //-----------------------------------------------------------------------------
  @Override
  public double getLon()
  {
    return startPoint.getLng();
  }
  //-----------------------------------------------------------------------------
  @Override
  public STYLE getMarkerStyle()
  {
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public double getRadius()
  {
    return 0;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void paint(Graphics g, Point position, int radio)
  {
    //System.out.println("####### paint(g, "+position+", "+radio+") : zoom = " + Veto.getMapViewer().getZoom());
    Graphics2D g2 = (Graphics2D) g;
    
    g2.setColor(Color.red);

    // Dessin du centre
    g2.fillOval(position.x-5, position.y-5, 10, 10);

    // Dessin du cercle
    int rayonPixels = (int) (maxDistanceFromHome / Veto.getMapViewer().getMeterPerPixel());
    //int rayonPixels = (int) (maxDistanceFromHome / Veto2.getMapViewer().getMeterPerPixel());
    int centerX = position.x - (rayonPixels);
    int centerY = position.y - (rayonPixels);
    g2.drawOval(centerX, centerY, rayonPixels*2, rayonPixels*2);
    
  }
  //-----------------------------------------------------------------------------
}

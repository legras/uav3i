package com.deev.interaction.uav3i.veto.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVDataPoint;
import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.ui.Trajectory;

public class SymbolMapVeto extends JComponent
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 8553028045589171104L;
  
  private Trajectory    trajectory;
  private long          lastTrajectoryUpdate = 0;
  private BufferedImage uavImage = null;

  //-----------------------------------------------------------------------------
  public SymbolMapVeto()
  {
    super();
    setVisible(true);
    setOpaque(false);
    Color back = new Color(0.f, 0.f, 0.f, .0f);
    setBackground(back);  
    trajectory = new Trajectory();
    try
    {
      uavImage = ImageIO.read(this.getClass().getResource("/com/deev/interaction/uav3i/ui/img/uav.png"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

  }
  //-----------------------------------------------------------------------------
  public Point2D.Double getScreenForLatLng(LatLng latlng)
  { 
    Point a = Veto.osmMapGround.getMapViewer().getMapPosition(latlng.getLat(), latlng.getLng(), false);
    Point b = new Point(a.x+1, a.y+1);
    
    LatLng A = getLatLngForScreen(a.x, a.y);
    LatLng B = getLatLngForScreen(b.x, b.y);
    
    double X = (double) a.x + (latlng.getLng()-A.getLng()) / (B.getLng()-A.getLng());
    double Y = (double) a.y + (latlng.getLat()-A.getLat()) / (B.getLat()-A.getLat());
    
    return new Point2D.Double(X, Y);
  }
  //-----------------------------------------------------------------------------
  public LatLng getLatLngForScreen(double x, double y)
  {   
    int X = (int) x;
    int Y = (int) y;

    Coordinate A = Veto.osmMapGround.getMapViewer().getPosition(X, Y);
    Coordinate B = Veto.osmMapGround.getMapViewer().getPosition(X+1, Y+1);

    double lat = A.getLat() + (y-(double)Y) * (B.getLat()-A.getLat());
    double lon = A.getLon() + (x-(double)X) * (B.getLon()-A.getLon());
    
    return new LatLng(lat, lon);
  }
  //-----------------------------------------------------------------------------
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    paint(g2);
  }
  //-----------------------------------------------------------------------------
  public synchronized void paint(Graphics2D g2)
  {
    long currentTime = System.currentTimeMillis();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    g2.drawString("SymbolMapVeto", 50, 50);

    // Update de trajectoire
    if (currentTime - lastTrajectoryUpdate > 500)
    {
      trajectory.update();
      lastTrajectoryUpdate = currentTime;
    }

    // Tracé de trajectoire
    GeneralPath fullTrajectory = trajectory.getFullPath(this);
    
    if (fullTrajectory != null)
    {
      g2.setPaint(new Color(1.f, 1.f, 1.f, .5f));
      // g2.setPaint(new Color(0.f, 0.f, 0.f, .3f));
      g2.setStroke(new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
      g2.draw(fullTrajectory);
      g2.setPaint(Color.RED);
      g2.setStroke(new BasicStroke(1.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
      g2.draw(fullTrajectory);
    }
    
    // Dessin UAV
    AffineTransform old = g2.getTransform();  
    BufferedImage uavImg;
    UAVDataPoint uavpoint;

    old = g2.getTransform();

      uavImg = uavImage;
      
      uavpoint = UAVModel.getDataPointAtTime(System.currentTimeMillis());
      if (uavpoint != null)
      {
        Point2D.Double uav = getScreenForLatLng(uavpoint.latlng);
        double course = Math.PI/2. - uavpoint.course/180.*Math.PI;
        g2.translate(uav.x, uav.y);

        g2.rotate(Math.PI/2.-course);
        g2.drawImage(uavImg, -uavImg.getWidth()/2, -uavImg.getHeight()/2, null);

      }
      g2.setTransform(old);

  }
  //-----------------------------------------------------------------------------
}

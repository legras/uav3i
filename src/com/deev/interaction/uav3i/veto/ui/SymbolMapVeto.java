package com.deev.interaction.uav3i.veto.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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
import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.ui.Palette3i;
import com.deev.interaction.uav3i.ui.Trajectory;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class SymbolMapVeto extends JComponent
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 8553028045589171104L;
  
  private   Trajectory    trajectory;
  private   long          lastTrajectoryUpdate = 0;
  private   BufferedImage uavImage             = null;
  protected BufferedImage waypointImage        = null;
  private   ManoeuverDTO  sharedManoeuver      = null;

	private LatLng _startPoint;
	private int _maxDistanceFromHome;
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
      uavImage      = ImageIO.read(this.getClass().getResource("/img/uav.png"));
      waypointImage = ImageIO.read(this.getClass().getResource("/img/waypoint.png"));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
	
    _startPoint          = FlightPlanFacade.getInstance().getStartPoint();
    _maxDistanceFromHome = FlightPlanFacade.getInstance().getMaxDistanceFromHome();
  }
  //-----------------------------------------------------------------------------
  public Point2D.Double getScreenForLatLng(LatLng latlng)
  { 
    Point a = Veto.getMapViewer().getMapPosition(latlng.getLat(), latlng.getLng(), false);
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

    Coordinate A = Veto.getMapViewer().getPosition(X, Y);
    Coordinate B = Veto.getMapViewer().getPosition(X+1, Y+1);

    double lat = A.getLat() + (y-(double)Y) * (B.getLat()-A.getLat());
    double lon = A.getLon() + (x-(double)X) * (B.getLon()-A.getLon());
    
    return new LatLng(lat, lon);
  }
  //-----------------------------------------------------------------------------
  public double getPPM()
  {
    return 1. / Veto.getMapViewer().getMeterPerPixel();
  }
  //-----------------------------------------------------------------------------
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    paint(g2);
  }
  //-----------------------------------------------------------------------------
  public void reinit()
  {
    trajectory.reinit();
    sharedManoeuver = null;
  }
  //-----------------------------------------------------------------------------
  public void addManoeuver(ManoeuverDTO manoeuverDTO)
  {
    sharedManoeuver = manoeuverDTO;
  }
  //-----------------------------------------------------------------------------
  public ManoeuverDTO getSharedManoeuver()
  {
    return sharedManoeuver;
  }
  //-----------------------------------------------------------------------------
  public void clearManoeuver()
  {
    sharedManoeuver = null;
  }
  //-----------------------------------------------------------------------------
  public synchronized void paint(Graphics2D g2)
  {
    long currentTime = System.currentTimeMillis();

    // TODO : pas hyper top... pas ce qui est le plus optimisé !
    if(sharedManoeuver != null)
    {
      sharedManoeuver.positionButtons();
      if(sharedManoeuver.getButtons() != null)
        sharedManoeuver.getButtons().setBounds();
    }

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    
    // Zone d'opérations
 	float radius = (float) (_maxDistanceFromHome * getPPM());
 	float THICK = 40.f;
	
	Point2D.Double center = getScreenForLatLng(_startPoint);
	float[] dist = {0.f, radius/(radius+THICK), radius/(radius+THICK)+.000001f, 1.f};
	Color[] colors = {
			(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR),
			(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR),
			(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE),
			(Color) Palette3i.getPaint(Palette3i.UAV_SCOPE_CLEAR)};
	RadialGradientPaint gradPaint = new RadialGradientPaint(center, radius + THICK, dist, colors);

	g2.setPaint(gradPaint);
	g2.fill(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    
    // WayPoints
    FontRenderContext frc = g2.getFontRenderContext();
    Font f = new Font("Futura", Font.PLAIN, 12);
    g2.setStroke(new BasicStroke(3.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
    TextLayout textTl;
    Shape outline;
    synchronized (this)
    {
      for (UAVWayPoint wayPoint : UAVModel.getWayPoints().getWayPoints())
      {
        LatLng wayPointPosition = wayPoint.getWayPointPosition();
        if (wayPointPosition != null)
        {
          Point p = Veto.getMapViewer().getMapPosition(wayPointPosition.getLat(), wayPointPosition.getLng(), false);
          g2.drawImage(waypointImage, p.x - waypointImage.getWidth() / 2, p.y - waypointImage.getHeight() / 2, null);

          textTl = new TextLayout(wayPoint.getWayPointName(), f, frc);
          outline = textTl.getOutline(null);

          AffineTransform old = g2.getTransform();
          g2.translate(p.x + waypointImage.getWidth() / 2, p.y + waypointImage.getHeight() / 2);
          g2.setPaint(Palette3i.getPaint(Palette3i.WHITE_BG));
          g2.draw(outline);
          g2.setPaint(Color.GRAY);
          g2.fill(outline);
          g2.setTransform(old);
        }
      }
    }
	
    // Update de trajectoire
    if (currentTime - lastTrajectoryUpdate > 500)
    {
      trajectory.update();
      lastTrajectoryUpdate = currentTime;
    }

    // Tracé de trajectoire
    GeneralPath fullTrajectory = trajectory.getFullPath(this); 

    final float dash1[] = {8.f, 4.f};
      final BasicStroke dashed =
          new BasicStroke(2.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, dash1, 0.f);
    
    if (fullTrajectory != null)
    {
      g2.setPaint(new Color(1.f, 1.f, 1.f, .5f));
      // g2.setPaint(new Color(0.f, 0.f, 0.f, .3f));
      g2.setStroke(new BasicStroke(4.f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
      g2.draw(fullTrajectory);
      g2.setPaint(Color.RED);
      g2.setStroke(dashed);
      g2.draw(fullTrajectory);
    }
    
    synchronized(this)
    {
    	if(sharedManoeuver != null)
    		sharedManoeuver.paint(g2);
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

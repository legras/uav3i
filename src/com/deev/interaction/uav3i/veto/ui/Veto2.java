package com.deev.interaction.uav3i.veto.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.maps.OsmMapGround;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;

public class Veto2 extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 4871323813688143568L;
  
//  private JPanel contentPane;
  private static OsmMapGround osmMapGround;
  private static JMapViewer mapViewer;
  private int diffWidth, diffHeight;
  private Dimension initialDimensionLayeredPane = new Dimension(500, 400);
  //-----------------------------------------------------------------------------
  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          Veto2 frame = new Veto2();
          frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }
  //-----------------------------------------------------------------------------
  /**
   * Create the frame.
   */
  public Veto2()
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 450, 300);
//    contentPane = new JPanel();
//    contentPane.setBorder(new EmptyBorder(3, 3, 3, 3));
//    contentPane.setLayout(new BorderLayout(0, 0));
//    setContentPane(contentPane);
    
    JLayeredPane lpane = new JLayeredPane();
    
    mapViewer = new JMapViewer(UAV3iSettings.getInteractionMode());
    LatLng startPoint = FlightPlanFacade.getInstance().getStartPoint();
    mapViewer.setDisplayPositionByLatLon(startPoint.getLat(),
                                         startPoint.getLng(),
                                         UAV3iSettings.getTrajectoryZoom() - 3);
    switch (UAV3iSettings.getMapType())
    {
      case MAPNIK:
        mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Default value
        break;
      case BING_AERIAL:
        mapViewer.setTileSource(new BingAerialTileSource());
        break;
      case OSM_CYCLE_MAP:
        mapViewer.setTileSource(new OsmTileSource.CycleMap());
        break;
      case OFF_LINE:
        mapViewer.setTileSource(new OfflineOsmTileSource(UAV3iSettings.getOffLinePath(),
                                                         UAV3iSettings.getOffLineMinZoom(), 
                                                         UAV3iSettings.getOffLineMaxZoom()));
    }
    UAVScope scope = new UAVScope();
    mapViewer.addMapMarker(scope);
    mapViewer.setBounds(0, 0, initialDimensionLayeredPane.width, initialDimensionLayeredPane.height);
    
    lpane.setPreferredSize(initialDimensionLayeredPane);
    
    lpane.add(mapViewer,10);
    
    addComponentListener(new ComponentListener()
    {
      @Override
      public void componentResized(ComponentEvent e)
      {
        Rectangle bounds = ((Component)e.getSource()).getBounds();
        mapViewer.setBounds(0, 0, bounds.width - diffWidth, bounds.height - diffHeight);
      }
      @Override public void componentShown(ComponentEvent e)  {}
      @Override public void componentMoved(ComponentEvent e)  {}
      @Override public void componentHidden(ComponentEvent e) {}
    });
    
//    osmMapGround = new OsmMapGround();
//    //osmMapGround.getMapViewer().setBounds(0, 0, 500, 400);
//    //osmMapGround.setBounds(0, 0, 500, 400);
//    osmMapGround.setSize(500, 400);
//    System.out.println("####### " + osmMapGround.getMapViewer().getSize());
//    
//    lpane.add(osmMapGround, 10);

    //contentPane.add(osmMapGround, BorderLayout.CENTER);
    //contentPane.add(lpane, BorderLayout.CENTER);
    //contentPane.add(lpane);
    this.getContentPane().add(lpane);
    
    pack();
    diffWidth  = this.getSize().width - initialDimensionLayeredPane.width;
    diffHeight = this.getSize().height - initialDimensionLayeredPane.height;
  }
  //-----------------------------------------------------------------------------
  //public static JMapViewer    getMapViewer()     { return osmMapGround.getMapViewer(); }
  public static JMapViewer    getMapViewer()     { return mapViewer; }
  //-----------------------------------------------------------------------------
}

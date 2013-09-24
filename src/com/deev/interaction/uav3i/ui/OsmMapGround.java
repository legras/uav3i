package com.deev.interaction.uav3i.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class OsmMapGround extends Map
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -8999363642143227120L;
  private final JMapViewer mapViewer;
  //-----------------------------------------------------------------------------
  public OsmMapGround()
  {
    mapViewer = new JMapViewer();
    mapViewer.setDisplayPositionByLatLon(48.359407, -4.57013, 11);
    mapViewer.setTileSource(new OsmTileSource.Mapnik());
    
    this.setLayout(new BorderLayout());
    this.add(mapViewer, BorderLayout.CENTER);
  }
  //-----------------------------------------------------------------------------
  public JMapViewer getMapViewer()
  {
    return mapViewer;
  }
  //-----------------------------------------------------------------------------
//  public void paintComponent(Graphics g)
//  { 
//    Graphics2D g2D = (Graphics2D) g;
////    // Application de la mise à l'échelle de l'image.
////    g2D.transform(scaleTransform);
////    g2D.drawImage(map, 0, 0, null);
////    paintAll(getGraphics());
////    mapViewer.paintComponents(g2D);
//  }
  //-----------------------------------------------------------------------------
}

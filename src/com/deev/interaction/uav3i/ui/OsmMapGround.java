package com.deev.interaction.uav3i.ui;

import java.awt.BorderLayout;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class OsmMapGround extends Map
{
  //-----------------------------------------------------------------------------
  private static final long       serialVersionUID = -8999363642143227120L;
  private        final JMapViewer mapViewer;
  //-----------------------------------------------------------------------------
  public OsmMapGround()
  {
    mapViewer = new JMapViewer(false);
    mapViewer.setDisplayPositionByLatLon(48.359407, -4.57013, 10);
    mapViewer.setTileGridVisible(true);
    //mapViewer.setZoomContolsVisible(false);
    //mapViewer.setZoomButtonStyle(JMapViewer.ZOOM_BUTTON_STYLE.VERTICAL);
    
    //mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Default value
    //mapViewer.setTileSource(new BingAerialTileSource());
    //mapViewer.setTileSource(new OsmTileSource.CycleMap());
    //mapViewer.setTileSource(new MapQuestOpenAerialTileSource());
    //mapViewer.setTileSource(new MapQuestOsmTileSource());
    
    this.setLayout(new BorderLayout());
    this.add(mapViewer);
    
//    System.out.println("------------------> " + mapViewer.getPosition());
//    System.out.println("------------------> " + mapViewer.getPosition(100,100));
  }
  //-----------------------------------------------------------------------------
  public JMapViewer getMapViewer()
  {
    return mapViewer;
  }
  //-----------------------------------------------------------------------------
}

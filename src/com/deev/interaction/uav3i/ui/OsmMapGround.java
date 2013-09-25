package com.deev.interaction.uav3i.ui;

import java.awt.BorderLayout;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
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
    mapViewer.setDisplayPositionByLatLon(48.359407, -4.57013, 11);
    mapViewer.setTileSource(new OsmTileSource.Mapnik());

    mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Default value
    //mapViewer.setTileSource(new BingAerialTileSource());
    //mapViewer.setTileSource(new OsmTileSource.CycleMap());
    
    this.setLayout(new BorderLayout());
    this.add(mapViewer, BorderLayout.CENTER);
  }
  //-----------------------------------------------------------------------------
  public JMapViewer getMapViewer()
  {
    return mapViewer;
  }
  //-----------------------------------------------------------------------------
}

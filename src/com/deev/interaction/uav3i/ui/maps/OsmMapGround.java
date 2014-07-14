package com.deev.interaction.uav3i.ui.maps;

import java.awt.BorderLayout;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.Map;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.ui.UAVScope;

public class OsmMapGround extends Map
{
  //-----------------------------------------------------------------------------
  private static final long       serialVersionUID = -8999363642143227120L;
  private        final JMapViewer mapViewer;
  //-----------------------------------------------------------------------------
  public OsmMapGround()
  {
    mapViewer = new JMapViewer(UAV3iSettings.getInteractionMode());

    LatLng startPoint = FlightPlanFacade.getInstance().getStartPoint();
    switch (UAV3iSettings.getMode())
    {
      case VETO:
        mapViewer.setDisplayPositionByLatLon(startPoint.getLat(),
                                             startPoint.getLng(),
                                             UAV3iSettings.getTrajectoryZoom() - 3);
        break;
      default:
        mapViewer.setDisplayPositionByLatLon(startPoint.getLat(),
                                             startPoint.getLng(),
                                             UAV3iSettings.getTrajectoryZoom());
        UAVScope scope = new UAVScope(mapViewer);
        mapViewer.addMapMarker(scope);
        break;
    }
    
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
    
    
    this.setLayout(new BorderLayout());
    this.add(mapViewer);
  }
  //-----------------------------------------------------------------------------
//  public void setDisplayToFitMaxScope(UAVScope scope, JMapViewer mapViewer)
//  {
//    int x_min = Integer.MAX_VALUE;
//    int y_min = Integer.MAX_VALUE;
//    int x_max = Integer.MIN_VALUE;
//    int y_max = Integer.MIN_VALUE;
//    
//    int mapZoomMax = mapViewer.getTileController().getTileSource().getMaxZoom();
//
//    int xScopeCenter = OsmMercator.LonToX(scope.getLon(), mapZoomMax);
//    int yScopeCenter = OsmMercator.LatToY(scope.getLat(), mapZoomMax);
//    x_max = Math.max(x_max, xScopeCenter);
//    y_max = Math.max(y_max, yScopeCenter);
//    x_min = Math.min(x_min, xScopeCenter);
//    y_min = Math.min(y_min, yScopeCenter);
//
//
//    System.out.println("####### getWidth() = " + mapViewer.getWidth() + " / getHeight() = " + mapViewer.getHeight());
//    int height = Math.max(0, 600);
//    int width = Math.max(0, 960);
//    int newZoom = mapZoomMax;
//    int x = x_max - x_min;
//    int y = y_max - y_min;
//    while (x > width || y > height)
//    {
//      newZoom--;
//      x >>= 1;
//      y >>= 1;
//    }
//    x = x_min + (x_max - x_min) / 2;
//    y = y_min + (y_max - y_min) / 2;
//    int z = 1 << (mapZoomMax - newZoom);
//    x /= z;
//    y /= z;
//    mapViewer.setDisplayPosition(x, y, newZoom);
//  }
  //-----------------------------------------------------------------------------
  public JMapViewer getMapViewer()
  {
    return mapViewer;
  }
  //-----------------------------------------------------------------------------
}

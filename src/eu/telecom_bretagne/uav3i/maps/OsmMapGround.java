package eu.telecom_bretagne.uav3i.maps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import com.deev.interaction.uav3i.ui.Map;

import eu.telecom_bretagne.uav3i.UAV3iSettings;

public class OsmMapGround extends Map
{
  //-----------------------------------------------------------------------------
  private static final long       serialVersionUID = -8999363642143227120L;
  private        final JMapViewer mapViewer;
  //-----------------------------------------------------------------------------
  public OsmMapGround()
  {
    mapViewer = new JMapViewer(UAV3iSettings.getInteractionMode());
    //mapViewer.setDisplayPositionByLatLon(48.359407, -4.57013, 14);
    mapViewer.setDisplayPositionByLatLon(UAV3iSettings.getInitialLatitude(),
                                         UAV3iSettings.getInitialLongitude(),
                                         UAV3iSettings.getInitialZoom());
    
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
    
    // Test de configuation du JMapViewer
    
    //mapViewer.setTileGridVisible(true);
    //mapViewer.setZoomContolsVisible(false);
    //mapViewer.setZoomButtonStyle(JMapViewer.ZOOM_BUTTON_STYLE.VERTICAL);
    
     // Choix de la source cartographique : si aucun choix n'est précisé,
     // La source OsmTileSource.Mapnik() est prise par défaut.

    //
    //
    //
    //mapViewer.setTileSource(new MapQuestOpenAerialTileSource());
    //mapViewer.setTileSource(new MapQuestOsmTileSource());

    // Sans layout (layout null), la carte ne s'affiche pas. Avec un FlowLayout,
    // la carte s'affiche avec sa dimension par défaut (400 X 400), avec un
    // BorderLayout, la carte occupe tout l'espace disponible.
    
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
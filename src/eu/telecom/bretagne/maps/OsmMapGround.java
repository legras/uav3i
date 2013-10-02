package eu.telecom.bretagne.maps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOpenAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.MapQuestOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import com.deev.interaction.uav3i.ui.Map;

public class OsmMapGround extends Map
{
  //-----------------------------------------------------------------------------
  private static final long       serialVersionUID = -8999363642143227120L;
  private        final JMapViewer mapViewer;
  //-----------------------------------------------------------------------------
  public OsmMapGround()
  {
    mapViewer = new JMapViewer(false);
    mapViewer.setDisplayPositionByLatLon(48.291556083849486, -4.391552669882558, 10);
    
    // Test de configuation du JMapViewer
    
    //mapViewer.setTileGridVisible(true);
    //mapViewer.setZoomContolsVisible(false);
    //mapViewer.setZoomButtonStyle(JMapViewer.ZOOM_BUTTON_STYLE.VERTICAL);
    
     // Choix de la source cartographique : si aucun choix n'est précisé,
     // La source OsmTileSource.Mapnik() est prise par défaut.

    //mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Default value
    //mapViewer.setTileSource(new BingAerialTileSource());
    //mapViewer.setTileSource(new OsmTileSource.CycleMap());
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

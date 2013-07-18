package com.deev.interaction.uav3i.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.deev.interaction.uav3i.googleMap.CoordinateOutOfBoundsException;
import com.deev.interaction.uav3i.googleMap.GoogleMapCoordinate;
import com.deev.interaction.uav3i.googleMap.GoogleMapManager;
import com.deev.interaction.uav3i.googleMap.GoogleMapManagerUI;
import com.deev.interaction.uav3i.googleMap.ImageFormat;
import com.deev.interaction.uav3i.googleMap.Maptype;
import com.deev.interaction.uav3i.ui.Map;

/**
 * @author Philippe TANGUY
 */
public class GoogleMap extends Map
{
  /*
   * Note sur la dimension de la carte avec la prise en compte de la TimeLine
   * (codée en dur dans la classe TimeLine, à voir par la suite) :
   *   - La bordure bleue fait 10 pixels -> l'affichage de la carte se fait à
   *     partir du point (10,10) et non (0,0).
   *   - La TimeLine fait 200 pixels de haut.
   * La taille de la carte est donc :
   *   - Width : screen width - 20 (10, bordure gauche et 10, bordure droite).
   *   - Height : screen height - 210 (200, TimeLine et 10, bordure supérieure).
   *   
   * Note du 18/07/2013 : suppression de la bordure bleue de la carte. Il n'est
   * plus nécessaire de 10 pixels à G, à D et en haut. Ne prendre en compte que
   * la hauteur de la TimeLine qui ne fait que 200 pixels/.
   */
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 5750148745670298949L;
  
//  AffineTransform _transform;

  private GoogleMapManager   mapManager;
  private GoogleMapManagerUI mapManagerUI;
  private Dimension          screenSize;
  private AffineTransform    adaptDim;

  //-----------------------------------------------------------------------------
  public GoogleMap(Dimension screenSize)
  {
    this.screenSize = screenSize;
    //int mapScale  = 1;
    int mapScale  = 2;
    int mapZoom   = 12;
    mapManagerUI = new GoogleMapManagerUI(this);

    adaptDim = new AffineTransform();
    double mapAreaWidth = screenSize.getWidth();
    double mapAreaHeight = screenSize.getHeight() - 200;
    int mapWidth  = 640;
    int mapHeight = (int) (640.0 * (mapAreaHeight / mapAreaWidth));
    double agrandissement = mapAreaWidth / (mapWidth * mapScale);
    adaptDim.setToScale(agrandissement, agrandissement);
    try
    {
//      int mapWidth  = 640;
//      int mapHeight = 300;
      System.out.println("mapWidth = " + mapWidth + " / mapHeight = " + mapHeight);
      // Baie de douarnenez.
      // GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(48.184951,-4.296429);
      // Télécom Bretagne
      // GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(48.359407,-4.57013);
      // Lac d'Annecy
      GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(45.845064,6.184616);
      mapManager = new GoogleMapManager(initialCenter,
                                        mapWidth, mapHeight,    // Taille (en pixels) de la carte
                                        mapZoom,                // Niveau de zoom initial
                                        mapScale,               // Coefficient d'agrandissement initial
                                        ImageFormat.PNG8,       // Format initial de l'image
                                        Maptype.ROADMAP,        // Type initial de la carte
                                        //Maptype.TERRAIN,        // Type initial de la carte
                                        mapManagerUI);          // Référence vers l'indicateur
      mapManager.go();
    }
    catch (CoordinateOutOfBoundsException | IOException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public void paintComponent(Graphics g)
  { 
    Graphics2D g2D = (Graphics2D) g;
    g2D.transform(adaptDim);
    g2D.drawImage(mapManager.getMap_X(), 0, 0, null);

//    
//    g2.transform(_transform);
//    g2.drawImage(_image, _offset, null);
  }
  //-----------------------------------------------------------------------------
  public void updateTransform()
  {
//    _transform = new AffineTransform();
//    _transform.setToScale(_ppm, _ppm);
//    _transform.translate(-_center.x+getWidth()/_ppm/2., _center.y+getHeight()/_ppm/2.);
  }
  //-----------------------------------------------------------------------------
}

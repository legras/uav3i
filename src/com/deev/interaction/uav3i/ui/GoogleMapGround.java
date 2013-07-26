package com.deev.interaction.uav3i.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
public class GoogleMapGround extends Map
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
  //private GoogleMapManagerUI mapManagerUI;
  private BufferedImage      map;
  private Dimension          screenSize;
  private AffineTransform    scaleTransform;
  private double             imageScale;
  //-----------------------------------------------------------------------------
  /**
   * @param screenSize windows size.
   */
  //public GoogleMapGround(Dimension screenSize)
  public GoogleMapGround(Dimension screenSize, GoogleMapManagerUI mapManagerUI)
  {
    this.screenSize = screenSize;
    
    // Caractéristiques inititales de la carte.
    //int mapScale  = 1;
    int mapScale  = 2;
    int mapZoom   = 12;

    // Calcul des dimensions de la carte qui doit s'afficher dans toute la largeur
    // de la fenêtre et 200 pixels de moins en hauteur (hauteur de la TimeLine).
    double mapAreaWidth = screenSize.getWidth();
    double mapAreaHeight = screenSize.getHeight() - 200;
    
    int mapWidth, mapHeight = -1;
    if(mapAreaWidth > mapAreaHeight)
    {
      if(mapAreaWidth >= 640 * mapScale)
        mapWidth = 640;
      else
        mapWidth = (int) mapAreaWidth / mapScale;
      imageScale = mapAreaWidth / (mapWidth * mapScale);
      mapHeight = (int) (mapAreaHeight / mapScale / imageScale);
    }
    else
    {
      if(mapAreaHeight>= 640 * mapScale)
        mapHeight= 640;
      else
        mapHeight = (int) mapAreaHeight / mapScale;
      imageScale = mapAreaHeight / (mapHeight * mapScale);
      mapWidth = (int) (mapAreaWidth / mapScale / imageScale);
    }
    
    System.out.println("mapWidth = " + mapWidth + " / mapHeight = " + mapHeight + " / imageScale = " + imageScale);
    System.out.println("mapAreaWidth = " + mapAreaWidth + " ("+(int)(mapAreaWidth/imageScale-56/imageScale)+")" + " / mapAreaHeight = " + mapAreaHeight);

    // Composant pour l'affichage de l'état de téléchargement des cartes Google.
//    mapManagerUI = new GoogleMapManagerUI();
//    mapManagerUI.setBounds(10, 10, 46, 46);
//    this.add(mapManagerUI);


    // Gestion de la transformation de la carte (adaptation à la taille)
    scaleTransform = new AffineTransform();
    scaleTransform.setToScale(imageScale, imageScale);

    try
    {
      // Vallon-Pont'Arc
      GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(44.393285,4.395304);
      // Baie de douarnenez.
      // GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(48.184951,-4.296429);
      // Télécom Bretagne
      // GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(48.359407,-4.57013);
      // Lac d'Annecy
      // GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(45.845064,6.184616);
      
      mapManager = new GoogleMapManager(initialCenter,
                                        mapWidth, mapHeight,    // Taille (en pixels) de la carte
                                        mapZoom,                // Niveau de zoom initial
                                        mapScale,               // Coefficient d'agrandissement initial
                                        ImageFormat.PNG8,       // Format initial de l'image
                                        Maptype.ROADMAP,        // Type initial de la carte
                                        //Maptype.TERRAIN,        // Type initial de la carte
                                        //Maptype.SATELLITE,        // Type initial de la carte
                                        mapManagerUI);          // Référence vers l'indicateur
      mapManager.go();
      map = mapManager.getMap_X();
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
    // Application de la mise à l'échelle de l'image.
    g2D.transform(scaleTransform);
    g2D.drawImage(map, 0, 0, null);
  }
  //-----------------------------------------------------------------------------
  public void updateTransform()
  {
    // _transform = new AffineTransform();
    // _transform.setToScale(_ppm, _ppm);
    // _transform.translate(-_center.x+getWidth()/_ppm/2., _center.y+getHeight()/_ppm/2.);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void panPx(double dx, double dy)
  {
    // On doit tenir compte du coefficient d'agrandissement de l'image pour
    // récupérer un déplacement correct dans la carte.
    int realPanDeltaX = (int) (dx/imageScale);
    int realPanDeltaY = (int) (dy/imageScale);
    // Mise à jour de la carte suivant le déplacement.
    map = mapManager.getPartOfFullMap(realPanDeltaX, realPanDeltaY);
  }
  //-----------------------------------------------------------------------------
  public void updateMap(int panDeltaX, int panDeltaY)
  {
    // On doit tenir compte du coefficient d'agrandissement de l'image pour
    // récupérer un déplacement correct dans la carte.
    int realPanDeltaX = (int) (panDeltaX/imageScale);
    int realPanDeltaY = (int) (panDeltaY/imageScale);
    
    // Gestion du dépassement : les coordonnées de la souris sont données
    // par rapport à l'écran et non à l'IHM.
    if(Math.abs(realPanDeltaX/mapManager.getScale()) > mapManager.getWidth())
    {
      if(panDeltaX > 0) realPanDeltaX = mapManager.getWidth();
      else              realPanDeltaX = -mapManager.getWidth();
    }
    if(Math.abs(realPanDeltaY/mapManager.getScale()) > mapManager.getHeight())
    {
      if(panDeltaY > 0) realPanDeltaY = mapManager.getHeight();
      else              realPanDeltaY = -mapManager.getHeight();
    }
    try
    {
      GoogleMapCoordinate gc = mapManager.getMapCoordinateAt(mapManager.getWidth()*mapManager.getScale()/2 - realPanDeltaX, 
                                                             mapManager.getHeight()*mapManager.getScale()/2 - realPanDeltaY);
      mapManager.setCoordinate_X(gc);
      mapManager.go();
      map = mapManager.getMap_X();
    }
    catch (CoordinateOutOfBoundsException | IOException e)
    {
      e.printStackTrace();
    }
    
  }
  //-----------------------------------------------------------------------------
}

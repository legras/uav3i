package com.deev.interaction.uav3i.ui;

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
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 5750148745670298949L;
  
//  AffineTransform _transform;

  private GoogleMapManager   mapManager;
  private GoogleMapManagerUI mapManagerUI;
  //-----------------------------------------------------------------------------
  public GoogleMap()
  {
    mapManagerUI = new GoogleMapManagerUI(this);
    try
    {
      int mapWidth  = 640;
      int mapHeight = 300;
      int mapScale  = 2;
      int mapZoom   = 16;
      GoogleMapCoordinate initialCenter = new GoogleMapCoordinate(48.184951,-4.296429); // Baie de douarnenez.
      mapManager = new GoogleMapManager(initialCenter,
                                        mapWidth, mapHeight,    // Taille (en pixels) de la carte
                                        mapZoom,                // Niveau de zoom initial
                                        mapScale,               // Coefficient d'agrandissement initial
                                        ImageFormat.PNG8,       // Format initial de l'image
                                        Maptype.ROADMAP,        // Type initial de la carte
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

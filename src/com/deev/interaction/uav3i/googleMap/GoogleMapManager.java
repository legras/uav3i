package com.deev.interaction.uav3i.googleMap;

import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.image2map;
import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.latitude2y;
import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.longitude2x;
import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.map2image;
import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.x2longitude;
import static com.deev.interaction.uav3i.googleMap.GoogleMercatorProjectionUtilities.y2latitude;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;


/**
 * <p>
 *   Gestionnaire de chargement des cartes Google obtenues par l'API
 *   <a href="https://developers.google.com/maps/documentation/staticmaps/">Google
 *   Static Maps API</a>. Voir cette documentation pour plus de détails sur
 *   l'utilisation de cette API.
 * </p>
 * <p>
 *  Le chargement des cartes se fait par HTTP à l'aide d'une URL du type :
 * </p>
 * <ul>
 *   <li>
 *     {@code http://maps.googleapis.com/maps/api/staticmap? ... liste de paramètres ... }
 *   </li>
 *   <li>
 *     Exemple : <a href="http://maps.googleapis.com/maps/api/staticmap?center=48.359407,-4.57013&zoom=15&size=400x300&scale=1&format=png8&maptype=roadmap&sensor=false">carte centrée sur Télécom Betagne</a>
 *   </li>
 * </ul>
 * <p>
 *   Le gestionnaire gère le chargement de la carte principale mais aussi des cartes
 *   adjacentes (au nombre de 8) situées immédiatement au nord, nord-est, est, etc.
 *   pour les mettre en cache afin de gérer plus efficacement les déplacements.
 * </p>
 * <p>
 *   Chacune de ces cartes est identifiée dans les structures de données internes
 *   par un codage correspondant la direction cardinale par rapport à la carte centrale
 *   (X = carte centrale, NW = North-West, fullmap = carte complète, etc.) :
 * </p>
 * <xmp>
 *     +---------+---------+---------+
 *     |         |         |         |
 *     |   NW    |    N    |    NE   |
 *     |         |         |         |
 *     +---------+---------+---------+
 *     |         |+-------+|         |
 *     |   W     ||   X   ||    E    |
 *     |         |+-------+|         |
 *     +---------+---------+---------+
 *     |         |         |         |
 *     |   SW    |    S    |    SE   |
 *     |         |         |         |
 *     +---------+---------+---------+
 * </xmp>
 * 
 * @author Philippe TANGUY
 */
public class GoogleMapManager
{
  //-----------------------------------------------------------------------------
  /**
   * Coordonnée géographique du centre de la carte. De cette coordonnée, sont
   * extraites les valeurs latitude et longitude afin de contruire le paramètre
   * {@code center} de l'URL. Ce paramètre est obligatoire dans l'URL en
   * l'absence du paramètre {@code marker}.
   */
  private GoogleMapCoordinate mapCoordinate_X;
  /**
   * Coordonnée image du centre de la carte exprimée en pixels. Cette coordonnée
   * est absolue par rapport à l'équateur et au méridien de Greenwitch.
   */
  private GoogleImageCoordinate imageCoordinate_X;
  /**
   * Largeur en pixels de la carte. Permet de construire le paramètre {@code size}
   * de l'URL. Ce paramètre est obligatoire dans l'URL.
   */
  private int width; 
  /**
   * Hauteur en pixels de la carte. Permet de construire le paramètre {@code size}
   * de l'URL. Ce paramètre est obligatoire dans l'URL.
   */
  private int height;
  /** 
   * Niveau de zoom de la carte : de 0 (monde entier) à 20 (23 dans certaines
   * zones). Ce paramètre est obligatoire dans l'URL.
   */
  private int zoom;
  /** 
   * Coefficient d'agrandissement, valeurs possibles : 1, 2 ou 4. Il s'ajoute au
   * zoom et permet d'avoir une image double en largeur/hauteur ({@code scale=2})
   * ou quadruple ({@code scale=4}) pour la même étendue. Utile sur pour les
   * appareils mobiles dont les écrans offrents en général une résolution
   * supérieure aux écrans LCD. Ce paramètre est obligatoire dans l'URL.
   */
  private int scale;
  /**
   * Format de l'image (<i>PNG</i>, <i>JPG</i>, etc.). Les valeurs possibles sont :
   * {@code png8}, {@code png32}, {@code gif}, {@code jpg} et {@code jpg-baseline}.
   * Ce paramètre est optionnel, la valeur par défaut est alors {@code png8}.
   */
  private ImageFormat format;
  /**
   * Type de la carte (<i>roadmap</i>, <i>satellite</i>, etc.). Les valeurs possibles
   * sont : {@code roadmap}, {@code satellite}, {@code terrain} et {@code hybrid}. Ce
   * paramètre est optionnel, la valeur par défaut est alors {@code roadmap}.
   */
  private Maptype maptype;
  /**
   * Paramètre permettant de paramètrer si l'application utilise un localisateur GPS.
   * Ce n'est pas le cas ici, la valeur est fixée à {@code false}. Ce paramètre est
   * obligatoire dans l'URL.
   */
  private String sensor = "false";
  /**
   * Clé Google d'identification de l'application. Ce paramètre est obligatoire dans
   * l'URL afin de pouvoir dépasser la limite (basse...) de requêtes sur les serveurs
   * Google. L'obtention d'une clé (gratuite) permet d'avoir un quota de 25 000
   * requêtes HTTP par jour.<br/>
   * Voir <a href="https://developers.google.com/maps/documentation/staticmaps/#api_key">https://developers.google.com/maps/documentation/staticmaps/#api_key</a>
   * pour plus de détails.
   */
  private String key;
  /**
   * Ensemble des coordonnées des cartes adjacentes. Chaque coordonnée est
   * référencée par une clé prenant la valeur cardinale de la position par
   * rapport à la carte centrale : {@code NW}, {@code SE}, etc. 
   */
  private Map<String, GoogleMapCoordinate> adjacentMapsCoordinate;
  /**
   * Ensemble des cartes : carte centrale + cartes adjacentes + combinaison
   * de l'ensemble des cartes.
   */
  private Map<String, BufferedImage> maps;

//  private int centreX, centreY;
  
  // Debug
  /**
   * Debug : si {@code true}, écrit les 9 cartes intermédiaires sur le disque.
   */
  private boolean writeElementaryMapsOnDisk = false;
  /**
   * Debug : si {@code true}, écrit la carte complète (assemblage des 9 cartes) sur le disque.
   */
  private boolean writeFullMapsOnDisk       = false;
  /**
   * Debug : si {@code true}, affiche la limite des cartes individuelles sur la carte assemblée.
   */
  private boolean drawSeparationOnFullMap   = true;
  /**
   * Référence vers le composant graphique visualisant l'état d'avancement du
   * chargement du cache des cartes adjacentes. Voir {@link GoogleMapManagerUI}.
   */
  private GoogleMapManagerUI googleMapManagerUI;
  //-----------------------------------------------------------------------------
  /**
   * Constructeur.
   * 
   * @param mapCoordinate      coordonnée géographique de la carte.
   * @param width              largeur en pixels de la carte.
   * @param height             hauteur en pixels de la carte.
   * @param zoom               niveau de zzom.
   * @param scale              échelle.
   * @param format             format de l'image.
   * @param maptype            type de la carte.
   * @param googleMapManagerUI composant graphique de visualisation du chargement des cartes adjacentes.
   * @throws CoordinateOutOfBoundsException
   * @throws IOException
   */
  public GoogleMapManager(GoogleMapCoordinate mapCoordinate,
                          int width, int height,
                          int zoom, int scale,
                          ImageFormat format,
                          Maptype maptype,
                          GoogleMapManagerUI googleMapManagerUI)
    throws CoordinateOutOfBoundsException, IOException
  {
    this.zoom         = zoom;
    this.width        = width;
    this.height       = height;
    this.scale        = scale;
    this.format       = format;
    this.maptype      = maptype;
    this.setCoordinate_X(mapCoordinate);
    
    this.googleMapManagerUI = googleMapManagerUI;

    // Lecure de la clé dans le fichier "StaticMapsAPIKey.properties" (attribut "key")
    Properties prop = new Properties();
    prop.load(new FileInputStream("StaticMapsAPIKey.properties"));
    key = prop.getProperty("key");
    
    adjacentMapsCoordinate = new HashMap<String, GoogleMapCoordinate>();
    maps = new HashMap<String, BufferedImage>();
  }
  //-----------------------------------------------------------------------------
  /**
   * Constructeur.
   * 
   * @param mapCoordinate             coordonnée géographique de la carte.
   * @param width                     largeur en pixels de la carte.
   * @param height                    hauteur en pixels de la carte.
   * @param zoom                      niveau de zzom.
   * @param scale                     échelle.
   * @param format                    format de l'image.
   * @param maptype                   type de la carte.
   * @param writeElementaryMapsOnDisk pour debug : écriture des cartes adjacentes sur le disque.
   * @param writeFullMapsOnDisk       pour debug : écriture de la carte complète sur le disque.
   * @param drawSeparationOnFullMap   pour debug : visualisation de la séparation des différentes cartes adjacentes.
   * @throws CoordinateOutOfBoundsException
   * @throws IOException
   */
  public GoogleMapManager(GoogleMapCoordinate mapCoordinate,
                          int width, int height,
                          int zoom, int scale,
                          ImageFormat format,
                          Maptype maptype,
                          boolean writeElementaryMapsOnDisk,
                          boolean writeFullMapsOnDisk,
                          boolean drawSeparationOnFullMap)
    throws CoordinateOutOfBoundsException, IOException
  {
    this(mapCoordinate, width, height, zoom, scale, format, maptype, null);
    this.writeElementaryMapsOnDisk = writeElementaryMapsOnDisk;
    this.writeFullMapsOnDisk       = writeFullMapsOnDisk;
    this.drawSeparationOnFullMap   = drawSeparationOnFullMap;
  }
  //-----------------------------------------------------------------------------
  /**
   * Déclenchement du chargement des cartes après le paramétrage (centre, taille,
   * zoom, etc.). Ce paramétrage est fait une première fois avec l'appel au
   * constructeur puis par la modification du paramétrage (changement de zoom,
   * déplacement sur la carte, choix d'un type de carte différent, etc.). À
   * chaque fois cette méthode doit être appelée.
   * 
   * @throws CoordinateOutOfBoundsException
   * @throws IOException
   */
  public void go() throws CoordinateOutOfBoundsException, IOException
  {
    if(googleMapManagerUI != null)
      googleMapManagerUI.start();
    
    // On charge tout de suite la carte centrale pour que l'affichage soit le plus
    // rapide possible.
    loadMap_X();
    
    // Une fois la carte centrale chargée, on délègue à un Thread la gestion des
    // cartes adjacentes.
    new Thread(new AdjacentMapsManager()).start();
  }
  //-----------------------------------------------------------------------------
  /**
   * Calcul des centres géographiques des cartes adjacentes par rapport au centre
   * de la carte principale. Cette méthode est un préalable au téléchargement des
   * cartes.
   *  
   * @throws CoordinateOutOfBoundsException
   */
  private void getCenters() throws CoordinateOutOfBoundsException
  {
    int pointX_y = imageCoordinate_X.y;
    int pointX_x = imageCoordinate_X.x;
//  int heightDifference = height * scale;
//  int widthDifference  = width * scale;
    int heightDifference = height;
    int widthDifference  = width;
    
    adjacentMapsCoordinate.put("NE", new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y + heightDifference),
                                                             x2longitude(zoom, scale, pointX_x + widthDifference)));
    adjacentMapsCoordinate.put("E",  new GoogleMapCoordinate(mapCoordinate_X.latitude,
                                                             x2longitude(zoom, scale, pointX_x + widthDifference)));

    adjacentMapsCoordinate.put("SE", new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y - heightDifference),
                                                             x2longitude(zoom, scale, pointX_x + widthDifference)));

    adjacentMapsCoordinate.put("S",  new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y - heightDifference),
                                                             mapCoordinate_X.longitude));

    adjacentMapsCoordinate.put("SW", new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y - heightDifference),
                                                             x2longitude(zoom, scale, pointX_x - widthDifference)));

    adjacentMapsCoordinate.put("W",  new GoogleMapCoordinate(mapCoordinate_X.latitude,
                                                             x2longitude(zoom, scale, pointX_x - widthDifference)));

    adjacentMapsCoordinate.put("NW", new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y + heightDifference),
                                                             x2longitude(zoom, scale, pointX_x - widthDifference)));

    adjacentMapsCoordinate.put("N",  new GoogleMapCoordinate(y2latitude(zoom, scale, pointX_y + heightDifference),
                                                             mapCoordinate_X.longitude));
  }
  //-----------------------------------------------------------------------------
  /**
   * Chargement de la carte centrale : X = centre.
   * 
   * @throws MalformedURLException
   * @throws IOException
   */
  private void loadMap_X() throws MalformedURLException, IOException
  {
    maps.put("X", loadMap("X",  mapCoordinate_X,  writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setX(true);
  }
  //-----------------------------------------------------------------------------
  /**
   * Chargement des cartes adjacentes.
   * 
   * @throws MalformedURLException
   * @throws IOException
   */
  private void loadAdjacentMaps() throws MalformedURLException, IOException
  {
    maps.put("NE", loadMap("NE", adjacentMapsCoordinate.get("NE"), writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setNE(true);
    
    maps.put("E",  loadMap("E",  adjacentMapsCoordinate.get("E"),  writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setE(true);
    
    maps.put("SE", loadMap("SE", adjacentMapsCoordinate.get("SE"), writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setSE(true);
    
    maps.put("S",  loadMap("S",  adjacentMapsCoordinate.get("S"),  writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setS(true);
    
    maps.put("SW", loadMap("SW", adjacentMapsCoordinate.get("SW"), writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setSW(true);
    
    maps.put("W",  loadMap("W",  adjacentMapsCoordinate.get("W"),  writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setW(true);
    
    maps.put("NW", loadMap("NW", adjacentMapsCoordinate.get("NW"), writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setNW(true);
    
    maps.put("N",  loadMap("N",  adjacentMapsCoordinate.get("N"),  writeElementaryMapsOnDisk));
    if(googleMapManagerUI != null)
      googleMapManagerUI.setN(true);
  }
  //-----------------------------------------------------------------------------
  /**
   * Fusion de l'ensemble des cartes (centrale + les 8 carte adjacentes) en une
   * image unique. C'est cette image unique (fullmap) qui sert à la gestion des déplacements
   * dans l'interface graphique.
   * 
   * @throws IOException
   */
  private void mergeMaps() throws IOException
  {
    // Largeur et hauteur d'une carte (pas de l'ensemble).
    int widthMap  = width * scale;
    int heightMap = height * scale;
    ImageObserver imageObserver = null;
    
    // Création de l'image.
    // TODO voir pour transition lors des déplacement (transparence, flou, ...)
    BufferedImage fullmap = new BufferedImage(widthMap * 3,                 // Une image avec une couche 
                                              heightMap * 3,                // alpha pour la gestion
                                              BufferedImage.TYPE_INT_ARGB); // de la transparence.
    
    // Récupération du contexte graphique.
    Graphics2D g2D = fullmap.createGraphics();

    // On compose la carte globale en juxtaposant l'ensemble des cartes.
    g2D.drawImage(maps.get("NW"), 0,            0,             imageObserver);
    g2D.drawImage(maps.get("N"),  widthMap,     0,             imageObserver);
    g2D.drawImage(maps.get("NE"), 2 * widthMap, 0,             imageObserver);
    g2D.drawImage(maps.get("W"),  0,            heightMap,     imageObserver);
    g2D.drawImage(maps.get("X"),  widthMap,     heightMap,     imageObserver);
    g2D.drawImage(maps.get("E"),  2 * widthMap, heightMap,     imageObserver);
    g2D.drawImage(maps.get("SW"), 0,            2 * heightMap, imageObserver);
    g2D.drawImage(maps.get("S"),  widthMap,     2 * heightMap, imageObserver);
    g2D.drawImage(maps.get("SE"), 2 * widthMap, 2 * heightMap, imageObserver);
    
    // Ajout éventuel du quadrillage pour test : affichage de la matrice des 9 cartes.
    if(drawSeparationOnFullMap)
    {
      g2D.setColor(Color.red);
      g2D.drawLine(widthMap ,     0,             widthMap,     3 * heightMap);
      g2D.drawLine(2 * widthMap , 0,             2 * widthMap, 3 * heightMap);
      g2D.drawLine(0,             heightMap,     3 * widthMap, heightMap);
      g2D.drawLine(0,             2 * heightMap, 3 * widthMap, 2 * heightMap);
    }

    // Une des traductions de dispose est "écouler"...
    g2D.dispose();
    
    maps.put("fullmap", fullmap);
    
    // Sauvegarde éventuelle de la carte sur le disque.
    if(writeFullMapsOnDisk)
      ImageIO.write(fullmap, "png", new File("images générées/full map.png"));
    
    if(googleMapManagerUI != null)
      googleMapManagerUI.setFinished(true);
  }
  //-----------------------------------------------------------------------------
  /**
   * Chargement d'une carte Google.
   * 
   * @param nom              nom de la carte, utile uniquement en cas d'écriture du fichier sur le disque.
   * @param coordinate       coordonnée du centre de la carte.
   * @param writeImageOnDisk si {@code true}, écriture du fichier sur le disque.
   * @return la carte dans une instance de {@link BufferedImage}.
   * @throws MalformedURLException
   * @throws IOException
   */
  private BufferedImage loadMap(String nom, GoogleMapCoordinate coordinate, boolean writeImageOnDisk)
      throws MalformedURLException, IOException
  {
//    long start = System.currentTimeMillis();
    BufferedImage map = ImageIO.read(new URL(getUrlMap(coordinate)));
    // Sauvegarde éventuelle de la carte sur le disque.
    if(writeImageOnDisk)
      ImageIO.write(map, format.getFileExtension(), new File("images générées/" + nom + ".png"));
//    long end = System.currentTimeMillis();
//    System.out.println("Temps de chargement de " + nom + " = " + (end - start) + " ms");
    return map;
  }
  //-----------------------------------------------------------------------------
  /**
   * Génération de l'URL pour le téléchargement d'une carte sur les serveurs de Google.
   * 
   * @param coordinate coordonnée géographique du centre de la carte à récupérer.
   * @return l'URL au sein d'une chaîne de caractères.
   */
  private String getUrlMap(GoogleMapCoordinate coordinate)
  {
    // http://maps.googleapis.com/maps/api/staticmap?center=48.404130,-4.408229&zoom=15&size=600x300&sensor=false&key=AIzaSyDxsS6bEqdCrOjyel9tUsyWtpvMhcq0RV4
    String urlMap = "http://maps.googleapis.com/maps/api/staticmap?" +
                    "center=" + coordinate.latitude + "," + coordinate.longitude +
                    //"&markers=size:normal|color:blue|" + coordinate.latitude + "," + coordinate.longitude +
                    "&zoom=" + zoom +
                    "&size=" + width + "x"+ height +
                    "&scale=" + scale +
                    "&format=" + format.value +
                    "&maptype=" + maptype.value +
                    "&sensor=" + sensor +
                    "&key=" + key;
    //System.out.println("####### urlMap = " + urlMap);
    return urlMap;
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la coordonnée relative à l'image (le point (0,0) est en haut, à gauche)
   * exprimée en pixels d'un point connu par sa coordonnée géographique. Attention,
   * si la coordonnée est en dehors de l'étendue de l'image, la coordonnée sera hors
   * valeurs.
   * 
   * @param mapCoordinate coordonnée géographique du point dont on veut la coordonnée image.
   * @return un tableau de deux entiers représentant la coordonnée (0 -> la latitude en y, 1 -> la longitude en x).
   */
  public  int[] getImageCoordinateAt(GoogleMapCoordinate mapCoordinate)
  {
    // Coordonnée image (valeur absolue) du centre de la carte
    int pointX_y = imageCoordinate_X.y;
    int pointX_x = imageCoordinate_X.x;

    // Coordonnée image (valeur absolue) du point dont on veut la coordonnée locale
    GoogleImageCoordinate gic = map2image(zoom, scale, mapCoordinate);
    
    int[] coordonnee = new int[2];
    coordonnee[0] = pointX_y + (height/2) - gic.y;
    coordonnee[1] = gic.x - pointX_x + (width/2);
    return coordonnee;
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la coordonnée image (absolue) exprimée en pixels d'un point connu
   * par sa position relative en x et en y sur l'image.
   * 
   * @param posX coordonnée x du point sur l'image.
   * @param posY coordonnée y du point sur l'image.
   * @return la coordonnée image dans une instance de {@link GoogleImageCoordinate}.
   */
  public GoogleImageCoordinate getImageCoordinateAt(int posX, int posY)
  {
    // Coordonnée image (valeur absolue) du centre de la carte
    int pointX_y = imageCoordinate_X.y;
    int pointX_x = imageCoordinate_X.x;

    // Différences par rapport au centre de la carte.
//    int deltaX = posX - (width/2);
//    int deltaY = posY - (height/2);
    int deltaX = (posX/scale) - (width/2);
    int deltaY = (posY/scale) - (height/2);
    
    // Calcul de la coordonnée image à partir de la position pointée
    // par la souris.
    return new GoogleImageCoordinate(pointX_y - deltaY,
                                     pointX_x + deltaX);
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la coordonnée géographique (latitude, longitude) d'un point connu
   * par sa position relative en x et en y sur l'image.
   * 
   * @param posX coordonnée x du point sur l'image.
   * @param posY coordonnée x du point sur l'image.
   * @return la coordonnée géographique dans une instance de {@link GoogleMapCoordinate}.
   * @throws CoordinateOutOfBoundsException
   */
  public GoogleMapCoordinate getMapCoordinateAt(int posX, int posY)
      throws CoordinateOutOfBoundsException
  {
    return image2map(zoom, scale, getImageCoordinateAt(posX, posY));
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie une portion de l'image concaténée pour l'affichage dans l'interface
   * graphique lors des déplacements. Cette image possède la même taille que
   * l'image de départ.
   * 
   * @param deltaX déplacement en x par rapport à l'image de départ.
   * @param deltaY déplacement en y par rapport à l'image de départ.
   * @return l'image correspondant au déplacement dans une instance de {@link BufferedImage}.
   */
  public BufferedImage getPartOfFullMap(int deltaX, int deltaY)
  {
//    int x = width - deltaX;
//    int y = height - deltaY;
//
//    // Gestion du dépassement des images en cache.
//    if(x < 0)          x = 0;
//    if(x > 2 * width)  x = 2 * width;
//    if(y < 0)          y = 0;
//    if(y > 2 * height) y = 2 * height;
//    
//    return maps.get("fullmap").getSubimage(x, y, width, height);

    int x = width*scale - deltaX;
    int y = height*scale - deltaY;

    // Gestion du dépassement des images en cache.
    if(x < 0)                x = 0;
    if(x > 2 * width*scale)  x = 2 * width*scale;
    if(y < 0)                y = 0;
    if(y > 2 * height*scale) y = 2 * height*scale;

    return maps.get("fullmap").getSubimage(x, y, width*scale, height*scale);
  }
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour de la coordonnée géographique du centre de la carte. La coordonnée image
   * correspondant est automatiquement recalculée. 
   * 
   * @param mapCoordinate nouvelle coordonnée géographique.
   */
  public void setCoordinate_X(GoogleMapCoordinate mapCoordinate)
  {
    this.mapCoordinate_X = mapCoordinate;
    imageCoordinate_X = new GoogleImageCoordinate(latitude2y(zoom, scale, mapCoordinate_X.latitude),
                                                  longitude2x(zoom, scale, mapCoordinate_X.longitude));
  }
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour du zoom de la carte. Attention, la méthode met à jour la valeur
   * uniquement si 0 <= zoom <= 25. Pour une valeur inférieure, le zoom vaut 0 et
   * 25 pour une valeur supérieure.
   * 
   * @param zoom nouvelle valeur du zoom.
   */
  public void setZoom(int zoom)
  {
    if(zoom < 0)
      this.zoom = 0;
    else if(zoom > 25)
      this.zoom = 25;
    else
      this.zoom = zoom;
    
    // Le niveau de zoom ayant changé, même si la coordonnée géographique du centre
    // n'a pas bougé, la coordonnée image n'a plus la même valeur.
    imageCoordinate_X = new GoogleImageCoordinate(latitude2y(zoom, scale, mapCoordinate_X.latitude),
                                                  longitude2x(zoom, scale, mapCoordinate_X.longitude));
  }
  //-----------------------------------------------------------------------------
  /**
   * Arrondi d'un double avec n éléments après la virgule.<br/>
   * Voir : <a href="http://java.developpez.com/faq/java/?page=langage_chaine#LANGAGE_MATH_arrondir">http://java.developpez.com/faq/java/?page=langage_chaine#LANGAGE_MATH_arrondir</a>
   * 
   * @param a La valeur à convertir.
   * @param n Le nombre de décimales à conserver.
   * @return La valeur arrondi à n décimales.
   */
  public static double floor(double a, int n)
  {
    double p = Math.pow(10.0, n);
    return Math.floor((a*p)+0.5) / p;
  }
  //-----------------------------------------------------------------------------
//  public void setAlpha(byte alpha, BufferedImage image)
//  {
//    alpha %= 0xff;
//    for (int cx=0;cx<image.getWidth();cx++)
//    {
//      for (int cy=0;cy<image.getHeight();cy++)
//      {
//        int color = image.getRGB(cx, cy);
//
//        int mc = (alpha << 24) | 0x00ffffff;
//        int newcolor = color & mc;
//        image.setRGB(cx, cy, newcolor);
//      }
//    }
//  }
  //-----------------------------------------------------------------------------
  public GoogleMapCoordinate getMapCoordinate_X() { return mapCoordinate_X;            }
  public int                 getZoom()            { return zoom;                       }
  public int                 getWidth()           { return width;                      }
  public int                 getHeight()          { return height;                     }
  public int                 getScale()           { return scale;                      }
  public ImageFormat         getFormat()          { return format;                     }
  public Maptype             getMaptype()         { return maptype;                    }
  public String              getSensor()          { return sensor;                     }
  public BufferedImage       getMap_X()           { return maps.get("X");              }
  public BufferedImage       getFullmap()         { return maps.get("fullmap");        }
  public String              getUrlMap_X()        { return getUrlMap(mapCoordinate_X); }
  //-----------------------------------------------------------------------------
  public void setWidth(int width)                             { this.width           = width;       }
  public void setHeight(int height)                           { this.height          = height;      }
  public void setScale(int scale)                             { this.scale           = scale;       }
  public void setFormat(ImageFormat format)                   { this.format          = format;      }
  public void setMaptype(Maptype maptype)                     { this.maptype         = maptype;     }
  public void setSensor(String sensor)                        { this.sensor          = sensor;      }
  //-----------------------------------------------------------------------------







  //-----------------------------------------------------------------------------
  /**
   * Classe chargeant en arrière plan les cartes adjacentes. Ce traitement se fait
   * dans un thread séparé, la classe imlémente {@code Runnable}.
   * 
   * @author Philippe TANGUY
   */
  private class AdjacentMapsManager implements Runnable
  {
    @Override
    public void run()
    {
      try
      {
        // Calcul des centres des images adjacentes.
        getCenters();

        // Chargement des 8 cartes adjacentes.
        loadAdjacentMaps();

        // Génération de la carte globale.
        mergeMaps();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.googleMap;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.rint;
import static java.lang.Math.sinh;
import static java.lang.Math.tan;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * <p>
 *   Classe utilitaire permettant la manipulation des cartes Google obtenues par l'API
 *   <a href="https://developers.google.com/maps/documentation/staticmaps/">Google Static
 *   Maps API</a> (délivrant uniquement des carte raster, des images bitmap donc). Elle
 *   permet principalement d'obtenir l'équivalence, dans les deux sens, entre les
 *   coordonnées géographiques (latitude et longitude exprimées en degrés) et les
 *   coordonnées image exprimées en pixels.
 * </p>
 * <p>Remarques :</p>
 * <ul>
 *   <li>
 *     Comme pour les coordonnées géographiques, les coordonnées images sont référencées
 *     à partir du croisement de l'équateur par le méridien de Greenwitch.
 *   </li>
 *   <li>
 *     Les cartes Google utilisent la projection de Mercator : voir
 *     <a href="http://en.wikipedia.org/wiki/Mercator_projection">wikipedia</a> pour plus
 *     d'information sur les fonctions mathématiques utilisées pour les transformations
 *     coordonnées géographiques <-> coordonnées image.
 *   </li>
 * </ul>
 * </p>Méthode principales  :</p>
 * <ul>
 *   <li>
 *     {@link #longitude2x} : transforme une longitude géographique (degré) en une
 *     coordonnée image en abscisse (pixel).
 *   </li> 
 *   <li>
 *     {@link #x2longitude} : fonction inverse.
 *   </li>
 *   <li>
 *     {@link #latitude2y} : transforme une latitude géographique (degré) en une
 *     coordonnée image en ordonnée (pixel).
 *   </li> 
 *   <li>
 *     {@link #y2latitude} : fonction inverse.
 *   </li>
 *   <li>
 *     {@link #map2image} : transforme une coordonnée géographique (couple
 *     latitude-longitude en degrés) en une coordonnée image (couple y-x en pixels).
 *   </li> 
 *   <li>
 *     {@link #image2map} : fonction inverse.
 *   </li>
 * </ul>
 * <p>Les coordonnées image dépendent à la fois des paramètres <i>zoom</i> et <i>scale</i> :</p>
 * <ul>
 *   <li>
 *     {@code zoom} : résolution de la carte, de 0 (visualisation du monde entier) à 20 (23 dans
 *     certaines zones).
 *   </li> 
 *   <li>
 *     {@code scale} : coefficient d'agrandissement qui s'ajoute au zoom et permet d'avoir
 *     une image double en largeur/hauteur ({@code scale=2}) ou quadruple ({@code scale=4})
 *     pour la même étendue. Utile sur pour les appareils mobiles dont les écrans offrents en
 *     général une résolution supérieure aux écrans LCD.<br/>
 *     Exemples, les cartes suivantes offrent la même taille et la même étendue pour des 
 *     niveaux de <i>zoom</i> et de <i>scale</i> différents :
 *     <ul>
 *       <li>
 *         <a href="http://maps.googleapis.com/maps/api/staticmap?center=48.404173,-4.408392&zoom=12&size=1024x1024&scale=1&format=png8&maptype=roadmap&sensor=false">zoom = 12, size = 1024x1024, scale = 1</a>
 *       </li>
 *       <li>
 *         <a href="http://maps.googleapis.com/maps/api/staticmap?center=48.404173,-4.408392&zoom=11&size=512x512&scale=2&format=png8&maptype=roadmap&sensor=false">zoom = 11, size = 512x512, scale = 2</a>
 *       </li>
 *       <li>
 *         <a href="http://maps.googleapis.com/maps/api/staticmap?center=48.404173,-4.408392&zoom=10&size=256x256&scale=4&format=png8&maptype=roadmap&sensor=false">zoom = 10, size = 256x256, scale = 4</a>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * @author Philippe TANGUY
 */
public class GoogleMercatorProjectionUtilities
{
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la taille maximale théorique de la carte à un niveau de zoom et
   * d'échelle (paramètre {@code scale}) donnés afin d'afficher le monde entier.
   * Quelque soit le niveau de zoom, pour afficher le monde entier, les cartes
   * Google sont carrées. Pour une valeur du paramètre {@code scale} égal à 1, la
   * taille varie de 256 pixels x 256 pixels (zoom = 0) à 2 147 483 647 x 
   * 2 147 483 647 (zoom = 23) soit plus de 30 millions de To !

   * @param zoom  zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines 
   *              zones).
   * @param scale coefficient d'agrandissement -> 1, 2 ou 4.
   * @return un valeur entière représentant la taille du côté de la carte.
   */
  public static int getMaxMapSize(int zoom, int scale)
  //public static int getMaxMapSize(int zoom)
  {
    //256 * 2^zoom
    //return (int) (256 * pow(2, zoom) * scale);
    return (int) (256 * pow(2, zoom));
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie le ratio d'une carte google suivant la valeur des paramètres {@code zoom}
   * et {@code scale}.
   * 
   * @param zoom  zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines
   *              zones).
   * @param scale coefficient d'agrandissement -> 1, 2 ou 4.
   * @return le ratio de la carte sous la forme d'un {@code double}.
   */
  public static double getRatio(int zoom, int scale)
  {
    //System.out.println("~~~~~~~~~~~~~~~> getRatio(zoom="+zoom+", scale="+scale+")");
    return getMaxMapSize(zoom, scale) / (2 * PI);
    //return getMaxMapSize(zoom) / (2 * PI);
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une longitude exprimée en degrés en une abcsisse exprimée en pixels. La
   * valeur 0 correspond au méridien de Greenwitch. Une valeur négative correspond à une
   * longitude ouest.<br/>
   * La méthode inverse est la méthode {@link #x2longitude}.
   * 
   * @param zoom      zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines
   *                  zones).
   * @param scale     coefficient d'agrandissement -> 1, 2 ou 4.
   * @param longitude longitude exprimée en degrés.
   * @return un entier correspondant à l'absisse en pixels.
   */
  public static int longitude2x(int zoom, int scale, double longitude)
  {
    // TODO : comme pour la transformation corrdonnée image en x -> longitude, prévoir la gestion du débordement à l'est et à l'ouest.
    int w = getMaxMapSize(zoom, scale); // Taille maxi en pixels de la carte siuvant le niveau de zoom et l'échelle.
    //int w = getMaxMapSize(zoom); // Taille maxi en pixels de la carte siuvant le niveau de zoom et l'échelle.
    double x = w * ((longitude + 180 ) / 360);
    return (int)(rint(x));
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une abcsisse exprimée en pixels en une longitude exprimée en degrés. La
   * valeur 0 correspond au méridien de Greenwitch. Une valeur négative correspond à une
   * longitude ouest..<br/>
   * La méthode inverse est la méthode {@link #longitude2x}.

   * 
   * @param zoom        zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines
   *                    zones).
   * @param scale       coefficient d'agrandissement -> 1, 2 ou 4.
   * @param xCoordinate abscisse du point exprimée en pixels.
   * @return un double correspondant à la longitude en degrés.
   */
  public static double x2longitude(int zoom, int scale, int xCoordinate)
  {
    int w = getMaxMapSize(zoom, scale); // Taille maxi en pixels de la carte siuvant le niveau de zoom et l'échelle.
    //int w = getMaxMapSize(zoom); // Taille maxi en pixels de la carte siuvant le niveau de zoom et l'échelle.
    double longitude = ((360 * (double)xCoordinate) / w) - 180;
    
    // En cas de débordement à l'est
    if(longitude > 180)
      longitude = -180 + (longitude % 180);
    
    // En cas de débordement à l'ouest
    if(longitude < -180)
      longitude = 180 + (longitude % 180);
    
    return longitude;
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une latitude exprimée en degrés en une ordonnée exprimée en pixels. La
   * valeur 0 correspond à l'équateur. Une valeur négative correspond à une
   * latitude sud..<br/>
   * La méthode inverse est la méthode {@link #y2latitude}.

   * 
   * @param zoom     zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines
   *                 zones).
   * @param scale    coefficient d'agrandissement -> 1, 2 ou 4.
   * @param latitude latitude exprimée en degrés.
   * @return un entier correspondant à l'ordonnée en pixels.
   */
  public static int latitude2y(int zoom, int scale, double latitude)
  {
    double y = log(tan(toRadians(latitude)) + 1 / cos(toRadians(latitude))) * getRatio(zoom, scale);
    return (int)(rint(y));
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une ordonnée exprimée en pixels en une latitude exprimée en degrés. La
   * valeur 0 correspond à l'équateur. Une valeur négative correspond à une
   * latitude sud..<br/>
   * La méthode inverse est la méthode {@link #latitude2y}.

   * 
   * @param zoom        zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines
   *                    zones).
   * @param scale       coefficient d'agrandissement -> 1, 2 ou 4.
   * @param yCoordinate ordonnée du point exprimée en pixels.
   * @return un double correspondant à la latutude en degrés.
   */
  public static double y2latitude(int zoom, int scale, int yCoordinate)
  {
    double latitude = toDegrees(atan(sinh(yCoordinate/getRatio(zoom, scale))));
    //double latitude = toDegrees((2 * atan(pow(E,yCoordinate))) - (PI / 2));
    //double latitude = (2 * atan(pow(E,yCoordinate))) - (PI / 2);
    return latitude;
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une coordonnée géographique ({@link GoogleMapCoordinate})en une
   * coordonnée image ({@link GoogleImageCoordinate}). La méthode ne fait
   * qu'appeler les méthodes {@link #latitude2y} et {@link #longitude2x} pour 
   * chacun des membres du couple.<br/>
   * La méthode inverse est la méthode {@link #image2map}.
   * 
   * @param zoom          zoom de la carte -> de 0 (monde entier) à 20 (23 dans
   *                      certaines zones).
   * @param scale         coefficient d'agrandissement -> 1, 2 ou 4.
   * @param mapCoordinate coordonée géographique à transformer.
   * @return l'instance de {@link GoogleMapCoordinate} une fois transformée.
   */
  public static GoogleImageCoordinate map2image(int zoom, int scale, GoogleMapCoordinate mapCoordinate)
  {
    return new GoogleImageCoordinate(latitude2y(zoom, scale, mapCoordinate.latitude), 
                                     longitude2x(zoom, scale, mapCoordinate.longitude));
  }
  //-----------------------------------------------------------------------------
  /**
   * Transforme une coordonnée image ({@link GoogleImageCoordinate}) en une
   * coordonnée géographique ({@link GoogleMapCoordinate}). La méthode ne fait
   * qu'appeler les méthodes {@link #y2latitude} et {@link #x2longitude} pour 
   * chacun des membres du couple.
   * La méthode inverse est la méthode {@link #map2image}.
   * 
   * @param zoom            zoom de la carte -> de 0 (monde entier) à 20 (23 dans certaines zones).
   * @param scale           coefficient d'agrandissement -> 1, 2 ou 4.
   * @param pixelCoordinate coordonnée image à transformer.
   * @return l'instance de {@link GoogleMapCoordinate} à transformer.
   * @throws CoordinateOutOfBoundsException
   */
  public static GoogleMapCoordinate image2map(int zoom, int scale, GoogleImageCoordinate pixelCoordinate)
      throws CoordinateOutOfBoundsException
  {
    return new GoogleMapCoordinate(y2latitude(zoom, scale, pixelCoordinate.y),
                                   x2longitude(zoom, scale, pixelCoordinate.x));
  }
  //-----------------------------------------------------------------------------
//  /**
//   * Renvoie la taille d'un degré de longitude en nombre de pixels suivant le niveau
//   * de zoom.
//   * 
//   * @param zoom niveau de zoom.
//   * @return
//   */
//  public static double getDegreeSize(int zoom, int scale)
//  {
//    return (double)(getMaxMapSize(zoom, scale) / 360.0);
//  }
//  //-----------------------------------------------------------------------------
//  /**
//   * Renvoie la taille d'un pixel en degrés de longitude suivant le niveau de zoom.
//   * 
//   * @param zoom niveau de zoom.
//   * @return
//   */
//  public static double getPixelSize(int zoom, int scale)
//  {
//    return (double)(360.0 / getMaxMapSize(zoom, scale));
//  }
//  //-----------------------------------------------------------------------------
}

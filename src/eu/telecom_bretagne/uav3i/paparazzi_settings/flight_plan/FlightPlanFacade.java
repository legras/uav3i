package eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.Block;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.FlightPlan;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.Waypoint;

/**
 * Classe "facade" pour un accès plus aisé aux informations du plan de vol XML. Le
 * plan de vol à pris en compte est celui défini par l'entrée <code>PAPARAZZI_FLIGHT_PLAN</code>
 * du fichier <code>uav3i.properties.</code><br/>
 * La classe utilise le mécanisme de désérialisation JAXB (unmarshalling) pour l'accès
 * à ces informations. Le schéma XML ayant servi à la génération des classes JAXB a été
 * généré à partir de la DTD (<code>flight_plan.dtd</code>) de la version 5.02 de
 * Paparazzi. Voir les fichiers dans le répertoire : <code>Gestion des flight plans/Schéma</code>.
 * 
 * @author Philippe TANGUY
 */
/**
 * @author ptanguy
 *
 */
public class FlightPlanFacade
{
  //-----------------------------------------------------------------------------
  private static FlightPlanFacade instance = null;

  private FlightPlan           flightPlan;
  private Map<String, Integer> wayPointsIndex;
  private List<Waypoint>       waypoints;
  private Map<String, Integer> blocksIndex;
  private List<Block>          blocks;
  private LatLng               startPoint;
  private char                 utmLatitudeZoneLetter;
  private int                  utmLongitudeZoneNumber;
  private int                  maxDistanceFromHome;
  //-----------------------------------------------------------------------------
  /**
   * Constructeur : désérialisation JAXB.
   */
  private FlightPlanFacade()
  {
    try
    {
      // Ouverture du fichier schéma XML
     InputStream xmlStream = new FileInputStream(UAV3iSettings.getPaparazziFlightPlan());
     
     // Désérialisation dans l'arborescence de classes JAXB
     flightPlan = JAXB.unmarshal(xmlStream, FlightPlan.class);
     
     wayPointsIndex = new HashMap<>();
     processWaypoints();
     blocksIndex = new HashMap<>();
     processBlocks();
     processStartPoint();
     
//     // 'name' est un attribut de l'élément racine <flight_plan>
//     System.out.println("FLIGHT PLAN : " + flightPlan.getName());
//     
//     // Affichage des waypoints
//     for(int i=0;i<waypoints.size(); i++)
//       System.out.println("Waypoint [" + i + "] : " + waypoints.get(i).getName());
//
//     // Affichage des blocks
//     for(int i=0;i<blocks.size(); i++)
//       System.out.println("Block [" + i + "] : " + blocks.get(i).getName());
//     System.out.println();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  /**
   * Pattern Singleton : la méthode renvoie l'unique instance de {@link FlightPlanFacade}.
   * @return l'instance de <code>FlightPlanFacade</code>.
   */
  public final static FlightPlanFacade getInstance()
  {
    if (instance == null)
    {
      synchronized(FlightPlanFacade.class)
      {
        if (instance == null)
          instance = new FlightPlanFacade();
      }
    }
    return instance;
  }
  //-----------------------------------------------------------------------------
  /**
   * Récupération de l'index du <code>waypoint</code> (position au sein du fichier
   * XML). C'est ce positionnement qu'il faut transmettre sur le bus Ivy afin qu'il
   * soit pris en compte par Paparazzi.<br/>
   * Le waypoint est retrouvé par l'intermédiaire de son nom (valeur de l'attribut
   * <code>name</code> de l'élément <code>&lt;waypoint&gt;</code>). Il aurait été
   * intéressant de positionner une contrainte d'unicité sur la valeur (type IDREF)
   * ce qui est malheusement impossible : Paparazzi s'attend à trouver un waypoint
   * nommé "1" et un IDREF ne peut commencer par un chiffre...
   * 
   * @see #getBlockIndex(String)
   * @param name nom du waypoint à retrouver.
   * @return position du waypoint dans le fichier XML.
   */
  public int getWaypointsIndex(String name)
  {
    // TODO Contrairement aux "blocks", la valeur de l'index doit être incrémentée de 1. A creuser...
    return wayPointsIndex.get(name) + 1;
  }
  //-----------------------------------------------------------------------------
  /**
  * Récupération de l'index du <code>block</code> (position au sein du fichier
  * XML). C'est ce positionnement qu'il faut transmettre sur le bus Ivy afin qu'il
  * soit pris en compte par Paparazzi.<br/>
  * Même commentaire que {@link #getBlockIndex(String)} sur l'unicité de la valeur du
  * nom.
  *  
  * @see #getWaypointsIndex(String)
  * @param name nom du waypoint à retrouver.
  * @return position du waypoint dans le fichier XML.
   */
  public int getBlockIndex(String name)
  {
    return blocksIndex.get(name);
  }
  //-----------------------------------------------------------------------------
  /**
   * Récupération du <code>Waypoint</code> dont on connait l'index.
   * 
   * @see #getBlock(int)
   * @param index position du <code>Waypoint</code> dans le fichier XML.
   * @return instance du <code>Waypoint</code>.
   */
  public Waypoint getWaypoint(int index)
  {
    return waypoints.get(index);
  }
  //-----------------------------------------------------------------------------
  /**
   * Récupération du <code>Block</code> dont on connait l'index.
   * 
   * @see #getWaypoint(int)
   * @param index position du <code>Block</code> dans le fichier XML.
   * @return instance du <code>Block</code>.
   */
  public Block getBlock(int index)
  {
    return blocks.get(index);
  }
  //-----------------------------------------------------------------------------
  public LatLng getStartPoint()
  {
    return startPoint;
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la bande UTM (Latitude Zone) du point initial du plan de vol. Ex : 
   * Brest, Rotterdam et le Kent sont dans le fuseau 'U'<br/>
   * Les bandes sont codées par une lettre allant de C à X (à l'exception des
   * lettres I et O qui pourraient être confondues avec les chiffres 1 et 0).<br/>
   * Voir <a ref="http://gerssat.chez-alice.fr/liens/tutdroitref/UTM.htm">http://gerssat.chez-alice.fr/liens/tutdroitref/UTM.htm</a>
   * 
   * @return la lettre représentant le fuseau.
   */
  public char getUTMLatitudeZoneLetterFlightPlan()
  {
    return utmLatitudeZoneLetter;
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie le fuseau UTM (Longitude Zone) du point initial du plan de vol. Ex : 
   * Brest est dans le fuseau 30, Rotterdam et le Kent sont dans le fuseau 31<br/>
   * Les fuseaux sont numérotés d'ouest en est de 1 à 60 avec une amplitude de 6°
   * en longitude (360 = 60 x 6) en partant du méridien 180°.<br/> 
   * Voir <a ref="http://gerssat.chez-alice.fr/liens/tutdroitref/UTM.htm">http://gerssat.chez-alice.fr/liens/tutdroitref/UTM.htm</a>
   * 
   * @return le nombre représentant la bande UTM.
   */
  public int getUTMLongitudeZoneNumber()
  {
    return utmLongitudeZoneNumber;
  }
  //-----------------------------------------------------------------------------
  /**
   * Renvoie la distance maximale autorisée en mètres à partir du point de départ. Cette
   * distance définit le rayon d'un cercle au-delà duquel le drone n'a pas le droit d'évoluer.
   * 
   * @return distance en mètres 
   */
  public int getMaxDistanceFromHome()
  {
    return maxDistanceFromHome;
  }
  //-----------------------------------------------------------------------------

  
  
  

  
  
  //-----------------------------------------------------------------------------
  /**
   * Renseigne la {@link Map} qui contient l'équivalence nom du <code>Waypoint</code>
   * <-> index.
   */
  private void processWaypoints()
  {
    waypoints = flightPlan.getWaypoints().getWaypoint();
    for(int i=0; i<waypoints.size(); i++)
      wayPointsIndex.put(waypoints.get(i).getName(), i);
  }
  //-----------------------------------------------------------------------------
  /**
   * Renseigne la {@link Map} qui contient l'équivalence nom du <code>Block</code>
   * <-> index.
   */
  private void processBlocks()
  {
    blocks = flightPlan.getBlocks().getBlock();
    for(int i=0; i<blocks.size(); i++)
      blocksIndex.put(blocks.get(i).getName(), i);
  }
  //-----------------------------------------------------------------------------
  private void processStartPoint()
  {
    startPoint = new LatLng(Double.parseDouble(flightPlan.getLat0()),
                            Double.parseDouble(flightPlan.getLon0()));
    //utmLatitudeZoneLetter = UTMRef.getUTMLatitudeZoneLetter(startPoint.getLat());
    UTMRef utm = startPoint.toUTMRef();
    utmLatitudeZoneLetter  = utm.getLatZone();
    utmLongitudeZoneNumber = utm.getLngZone();
    maxDistanceFromHome = Integer.parseInt(flightPlan.getMaxDistFromHome());
    System.out.println("Start point = " + startPoint +  " (utmLongitudeZoneNumber = " + utmLongitudeZoneNumber + ") (UTMLatitudeZoneLetter = " + utmLatitudeZoneLetter + ") " + utm);
    System.out.println("maxDistanceFromHome = " + maxDistanceFromHome);
    //UTMRef maxNorthPoint = 
    
    
  }
  //-----------------------------------------------------------------------------
}

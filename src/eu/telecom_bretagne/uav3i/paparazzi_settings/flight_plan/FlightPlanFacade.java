package eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.Block;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.FlightPlan;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.jaxb.Waypoint;

public class FlightPlanFacade
{
  //-----------------------------------------------------------------------------
  private static FlightPlanFacade instance = null;

  private FlightPlan           flightPlan;
  private Map<String, Integer> wayPointsIndex;
  private List<Waypoint>       waypoints;
  private Map<String, Integer> blocksIndex;
  private List<Block>          blocks;
  //-----------------------------------------------------------------------------
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
  public int getWaypointsIndex(String name)
  {
    // TODO Contrairement aux "blocks", la valeur de l'index doit être incrémentée de 1. A creuser...
    return wayPointsIndex.get(name) + 1;
  }
  //-----------------------------------------------------------------------------
  public int getBlockIndex(String name)
  {
    return blocksIndex.get(name);
  }
  //-----------------------------------------------------------------------------
  public Waypoint getWaypoint(int index)
  {
    return waypoints.get(index);
  }
  //-----------------------------------------------------------------------------
  public Block getBlock(int index)
  {
    return blocks.get(index);
  }
  //-----------------------------------------------------------------------------
  private void processWaypoints()
  {
    waypoints = flightPlan.getWaypoints().getWaypoint();
    for(int i=0; i<waypoints.size(); i++)
      wayPointsIndex.put(waypoints.get(i).getName(), i);
  }
  //-----------------------------------------------------------------------------
  private void processBlocks()
  {
    blocks = flightPlan.getBlocks().getBlock();
    for(int i=0; i<blocks.size(); i++)
      blocksIndex.put(blocks.get(i).getName(), i);
  }
  //-----------------------------------------------------------------------------
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
}

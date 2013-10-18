package eu.telecom_bretagne.uav3i.flight_plan;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import eu.telecom_bretagne.uav3i.flight_plan.jaxb.Block;
import eu.telecom_bretagne.uav3i.flight_plan.jaxb.FlightPlan;
import eu.telecom_bretagne.uav3i.flight_plan.jaxb.Waypoint;

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
     InputStream xmlStream = new FileInputStream("flight_plan_lanveoc.xml");
     
     // Désérialisation dans l'arborescence de classes JAXB
     flightPlan = JAXB.unmarshal(xmlStream, FlightPlan.class);
     
     wayPointsIndex = new HashMap<>();
     processWaypoints();
     blocksIndex = new HashMap<>();
     processBlocks();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public int getWaypointsIndex(String name)
  {
    return wayPointsIndex.get(name);
  }
  //-----------------------------------------------------------------------------
  public int getBlockIndex(String name)
  {
    return blocksIndex.get(name);
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

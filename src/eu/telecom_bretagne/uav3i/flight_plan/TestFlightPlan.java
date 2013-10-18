package eu.telecom_bretagne.uav3i.flight_plan;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import eu.telecom_bretagne.uav3i.flight_plan.jaxb.Block;
import eu.telecom_bretagne.uav3i.flight_plan.jaxb.FlightPlan;
import eu.telecom_bretagne.uav3i.flight_plan.jaxb.Waypoint;

public class TestFlightPlan
{
  public TestFlightPlan()
  {
    printFlightPlan("../Exemples_XML/corsica.xml");
    printFlightPlan("../Exemples_XML/douarnenez_bis.xml");
    printFlightPlan("../Exemples_XML/form_leader.xml");
    printFlightPlan("../Exemples_XML/huit.xml");
    printFlightPlan("../Exemples_XML/lanveoc_bis.xml");
    
  }
  
  private void printFlightPlan(String name)
  {
    // Ouverture du fichier XML (voir pour autre chose que getResourceAsStream)
    InputStream xmlStream = FlightPlan.class.getResourceAsStream(name); 
    
    // Désérialisation dans l'arborescence de classes JAXB
    FlightPlan flightPlan = JAXB.unmarshal(xmlStream, FlightPlan.class);
    
    // 'name' est un attribut de l'élément racine <flight_plan>
    System.out.println("FLIGHT PLAN : " + flightPlan.getName());
    
    // Récup et affichage des waypoints
    List<Waypoint> waypoints = flightPlan.getWaypoints().getWaypoint();
    for(Waypoint wp : waypoints)
      System.out.println("Waypoint --> " + wp.getName());

    // Récup et affichage des blocks
    List<Block> blocks = flightPlan.getBlocks().getBlock();
    //for(Block b : blocks)
    for(int i=0;i<blocks.size(); i++)
      System.out.println("Block --> " + i + " - " + blocks.get(i).getName());
    System.out.println();
    
  }

  public static void main(String[] args)
  {
    new TestFlightPlan();
  }

}

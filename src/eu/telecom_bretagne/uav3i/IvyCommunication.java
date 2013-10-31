package eu.telecom_bretagne.uav3i;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVDataStore;

import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.FlightPlanFacade;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Classe assurant la communication avec la plateforme Paparazzi via le bus Ivy. Cette classe
 * communique avec les données stockées dans le fichier de plan de vol (qui définit
 * essentiellement des waypoints et des blocks [templates de vol]).
 * 
 * @author Philippe TANGUY
 */
public class IvyCommunication
{
  //-----------------------------------------------------------------------------
  private String applicationName = "uav3i";
  private Ivy    bus;
  //-----------------------------------------------------------------------------
  public IvyCommunication()
  {
    try
    {
      initializeIvy();
    }
    catch (IvyException e)
    {
      e.printStackTrace();
    }
    
  }
  //-----------------------------------------------------------------------------
  /**
   * Initialize the connection to the Ivy bus.
   * @throws IvyException 
   */
  private void initializeIvy() throws IvyException
  {
    // initialization, name and ready message
    bus = new Ivy(applicationName,
                  applicationName + " Ready",
                  null);
    bus.start(UAV3iSettings.getIvyDomainBus());

		// Messages WORLD_ENV_REQ -> demande de la part de la simu à destination
		// de Gaia pour connaître les conditions météo au point où se trouve le
		// drone.
    //bus.bindMsg("(.*)WORLD_ENV_REQ(.*)", uavPositionListener);
    
    // Mise en écoute des messages GPS
    // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
    bus.bindMsg("(.*)GPS(.*)", new UAVPositionListener());
  }
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour du rayon (en mètres ?) pour le vol circulaire.
   * @param radius rayon en mètres.
   */
  public void setNavRadius(double radius)
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
    sendMsg("dl DL_SETTING 5 6 " + radius);
  }
  //-----------------------------------------------------------------------------
  /**
   * Déplacement d'un waypoint défini dans le plan de vol.
   * 
   * @param waypointName nom du waypoint (attribut <code>name</code> de l'élément <code>&lt;waypoint&gt:</code>).
   * @param coordinate nouvelles coordonnées en {@link LatLng} du point.
   */
  public void moveWayPoint(String waypointName, LatLng coordinate)
  {
    //System.out.println("---------------------> moveWayPoint(" + waypointName + ", " + coordinate + ")");
    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");
  }
  //-----------------------------------------------------------------------------
  /**
   * Demande d'exécution d'un template défini dans le plan de vol.
   * 
   * @param blockName nom du template de vol à exécuter.
   */
  public void jumpToBlock(String blockName)
  {
    //System.out.println("---------------------> jumpToBlock(" + blockName + ")");
    sendMsg("gcs JUMP_TO_BLOCK 5 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
  }
  //-----------------------------------------------------------------------------
  /**
   * Envoi du message sur le bus Ivy.
   * @param message message à envoyer.
   */
  private void sendMsg(String message)
  {
    try { bus.sendMsg(message); }
    catch (IvyException e) { e.printStackTrace(); }
  }
  //-----------------------------------------------------------------------------
  
  
  
  

  
  
  //-----------------------------------------------------------------------------
  private class UAVPositionListener implements IvyMessageListener
  {
    @Override
    public void receive(IvyClient client, String[] args)
    {
    	//System.out.println("Longueur du tableau args = " + args.length);
    	//for(int i=0; i<args.length; i++)
    	//	System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
			
			String tokens = args[1];

			//    <message name="GPS" ID="8">
      //      <field name="mode" type="uint8" unit="byte_mask"/>
      //      <field name="utm_east" type="int32" unit="cm"/>       2
      //      <field name="utm_north" type="int32" unit="cm"/>      3
      //      <field name="course" type="int16" unit="decideg"/>    4
      //      <field name="alt" type="int32" unit="cm"/>            5
      //      <field name="speed" type="uint16" unit="cm/s"/>
      //      <field name="climb" type="int16" unit="cm/s"/>
      //      <field name="itow" type="uint32" unit="ms"/>          8 Time ?
      //      <field name="utm_zone" type="uint8"/>                 9 Erreur ?
      //      <field name="gps_nb_err" type="uint8"/>
      //    </message>
      // Il doit y avoir une erreur dans la doc !!! A l'évidence le 8ème n'est pas le temps/

			String[] message = tokens.split(" ");
			//for(int i=0; i<message.length; i++)
			//	System.out.println("---------------> " + i + " = " + message[i]);

			// Les messages "GPS_SOL" passe par le pattern, on les filtre ici. 
			if(message[0].equals("_SOL")) return;

//    System.out.print("---------------> ");
//    for(int i=0; i<message.length; i++)
//      System.out.print("["+i+" = " + message[i] + "] ");
//    System.out.println();

    // ---------------> [0 = ] [1 = 3] [2 = 72344664] [3 = 532066912] [4 = 932] [5 = 75826] [6 = 1443] [7 = 447] [8 = 0] [9 = 127047240] [10 = 30] [11 = 8]
    
    UAVDataStore.addUAVDataPoint(Integer.parseInt(message[2]),  // utmEast
                                 Integer.parseInt(message[3]),  // utmNorth
                                 Integer.parseInt(message[10]), // utm_zone
                                 Integer.parseInt(message[4]),  // course
                                 Integer.parseInt(message[5]),  // alt
                                 Long.parseLong(message[9]));   // t                

  		//System.out.println();
    }
  }
  //-----------------------------------------------------------------------------
}

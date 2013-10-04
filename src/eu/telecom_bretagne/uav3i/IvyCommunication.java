package eu.telecom_bretagne.uav3i;

import com.deev.interaction.uav3i.model.UAVDataStore;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Tagada tsoin tsoin...
 * 
 * @author Philippe TANGUY
 */
public class IvyCommunication
{
  //-----------------------------------------------------------------------------
  private String applicationName = "uav3i";
  
  private Ivy                 bus;
  private UAVPositionListener uavPositionListener;
  //-----------------------------------------------------------------------------
  //public IvyCommunication(GoogleMapManager mapManager, MapZone mapZone)
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
   * Initailize the connection to the Ivy bus.
   * @throws IvyException 
   */
  private void initializeIvy() throws IvyException
  {
    // initialization, name and ready message
    bus = new Ivy(applicationName,
                  applicationName + " Ready",
                  null);
    bus.start(null);

    // Initialisation of the main Ivy listener.
    uavPositionListener = new UAVPositionListener();
    
		// Messages WORLD_ENV_REQ -> demande de la part de la simu à destination
		// de Gaia pour connaître les conditions météo au point où se trouve le
		// drone.
    //bus.bindMsg("(.*)WORLD_ENV_REQ(.*)", uavPositionListener);
    
    // Mise en écoute des messages GPS
    // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
    bus.bindMsg("(.*)GPS(.*)", uavPositionListener);
  }
  //-----------------------------------------------------------------------------
//  public void moveWayPointS1(GoogleMapCoordinate coordinate)
//  {
//    try
//    {
//      System.out.println("-------------> S1 = " + coordinate);
//      bus.sendMsg("gcs MOVE_WAYPOINT 5 6 "+coordinate.latitude+" "+coordinate.longitude+" 100.000000");
//    }
//    catch (IvyException e)
//    {
//      e.printStackTrace();
//    }
//    
//  }
  //-----------------------------------------------------------------------------
//  public void moveWayPointS2(GoogleMapCoordinate coordinate)
//  {
//    try
//    {
//      System.out.println("-------------> S2 = " + coordinate);
//      bus.sendMsg("gcs MOVE_WAYPOINT 5 7 "+coordinate.latitude+" "+coordinate.longitude+" 100.000000");
//    }
//    catch (IvyException e)
//    {
//      e.printStackTrace();
//    }
//    
//  }
  //-----------------------------------------------------------------------------
  public void jumpToSurveyS1S2()
  {
    try
    {
      // Attention, pour le moment le saut vers le bloc est codé en dur...
      bus.sendMsg("gcs JUMP_TO_BLOCK 5 5");
    }
    catch (IvyException e)
    {
      e.printStackTrace();
    }
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
			String[] message = tokens.split(" ");
			//for(int i=0; i<message.length; i++)
			//	System.out.println("---------------> " + i + " = " + message[i]);

			// Les messages "GPS_SOL" passe par le pattern, on les filtre ici. 
			if(message[0].equals("_SOL")) return;

      int utmEast  = Integer.parseInt(message[2]);
      int utmNorth = Integer.parseInt(message[3]);
      int course   = Integer.parseInt(message[4]);
      int alt      = Integer.parseInt(message[5]);
      long t       = Long.parseLong(message[9]);
      
      UAVDataStore.addUAVDataPoint(utmEast, utmNorth, course, alt, t);

  		//System.out.println();
    }
  }
  //-----------------------------------------------------------------------------
}

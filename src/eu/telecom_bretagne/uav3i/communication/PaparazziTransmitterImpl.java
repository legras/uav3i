package eu.telecom_bretagne.uav3i.communication;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import uk.me.jstott.jcoord.LatLng;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.FlightPlanFacade;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

public class PaparazziTransmitterImpl implements IPaparazziTransmitter
{
  //-----------------------------------------------------------------------------
  private String            applicationName = "uav3i - PaparazziTransmitter";
  private Ivy               bus;
  private IUav3iTransmitter uav3iTransmitter;
  //-----------------------------------------------------------------------------
  //public PaparazziTransmitterImpl(IUav3iTransmitter uav3iTransmitter) throws IvyException, RemoteException
  public PaparazziTransmitterImpl() throws IvyException, RemoteException
  {
    super();
    //this.uav3iTransmitter = uav3iTransmitter;
    initializeIvy();
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
    //bus.start(UAV3iSettings.getIvyDomainBus());
    bus.start(null);

    
//    // Mise en écoute des messages GPS
//    // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
//    bus.bindMsg("(.*)GPS(.*)", new UAVPositionListener());
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#setNavRadius(double)
   */
  @Override
  public void setNavRadius(double radius) throws RemoteException
  {
    System.out.println("---------------------> setNavRadius(" + radius + ")");
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
    sendMsg("dl DL_SETTING 5 6 " + radius);
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#moveWayPoint(java.lang.String, uk.me.jstott.jcoord.LatLng)
   */
  @Override
  public void moveWayPoint(String waypointName, LatLng coordinate)
      throws RemoteException
  {
    System.out.println("---------------------> moveWayPoint(" + waypointName + ", " + coordinate + ")");
    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#jumpToBlock(java.lang.String)
   */
  @Override
  public void jumpToBlock(String blockName) throws RemoteException
  {
    System.out.println("---------------------> jumpToBlock(" + blockName + ")");
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
  @Override
  public void connect(String hostname, int port)  throws RemoteException
  {
    System.out.println("####### Demande de connexion de " + hostname + ":" + port);
    // Connexion en tant que client : PaparazziTransmitter se connecte à uav3i.
    Registry remoteRegistry = LocateRegistry.getRegistry(hostname, port);
    try
    {
      uav3iTransmitter  = (IUav3iTransmitter) remoteRegistry.lookup("Uav3iTransmitter");

      // Mise en écoute des messages GPS
      // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
      bus.bindMsg("(.*)GPS(.*)", new UAVPositionListener());
    }
    catch (NotBoundException | IvyException e)
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
      //  System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
      
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
      //  System.out.println("---------------> " + i + " = " + message[i]);

      // Les messages "GPS_SOL" passe par le pattern, on les filtre ici. 
      if(message[0].equals("_SOL")) return;

//    System.out.print("---------------> ");
//    for(int i=0; i<message.length; i++)
//      System.out.print("["+i+" = " + message[i] + "] ");
//    System.out.println();

      // ---------------> [0 = ] [1 = 3] [2 = 72344664] [3 = 532066912] [4 = 932] [5 = 75826] [6 = 1443] [7 = 447] [8 = 0] [9 = 127047240] [10 = 30] [11 = 8]
    
      try
      {
        uav3iTransmitter.addUAVDataPoint(Integer.parseInt(message[2]),  // utmEast
                                         Integer.parseInt(message[3]),  // utmNorth
                                         Integer.parseInt(message[10]), // utm_zone
                                         Integer.parseInt(message[4]),  // course
                                         Integer.parseInt(message[5]),  // alt
                                         Long.parseLong(message[9]));   // t
      }
      catch (RemoteException e)
      {
        e.printStackTrace();
      }

      //System.out.println();
    }
  }
  //-----------------------------------------------------------------------------
}

package eu.telecom_bretagne.uav3i.communication.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import uk.me.jstott.jcoord.LatLng;
import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.communication.UAVPositionListener;
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
      uav3iTransmitter  = (IUav3iTransmitter) remoteRegistry.lookup(UAV3iSettings.getUav3iServerServiceName());

      // Mise en écoute des messages GPS
      // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
      bus.bindMsg("(.*)GPS(.*)", new UAVPositionListener(uav3iTransmitter));
    }
    catch (NotBoundException | IvyException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

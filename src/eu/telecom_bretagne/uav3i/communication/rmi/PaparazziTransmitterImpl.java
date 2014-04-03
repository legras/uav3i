package eu.telecom_bretagne.uav3i.communication.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import uk.me.jstott.jcoord.LatLng;
import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.communication.dto.ManoeuverDTO;
import eu.telecom_bretagne.uav3i.communication.UAVPositionListener;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.FlightPlanFacade;
import eu.telecom_bretagne.uav3i.util.log.LoggerUtil;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

public class PaparazziTransmitterImpl implements IPaparazziTransmitter
{
  //-----------------------------------------------------------------------------
  private String              applicationName = "uav3i (PT)";
  private Ivy                 bus;
  private IUav3iTransmitter   uav3iTransmitter;
//  private String              uav3iHostname;
//  private int                 uav3iPort;
  private UAVPositionListener uavPositionListener = null;
  //-----------------------------------------------------------------------------
  public PaparazziTransmitterImpl() throws IvyException, RemoteException
  {
    super();
    initializeIvy();
    new Thread(new Uav3iSupervizor()).start();
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
    uavPositionListener = new UAVPositionListener();
    LoggerUtil.LOG.config("Ivy initialized");
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#setNavRadius(double)
   */
  @Override
  public void setNavRadius(double radius) throws RemoteException
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
    sendMsg("dl DL_SETTING 5 6 " + radius);
    LoggerUtil.LOG.info("setNavRadius(" + radius + ") - Message sent to Ivy bus");
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#moveWayPoint(java.lang.String, uk.me.jstott.jcoord.LatLng)
   */
  @Override
  public void moveWayPoint(String waypointName, LatLng coordinate)
      throws RemoteException
  {
    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");
    LoggerUtil.LOG.info("moveWayPoint(" + waypointName + ", " + coordinate + ") - Message sent to Ivy bus");
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see eu.telecom_bretagne.uav3i.communication.IPaparazziTransmitter#jumpToBlock(java.lang.String)
   */
  @Override
  public void jumpToBlock(String blockName) throws RemoteException
  {
    sendMsg("gcs JUMP_TO_BLOCK 5 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
    LoggerUtil.LOG.info("jumpToBlock(" + blockName + ") - Message sent to Ivy bus");
  }
  //-----------------------------------------------------------------------------
  @Override
  public boolean submitManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException
  {
    LoggerUtil.LOG.info("submitManoeuver("+mnvrDTO+")");
    return true;
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
  public void register(String uav3iHostname, int uav3iPort)  throws RemoteException
  {
    try
    {
      bus.start(null);
    }
    catch (IvyException e1)
    {
      LoggerUtil.LOG.severe(e1.getMessage());
      e1.printStackTrace();
    }

    LoggerUtil.LOG.info("Demande d'enregistrement de " + uav3iHostname + ":" + uav3iPort);
    try
    {
      // Connexion en tant que client : PaparazziTransmitter se connecte à uav3i.
      Registry remoteRegistry = LocateRegistry.getRegistry(uav3iHostname, uav3iPort);
      uav3iTransmitter  = (IUav3iTransmitter) remoteRegistry.lookup(UAV3iSettings.getUav3iServerServiceName());

      // Mise à jour du proxy dans le listener. En cas de déconnexion/reconnexion d'uav3i,
      // le proxy change, on ne peut donc pas l'initialiser une fois pour toute.
      uavPositionListener.setUav3iTransmitter(uav3iTransmitter);
      // Mise en écoute des messages GPS
      // TODO Attention, les message de type GPS_SOL sont aussi filtrés par le pattern !
      bus.bindMsg("(.*)GPS(.*)", uavPositionListener);
    }
    catch (NotBoundException | IvyException e)
    {
      LoggerUtil.LOG.severe(e.getMessage());
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public void unRegisterUav3iTransmitter()
  {
    uav3iTransmitter = null;
    bus.stop();
    LoggerUtil.LOG.info("unRegisterUav3iTransmitter()");

  }
  //-----------------------------------------------------------------------------


  
  
  
  
  
  //-----------------------------------------------------------------------------
  /**
   * The Supervisor class (a Runnable class) monitors the remote client reference
   * in order to know if it is alive (by the remote call of the "ping" method on
   * the client side). If not, the client is unregistered (value of remoteClient
   * becomes null).
   * 
   * @author Philippe TANGUY (Télécom Bretagne)
   */
  private class Uav3iSupervizor implements Runnable
  {
    private long delay = 1000;
    @Override
    public void run()
    {
      while (true)
      {
        try
        {
          if(uav3iTransmitter != null)
          {
            uav3iTransmitter.ping();
          }
        }
        catch (RemoteException e)
        {
          //System.out.println("####### Ping a échoué sur uav3iTransmitter");
          unRegisterUav3iTransmitter();
        }
        //System.out.println("####### Uav3iSupervizor is alive!");

        try { Thread.sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }
      }
    }
  }
  //-----------------------------------------------------------------------------
}

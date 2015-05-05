package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.Mode;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.airframe.AirframeFacade;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.dto.BoxMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.CircleMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.LineMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.communication.uavListener.UAVFlightParamsListener;
import com.deev.interaction.uav3i.veto.communication.uavListener.UAVNavStatusListener;
import com.deev.interaction.uav3i.veto.communication.uavListener.UAVPositionListener;
import com.deev.interaction.uav3i.veto.communication.uavListener.UAVPositionListenerRotorcraft;
import com.deev.interaction.uav3i.veto.communication.uavListener.UAVWayPointsListener;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

public class PaparazziTransmitterImpl implements IPaparazziTransmitter
{
  //-----------------------------------------------------------------------------
  private        String                  applicationName = "uav3i (PT)";
  private        Ivy                     bus;
  private static IUav3iTransmitter       uav3iTransmitter;
  private        UAVPositionListener           uavPositionListener           = null;
  private        UAVPositionListenerRotorcraft uavPositionListenerRotorcraft = null;
  private        UAVFlightParamsListener       uavFlightParamsListener       = null;
  private        UAVWayPointsListener          uavWayPointsListener          = null;
  private        UAVNavStatusListener          uavNavStatusListener          = null;
  
  private static PaparazziTransmitterImpl instance;
  //-----------------------------------------------------------------------------
  private PaparazziTransmitterImpl() throws IvyException, RemoteException
  {
    super();
    initializeIvy();
    //UAVModel.initialize();
    new Thread(new Uav3iSupervizor()).start();
  }
  //-----------------------------------------------------------------------------
  public static PaparazziTransmitterImpl getInstance() throws RemoteException, IvyException
  {
    if(instance == null)
      instance = new PaparazziTransmitterImpl();
    return instance;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void communicateManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException
  {
    LoggerUtil.LOG.info("communicateManoeuver("+mnvrDTO+")");
    Veto.getSymbolMapVeto().addManoeuver(mnvrDTO);
    Veto.centerManoeuverOnMap(mnvrDTO);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(int idMnvr) throws RemoteException
  {
    // Comme les boutons ne sont pas 'Serializable' (et qu'on n'en a rien à
    // faire côté table), on les ajoute sur la manoeuvre une fois qu'elle
    // arrive sur le Veto. Comme cette manoeuvre (même si c'est la même) est une
    // copie, on récupère celle précédemment transmise lors du 'communicateManoeuver'
    // pour lui ajouter les boutons.
    // TODO : il n'est pas utile de transmettre l'intance de la manoeuvre, seul son id est suffisant.
    ManoeuverDTO mDTO = Veto.getSymbolMapVeto().getSharedManoeuver();  
    if(mDTO != null)
    {
      if(mDTO.getId() == idMnvr)
      {
        if(UAV3iSettings.getMode() == Mode.VETO)
        {
          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") asked");
          mDTO.addButtons();
          mDTO.setRequestedStatus(ManoeuverRequestedStatus.ASKED);
        }
        else if(UAV3iSettings.getMode() == Mode.VETO_AUTO)
        {
          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") automaticaly accepted");
          // On transmet à la table le résultat de l'évaluation de la manoeuvre
          // par l'opérateur Paparazzi pour mise à jour de l'affichage.
          instance.getUav3iTransmitter().resultAskExecution(idMnvr, true);
          // On met à jour localement le statut de la manoeuvre pour mise
          // à jour de l'affichage sur le Veto.
          mDTO.setRequestedStatus(ManoeuverRequestedStatus.ACCEPTED);
          // On lance l'exécution de la manoeuvre.
          instance.startManoeuver(mDTO);
        }
      }
    }
    else
      LoggerUtil.LOG.severe(("Exection of a manoeuver that is not shared : " + idMnvr));
  }
  //-----------------------------------------------------------------------------
  public void startManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException
  {
    switch (mnvrDTO.getClass().getSimpleName())
    {
      case "CircleMnvrDTO":
        CircleMnvrDTO circleMnvrDTO = (CircleMnvrDTO) mnvrDTO;
        // Move way point for circle center.
//        moveWayPoint("CIRCLE_CENTER", circleMnvrDTO.getCenter());
//      jumpToBlock("Circle");
        moveWayPoint("CAM", circleMnvrDTO.getCenter());
        setNavRadius(circleMnvrDTO.getCurrentRadius());
        jumpToBlock("circle CAM");
        break;
      case "LineMnvrDTO":
        LineMnvrDTO lineMnvrDTO = (LineMnvrDTO) mnvrDTO;
        // Circle radius may have previously been modified by a circle
        // manoeuver, set it to default.
        setNavRadius(AirframeFacade.getInstance() .getDefaultCircleRadius());
        // Move way points to each side of the line.
//        moveWayPoint("L1", lineMnvrDTO.getTrajA());
//        moveWayPoint("L2", lineMnvrDTO.getTrajB());
//        jumpToBlock("Line_L1-L2");
        moveWayPoint("p1", lineMnvrDTO.getTrajA());
        moveWayPoint("p2", lineMnvrDTO.getTrajB());
        jumpToBlock("line_p1_p2");
        break;
      case "BoxMnvrDTO":
        BoxMnvrDTO boxMnvr = (BoxMnvrDTO) mnvrDTO;
        // Move way points to each side of the box.
        moveWayPoint("S1", boxMnvr.getBoxA());
        moveWayPoint("S2", boxMnvr.getBoxB());
        // Circle radius may have previously been modified by a circle
        // manoeuver, set it to default.
        setNavRadius(AirframeFacade.getInstance().getDefaultCircleRadius());
        if (boxMnvr.isNorthSouth())
          jumpToBlock("Survey_S1-S2_NS");
        else
          // West-East
          jumpToBlock("Survey_S1-S2_WE");
        break;
    }
    
  }
  //-----------------------------------------------------------------------------
  @Override
  public void clearManoeuver() throws RemoteException
  {
    LoggerUtil.LOG.info("clearManoeuver()");
    Veto.getSymbolMapVeto().clearManoeuver();
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
    uavPositionListener           = new UAVPositionListener();
    uavNavStatusListener          = new UAVNavStatusListener();
    uavPositionListenerRotorcraft = new UAVPositionListenerRotorcraft();
    uavFlightParamsListener       = new UAVFlightParamsListener();
    uavWayPointsListener          = new UAVWayPointsListener();
    LoggerUtil.LOG.config("Ivy initialized");
  }
  //-----------------------------------------------------------------------------
  private void setNavRadius(double radius) throws RemoteException
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
//    sendMsg("dl DL_SETTING 5 6 " + radius);
    
    // Exemple (rotorcraft) : dl DL_SETTING 202 26 34.500000
    sendMsg("dl DL_SETTING 202 26 " + radius);
    LoggerUtil.LOG.info("Message sent to Ivy bus - setNavRadius(" + radius + ")");
  }
  //-----------------------------------------------------------------------------
  private void moveWayPoint(String waypointName, LatLng coordinate) throws RemoteException
  {
//    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");

    // Exemple (rotorcraft) : gcs MOVE_WAYPOINT 202 6 48.3591789 -4.5730567 152.000071
    sendMsg("gcs MOVE_WAYPOINT 202 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 150.000000");
    LoggerUtil.LOG.info("Message sent to Ivy bus - moveWayPoint(" + waypointName + ", " + coordinate + ")");
  }
  //-----------------------------------------------------------------------------
  private void jumpToBlock(String blockName) throws RemoteException
  {
//    sendMsg("gcs JUMP_TO_BLOCK 5 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
    // Exemple (rotorcraft) : gcs JUMP_TO_BLOCK 202 8
    sendMsg("gcs JUMP_TO_BLOCK 202 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
    LoggerUtil.LOG.info("Message sent to Ivy bus - jumpToBlock(" + blockName + ")");
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

      // Mise à jour du proxy dans les listeners. En cas de déconnexion/reconnexion d'uav3i,
      // le proxy change, on ne peut donc pas l'initialiser une fois pour toute.
      uavPositionListener.setUav3iTransmitter(uav3iTransmitter);
      uavFlightParamsListener.setUav3iTransmitter(uav3iTransmitter);
      uavNavStatusListener.setUav3iTransmitter(uav3iTransmitter);
      uavPositionListenerRotorcraft.setUav3iTransmitter(uav3iTransmitter);
      uavPositionListenerRotorcraft.setUavNavStatusListener(uavNavStatusListener);
      uavWayPointsListener.setUav3iTransmitter(uav3iTransmitter);
      
      // Mise en écoute des messages GPS (version 3i)
//      // TODO Attention, les messages de type GPS_SOL sont aussi filtrés par le pattern !
      bus.bindMsg("(.*)GPS(.*)", uavPositionListener);
      
      // Mise en écoute des messages GPS (version Rotorcraft)
      bus.bindMsg("(.*)GPS_INT(.*)", uavPositionListenerRotorcraft);

      // Mise en écoute des messages NAV_STATUS
      bus.bindMsg("(.*) NAV_STATUS(.*)", uavNavStatusListener);

      // Mise en écoute des messages concernant l'altitude et la vitesse ascentionnelle
      bus.bindMsg("(.*)FLIGHT_PARAM(.*)", uavFlightParamsListener);

      // Mise en écoute des messages concernant les waypoints
      bus.bindMsg("(.*)WAYPOINT_MOVED(.*)", uavWayPointsListener);
    }
    catch (NotBoundException | IvyException e)
    {
      LoggerUtil.LOG.severe(e.getMessage());
      e.printStackTrace();
    }
    Veto.setVetoState(VetoState.RECEIVING);
  }
  //-----------------------------------------------------------------------------
  public void unRegisterUav3iTransmitter()
  {
    bus.stop();
    Veto.setVetoState(VetoState.IDLE);
    uav3iTransmitter = null;
    Veto.reinit();
    LoggerUtil.LOG.info("unRegisterUav3iTransmitter()");
  }
  //-----------------------------------------------------------------------------
  public IUav3iTransmitter getUav3iTransmitter()
  {
    return uav3iTransmitter;
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

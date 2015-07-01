package com.deev.interaction.uav3i.veto.communication.websocket;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.VetoMode;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.airframe.AirframeFacade;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.dto.BoxMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.CircleMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.LineMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterResultAskExecutionServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVCamStatusListener;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVFlightParamsListener;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVNavStatusListener;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVPositionListener;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVPositionListenerRotorcraft;
import com.deev.interaction.uav3i.veto.communication.websocket.uavListener.UAVWayPointsListener;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

/**
 * Classe instanciée côté serveur dans le cas d'une communication websocket :<br/>
 * Elle reçoit les demandes en prevenance du client (uav3i/table tactile) et les
 * relaie sur le bus Ivy afin d'être prises en comptes par Paparazzi.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class PaparazziTransmitterWebsocket
{
  //-----------------------------------------------------------------------------
  private          String                        applicationName = "uav3i (PT)";
  private          Ivy                           bus;
  private          UAVPositionListener           uavPositionListener           = null;
  private          UAVPositionListenerRotorcraft uavPositionListenerRotorcraft = null;
  private          UAVFlightParamsListener       uavFlightParamsListener       = null;
  private          UAVWayPointsListener          uavWayPointsListener          = null;
  private          UAVNavStatusListener          uavNavStatusListener          = null;
  private          UAVCamStatusListener          uavCamStatusListener          = null;

  private static PaparazziTransmitterWebsocket instance;
  //-----------------------------------------------------------------------------
  private PaparazziTransmitterWebsocket() throws IvyException
  {
    initializeIvy();
  }
  //-----------------------------------------------------------------------------
  public static PaparazziTransmitterWebsocket getInstance() throws IvyException
  {
    if(instance == null)
      instance = new PaparazziTransmitterWebsocket();
    return instance;
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
    //uavFlightParamsListener       = new UAVFlightParamsListener();
    uavWayPointsListener          = new UAVWayPointsListener();
    uavPositionListenerRotorcraft.setUavNavStatusListener(uavNavStatusListener);
//    uavCamStatusListener          = new UAVCamStatusListener();
    LoggerUtil.LOG.config("Ivy initialized");
  }
  //-----------------------------------------------------------------------------
  public void communicateManoeuver(ManoeuverDTO mnvrDTO)
  {
    LoggerUtil.LOG.info("communicateManoeuver("+mnvrDTO+")");
    Veto.getSymbolMapVeto().addManoeuver(mnvrDTO);
    Veto.centerManoeuverOnMap(mnvrDTO);
  }
  //-----------------------------------------------------------------------------
  public void executeManoeuver(int idMnvr)
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
        if(UAV3iSettings.getVetoMode() == VetoMode.MANUAL)
        {
          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") asked");
          mDTO.addButtons();
          mDTO.setRequestedStatus(ManoeuverRequestedStatus.ASKED);
        }
        else if(UAV3iSettings.getVetoMode() == VetoMode.AUTOMATIC)
        {
          LoggerUtil.LOG.info("executeManoeuver("+mDTO+") automaticaly accepted");
          // On transmet à la table le résultat de l'évaluation de la manoeuvre
          // par l'opérateur Paparazzi pour mise à jour de l'affichage.
          Uav3iTransmitterResultAskExecutionServerEndpoint.resultAskExecution(idMnvr, true);
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
  public void startManoeuver(ManoeuverDTO mnvrDTO)
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
//        setNavRadius(AirframeFacade.getInstance() .getDefaultCircleRadius());
        // TODO: ther is no default circle radius with the BeBop airframe file (bebop.xml). To see with Christophe.
        setNavRadius(5);
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
  public void clearManoeuver()
  {
    LoggerUtil.LOG.info("clearManoeuver()");
    Veto.getSymbolMapVeto().clearManoeuver();
  }
  //-----------------------------------------------------------------------------
  public void register()
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

    try
    {
      Veto2ClientWebsocketFacade.setConnected(true);
      // Mise en écoute des messages GPS (version 3i)
//      // TODO Attention, les messages de type GPS_SOL sont aussi filtrés par le pattern !
      bus.bindMsg("(.*)GPS(.*)", uavPositionListener);
      
      // Mise en écoute des messages GPS (version Rotorcraft)
      bus.bindMsg("(.*)GPS_INT(.*)", uavPositionListenerRotorcraft);

      // Mise en écoute des messages NAV_STATUS
      bus.bindMsg("(.*) NAV_STATUS(.*)", uavNavStatusListener);

//      // Mise en écoute des messages concernant l'altitude et la vitesse ascentionnelle
//      bus.bindMsg("(.*)FLIGHT_PARAM(.*)", uavFlightParamsListener);

      // Mise en écoute des messages concernant les waypoints
      bus.bindMsg("(.*)WAYPOINT_MOVED(.*)", uavWayPointsListener);
      
//      // Mise en écoute des messages CAM_STATUS
//      bus.bindMsg("(.*)CAM_STATUS(.*)", uavCamStatusListener);
    }
    catch (IvyException e)
    {
      LoggerUtil.LOG.severe(e.getMessage());
      e.printStackTrace();
    }
    Veto.setVetoState(VetoState.RECEIVING);
  }
  //-----------------------------------------------------------------------------
  public void unRegister()
  {
    bus.stop();
    Veto.setVetoState(VetoState.IDLE);
    Veto2ClientWebsocketFacade.setConnected(false);
    Veto.reinit();
    LoggerUtil.LOG.info("unRegisterUav3iTransmitter()");
  }
  //-----------------------------------------------------------------------------
  private void setNavRadius(double radius)
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
//    sendMsg("dl DL_SETTING 5 6 " + radius);
    
    // Exemple (rotorcraft) : dl DL_SETTING 202 26 34.500000
    sendMsg("dl DL_SETTING 202 26 " + radius);
    LoggerUtil.LOG.info("Message sent to Ivy bus - setNavRadius(" + radius + ")");
  }
  //-----------------------------------------------------------------------------
  private void moveWayPoint(String waypointName, LatLng coordinate)
  {
//    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");

    // Exemple (rotorcraft) : gcs MOVE_WAYPOINT 202 6 48.3591789 -4.5730567 152.000071
    sendMsg("gcs MOVE_WAYPOINT 202 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 150.000000");
    LoggerUtil.LOG.info("Message sent to Ivy bus - moveWayPoint(" + waypointName + ", " + coordinate + ")");
  }
  //-----------------------------------------------------------------------------
  private void jumpToBlock(String blockName)
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
}

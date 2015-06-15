package com.deev.interaction.uav3i.veto.communication.direct;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.BoxMnvr;
import com.deev.interaction.uav3i.ui.CircleMnvr;
import com.deev.interaction.uav3i.ui.LineMnvr;
import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.airframe.AirframeFacade;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.Client2VetoFacade;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.rmi.uavListener.UAVFlightParamsListener;
import com.deev.interaction.uav3i.veto.communication.rmi.uavListener.UAVPositionListener;
import com.deev.interaction.uav3i.veto.communication.rmi.uavListener.UAVWayPointsListener;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

/**
 * Classe assurant la communication avec la plateforme Paparazzi via le bus Ivy. Cette classe
 * communique avec les données stockées dans le fichier de plan de vol (qui définit
 * essentiellement des waypoints et des blocks [templates de vol]).
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class Client2VetoDirectCommunication extends Client2VetoFacade
{
  //-----------------------------------------------------------------------------
  private String applicationName = "uav3i";
  private Ivy    bus;
  //-----------------------------------------------------------------------------
  public Client2VetoDirectCommunication()
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
  @Override
  public void communicateManoeuver(ManoeuverDTO mnvrDTO)
  {
    // Nothing to do...
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(int idMnvr)
  {
    // Not used in PAPARAZZI_DIRECT mode.
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(Manoeuver mnvr)
  {
    switch (mnvr.getClass().getSimpleName())
    {
      case "CircleMnvr":
        CircleMnvr circleMnvr = (CircleMnvr) mnvr;
        // Move way point for circle center.
        moveWayPoint("CIRCLE_CENTER", circleMnvr.getCenter());
        setNavRadius(circleMnvr.getCurrentRadius());
        jumpToBlock("Circle");
        break;
      case "LineMnvr":
        LineMnvr lineMnvr = (LineMnvr) mnvr;
        // Move way points to each side of the line.
        moveWayPoint("L1", lineMnvr.getTrajA());
        moveWayPoint("L2", lineMnvr.getTrajB());
        // Circle radius may have previously been modified by a circle
        // manoeuver, set it to default.
        setNavRadius(AirframeFacade.getInstance() .getDefaultCircleRadius());
        jumpToBlock("Line_L1-L2");
        break;
      case "BoxMnvr":
        BoxMnvr boxMnvr = (BoxMnvr) mnvr;
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
  public void clearManoeuver()
  {
    // Nothing to do...
  }
  //-----------------------------------------------------------------------------
  private void setNavRadius(double radius)
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
    sendMsg("dl DL_SETTING 5 6 " + radius);
    LoggerUtil.LOG.info("Message sent to Ivy bus - setNavRadius(" + radius + ")");
  }
  //-----------------------------------------------------------------------------
  private void moveWayPoint(String waypointName, LatLng coordinate)
  {
    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");
    LoggerUtil.LOG.info("Message sent to Ivy bus - moveWayPoint(" + waypointName + ", " + coordinate + ")");
  }
  //-----------------------------------------------------------------------------
  private void jumpToBlock(String blockName)
  {
    sendMsg("gcs JUMP_TO_BLOCK 5 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
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
    // Mise en écoute des messages concernant les paramètres de vol
    bus.bindMsg("(.*)FLIGHT_PARAM(.*)", new UAVFlightParamsListener());
    // Mise en écoute des messages concernant les waypoints
    bus.bindMsg("(.*)WAYPOINT_MOVED(.*)", new UAVWayPointsListener());
  }
  //-----------------------------------------------------------------------------
}

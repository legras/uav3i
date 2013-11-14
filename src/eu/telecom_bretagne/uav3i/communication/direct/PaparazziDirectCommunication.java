package eu.telecom_bretagne.uav3i.communication.direct;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVModel;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.communication.PaparazziCommunication;
import eu.telecom_bretagne.uav3i.communication.UAVPositionListener;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.FlightPlanFacade;
import eu.telecom_bretagne.uav3i.util.log.LoggerUtil;
import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Classe assurant la communication avec la plateforme Paparazzi via le bus Ivy. Cette classe
 * communique avec les données stockées dans le fichier de plan de vol (qui définit
 * essentiellement des waypoints et des blocks [templates de vol]).
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class PaparazziDirectCommunication extends PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  private String applicationName = "uav3i";
  private Ivy    bus;
  //-----------------------------------------------------------------------------
  public PaparazziDirectCommunication()
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
  @Override
  public void setNavRadius(double radius)
  {
    // Exemple : dl DL_SETTING 5 6 1000.000000
    // Que veux dire le 6 ?
    sendMsg("dl DL_SETTING 5 6 " + radius);
    LoggerUtil.LOG.info("setNavRadius(" + radius + ") - Message sent to Ivy bus");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void moveWayPoint(String waypointName, LatLng coordinate)
  {
    sendMsg("gcs MOVE_WAYPOINT 5 " + FlightPlanFacade.getInstance().getWaypointsIndex(waypointName) + " " + coordinate.getLat() + " " + coordinate.getLng() + " 100.000000");
    LoggerUtil.LOG.info("moveWayPoint(" + waypointName + ", " + coordinate + ") - Message sent to Ivy bus");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void jumpToBlock(String blockName)
  {
    sendMsg("gcs JUMP_TO_BLOCK 5 " + FlightPlanFacade.getInstance().getBlockIndex(blockName));
    LoggerUtil.LOG.info("jumpToBlock(" + blockName + ") - Message sent to Ivy bus");
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

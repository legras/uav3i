package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.Uav3iTransmitterServerEndpoint;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.IvyClient;

/**
 * Listen the "WAYPOINT_MOVED" message on the Ivy bus.<br/>
 * 
 * Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
 * 
 * <message name="WAYPOINT_MOVED" id="30">
 *   <field name="ac_id" type="string"/>
 *   <field name="wp_id" type="uint8"/>
 *   <field name="lat"  type="float" unit="deg" format="%.7f"/>
 *   <field name="long"  type="float" unit="deg" format="%.7f"/>
 *   <field name="alt" type="float" unit="m"/>
 *   <field name="ground_alt" type="float" unit="m"/>
 * </message>
 * 
 * Paparazzi server    | ground WAYPOINT_MOVED 202 6 48.3592277 -4.5733102 152.000071 147.000000
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVWayPointsListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int indexWP_ID, indexLAT, indexLONG, indexALT;
  //-----------------------------------------------------------------------------
  public UAVWayPointsListener()
  {
    indexWP_ID = IvyMessagesFacade.getInstance().getFieldIndex("WAYPOINT_MOVED", "wp_id");  // Waypoint ID
    indexLAT   = IvyMessagesFacade.getInstance().getFieldIndex("WAYPOINT_MOVED", "lat");    // latitude
    indexLONG  = IvyMessagesFacade.getInstance().getFieldIndex("WAYPOINT_MOVED", "long");   // longitude
    indexALT   = IvyMessagesFacade.getInstance().getFieldIndex("WAYPOINT_MOVED", "alt");    // altitude
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    //displayArgs(this.getClass().getSimpleName(), client, args);

    String tokens = args[1];

    String[] message = tokens.split(" ");
    
    UAVWayPoint wayPoint = new UAVWayPoint(Integer.parseInt(message[indexWP_ID]),
                                           Double.parseDouble(message[indexLAT]),
                                           Double.parseDouble(message[indexLONG]),
                                           Double.parseDouble(message[indexALT]));

    if(UAVModel.getWayPoints().updateWayPoint(wayPoint))  // Mise à jour côté Veto lors du test
    {
      if(Veto.getVetoState() == VetoState.RECEIVING)
      {
        Uav3iTransmitterServerEndpoint.updateWayPoint(wayPoint);
      }
      else
        LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
    }
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import java.io.IOException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.Veto2ClientWebsocketFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddFlightParamsServerEndpoint;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.IvyClient;

/**
 * Listen the "FLIGHT_PARAM" message on the Ivy bus.<br/>
 * 
 * Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
 * 
 * <message name="FLIGHT_PARAM" id="11">
 *   <field name="ac_id"  type="string"/>
 *   <field name="roll"   type="float" unit="deg"/>
 *   <field name="pitch"  type="float" unit="deg"/>
 *   <field name="heading" type="float" unit="deg"/>
 *   <field name="lat"    type="float" unit="deg"/>
 *   <field name="long"   type="float" unit="deg"/>
 *   <field name="speed"  type="float" unit="m/s"/>                  7
 *   <field name="course" type="float" unit="deg" format="%.1f"/>
 *   <field name="alt"    type="float" unit="m"/>                    9
 *   <field name="climb"  type="float" unit="m/s"/>                  10
 *   <field name="agl"    type="float" unit="m"/>                    11
 *   <field name="unix_time"    type="float" unit="s (Unix time)"/>
 *   <field name="itow"   type="uint32" unit="ms"/>
 *   <field name="airspeed" type="float" unit="m/s"/>
 * </message>
 * 
 * Paparazzi server    | ground FLIGHT_PARAM 202 -3.594974 2.839610 215.431670 48.359324 -4.572613 5.091339 356.8 149.969145 -0.009037 2.969145 1430010069.192131 3685750 -1.000000
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVFlightParamsListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int cpt = 0;
  int indexSPEED, indexALT, indexCLIMB, indexAGL;
  //-----------------------------------------------------------------------------
  public UAVFlightParamsListener()
  {
    indexSPEED = IvyMessagesFacade.getInstance().getFieldIndex("FLIGHT_PARAM", "speed"); // ground speed
    indexALT   = IvyMessagesFacade.getInstance().getFieldIndex("FLIGHT_PARAM", "alt");   // altitude
    indexCLIMB = IvyMessagesFacade.getInstance().getFieldIndex("FLIGHT_PARAM", "climb"); // vertical speed
    indexAGL   = IvyMessagesFacade.getInstance().getFieldIndex("FLIGHT_PARAM", "agl");   // ground altitude
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    // On n'envoie les infos qu'une fois sur 10...
    cpt++;
    if(cpt%10 != 0)
      return;
    else
      cpt = 0;

    //displayArgs(this.getClass().getSimpleName(), client, args);

    String tokens = args[1];

    String[] message = tokens.split(" ");

    double altitude       = Double.parseDouble(message[indexALT]);
    double verticalSpeed  = Double.parseDouble(message[indexCLIMB]);
    double groundAltitude = Double.parseDouble(message[indexAGL]);
    double groundSpeed    = Double.parseDouble(message[indexSPEED]);
    String message2Client = altitude + "*" + verticalSpeed + "*" + groundAltitude + "*" + groundSpeed;

    // On transmet via RMI à l'IHM table tactile la position du drone.
    if(Veto2ClientWebsocketFacade.isConnected() && Veto.getVetoState() == VetoState.RECEIVING)
    {
      try
      {
        Uav3iTransmitterAddFlightParamsServerEndpoint.addFlightParams(message2Client);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

      // On transmet aussi les infos à l'IHM Veto pour l'affichage local.
      UAVModel.setAltitude(altitude);
      UAVModel.setVerticalSpeed(verticalSpeed);
      UAVModel.setGroundSpeed(groundSpeed);
      UAVModel.setGroundAltitude(groundAltitude);
    }
    else
      LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
  }
  //-----------------------------------------------------------------------------
}

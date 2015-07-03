package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import java.io.IOException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.Veto2ClientWebsocketFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddUavDataPointServerEndpoint;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.IvyClient;

/**
 * Listen the "GPS_INT" message on the Ivy bus. The listener needs also to transmit the
 * course of the drone but this information is not available in this message. For this
 * purpose, it uses a second listener: {@link UAVNavStatusListener}.<br/>
 * 
 * Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
 * 
 * <message name="GPS_INT" id="155">
 *   <field name="ecef_x"  type="int32" unit="cm"   alt_unit="m"/>
 *   <field name="ecef_y"  type="int32" unit="cm"   alt_unit="m"/>
 *   <field name="ecef_z"  type="int32" unit="cm"   alt_unit="m"/>
 *   <field name="lat"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>                  4
 *   <field name="lon"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>                  5
 *   <field name="alt"     type="int32" unit="mm"   alt_unit="m">altitude above WGS84 reference ellipsoid</field>
 *   <field name="hmsl"    type="int32" unit="mm"   alt_unit="m">height above mean sea level (geoid)</field>      7
 *   <field name="ecef_xd" type="int32" unit="cm/s" alt_unit="m/s"/>
 *   <field name="ecef_yd" type="int32" unit="cm/s" alt_unit="m/s"/>
 *   <field name="ecef_zd" type="int32" unit="cm/s" alt_unit="m/s"/>
 *   <field name="pacc"    type="uint32" unit="cm"   alt_unit="m"/>
 *   <field name="sacc"    type="uint32" unit="cm/s" alt_unit="m/s"/>
 *   <field name="tow"     type="uint32"/>                                                                        13
 *   <field name="pdop"    type="uint16"/>
 *   <field name="numsv"   type="uint8"/>
 *   <field name="fix"     type="uint8" values="NONE|UKN1|UKN2|3D"/>
 * </message>
 *
 * bebop_NPS           | 202 GPS_INT 462759583 11966883 437325680 435639942 14813288 203708 153907 -2 5 2 0 0 145000 0 0 3
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVPositionListenerRotorcraft extends UAVListener
{
  //-----------------------------------------------------------------------------
  private UAVNavStatusListener uavNavStatusListener;
  private UAVCamStatusListener uavCamStatusListener;
  private int indexLAT, indexLON, indexHMSL, indexTOW;
  //-----------------------------------------------------------------------------
  public UAVPositionListenerRotorcraft()
  {
    indexLAT  = IvyMessagesFacade.getInstance().getFieldIndex("GPS_INT", "lat");  // latitude
    indexLON  = IvyMessagesFacade.getInstance().getFieldIndex("GPS_INT", "lon");  // longitude
    indexHMSL = IvyMessagesFacade.getInstance().getFieldIndex("GPS_INT", "hmsl"); // altitude
    indexTOW  = IvyMessagesFacade.getInstance().getFieldIndex("GPS_INT", "tow");  // time
  }
  //-----------------------------------------------------------------------------
  /**
   * @param uavNavStatusListener the {@link UAVNavStatusListener} to set
   */
  public void setUavNavStatusListener(UAVNavStatusListener uavNavStatusListener)
  {
    this.uavNavStatusListener = uavNavStatusListener;
  }
  //-----------------------------------------------------------------------------
  /**
   * @param uavCamStatusListener the {@link UAVCamStatusListener} to set
   */
  public void setUavCamStatusListener(UAVCamStatusListener uavCamStatusListener)
  {
    this.uavCamStatusListener = uavCamStatusListener;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    //displayArgs(this.getClass().getSimpleName(), client, args);
    
    String tokens = args[1];
  
    String[] message = tokens.split(" ");
    
    int    latitude      = Integer.parseInt(message[indexLAT]);
    int    longitude     = Integer.parseInt(message[indexLON]);
    int    course        = uavNavStatusListener.getLastCourseValue();
    int    altitude      = Integer.parseInt(message[indexHMSL]);
    long   time          = Long.parseLong(message[indexTOW]);
    double camTargetLat  = uavCamStatusListener.getCamTargetLat();
    double camTargetLong = uavCamStatusListener.getCamTargetLong();
    
    String message2Client = latitude + "*" + longitude + "*" + course + "*" + altitude + "*" + time + camTargetLat + "*" + camTargetLong;
  
    // On transmet via RMI à l'IHM table tactile la position du drone.
    //if(uav3iTransmitter != null && Veto.getVetoState() == VetoState.RECEIVING)
    if(Veto2ClientWebsocketFacade.isConnected() && Veto.getVetoState() == VetoState.RECEIVING)
    {
      try
      {
        Uav3iTransmitterAddUavDataPointServerEndpoint.addUAVDataPoint("latlon", message2Client);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }

      // On transmet aussi la position du drone à l'IHM Veto pour l'affichage local.
      UAVModel.addUAVDataPoint(latitude, longitude, course, altitude, time);
    }
    else
      LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
  }
  //-----------------------------------------------------------------------------
}

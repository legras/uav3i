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
 * <message name="GPS" id="8">
 *   <field name="mode"       type="uint8"  unit="byte_mask"/>
 *   <field name="utm_east"   type="int32"  unit="cm" alt_unit="m"/>                                           2
 *   <field name="utm_north"  type="int32"  unit="cm" alt_unit="m"/>                                           3
 *   <field name="course"     type="int16"  unit="decideg" alt_unit="deg"/>                                    4
 *   <field name="alt"        type="int32"  unit="mm" alt_unit="m">Altitude above geoid (MSL)</field>          5
 *   <field name="speed"      type="uint16" unit="cm/s" alt_unit="m/s">norm of 2d ground speed in cm/s</field>
 *   <field name="climb"      type="int16"  unit="cm/s" alt_unit="m/s"/>
 *   <field name="week"       type="uint16" unit="weeks"/>
 *   <field name="itow"       type="uint32" unit="ms"/>                                                        9
 *   <field name="utm_zone"   type="uint8"/>                                                                   10
 *   <field name="gps_nb_err" type="uint8"/>
 * </message>
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVPositionListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int indexUTM_EAST, indexUTM_NORTH, indexCOURSE, indexALT, indexITOW, indexUTM_ZONE;
  //-----------------------------------------------------------------------------
  public UAVPositionListener()
  {
    indexUTM_EAST  = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "utm_east");  // utm east
    indexUTM_NORTH = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "utm_north"); // utm north
    indexCOURSE    = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "course");    // course
    indexALT       = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "alt");       // altitude
    indexITOW      = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "itow");      // time
    indexUTM_ZONE  = IvyMessagesFacade.getInstance().getFieldIndex("GPS", "utm_zone");  // utm zone
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    //displayArgs(this.getClass().getSimpleName(), client, args);
    
    String tokens = args[1];

    String[] message = tokens.split(" ");
    
    // FIXME Les messages "GPS_SOL" et GPS_INT passe par le pattern, on les filtre ici. Pas propre...
    if(message[0].equals("_SOL") || message[0].equals("_INT")) return;

    int  utmEast  = Integer.parseInt(message[indexUTM_EAST]);
    int  utmNorth = Integer.parseInt(message[indexUTM_NORTH]);
    int  utmZone  = Integer.parseInt(message[indexUTM_ZONE]);
    int  course   = Integer.parseInt(message[indexCOURSE]);
    int  altitude = Integer.parseInt(message[indexALT]);
    long time     = Long.parseLong(message[indexITOW]);
    
    String message2Client = utmEast + "*" + utmNorth + "*" + utmZone + "*" + course + "*" + altitude + "*" + time;

    
    // On transmet via RMI à l'IHM table tactile la position du drone.
    if(Veto2ClientWebsocketFacade.isConnected() && Veto.getVetoState() == VetoState.RECEIVING)
    {
      try
      {
        Uav3iTransmitterAddUavDataPointServerEndpoint.addUAVDataPoint("utm", message2Client);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      
      // On transmet aussi la position du drone à l'IHM Veto pour l'affichage local.
      UAVModel.addUAVDataPoint(utmEast, utmNorth, utmZone, course, altitude, time);
    }
    else
      LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.uavListener;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.IvyClient;

/**
 * <samp>
<message name="GPS_INT" id="155">
sdqsd
qsdqsd</samp>
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVPositionListenerRotorcraft extends UAVListener
{
  //-----------------------------------------------------------------------------
  private UAVNavStatusListener uavNavStatusListener;
  //-----------------------------------------------------------------------------
  /**
   * @param uavNavStatusListener the uavNavStatusListener to set
   */
  public void setUavNavStatusListener(UAVNavStatusListener uavNavStatusListener)
  {
    this.uavNavStatusListener = uavNavStatusListener;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    displayArgs(client, args);
    
    String tokens = args[1];
  
    // Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
    //
    // <message name="GPS_INT" id="155">
    //   <field name="ecef_x"  type="int32" unit="cm"   alt_unit="m"/>                                                //
    //   <field name="ecef_y"  type="int32" unit="cm"   alt_unit="m"/>                                                //
    //   <field name="ecef_z"  type="int32" unit="cm"   alt_unit="m"/>                                                //
    //   <field name="lat"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>                  // 4
    //   <field name="lon"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>                  // 5
    //   <field name="alt"     type="int32" unit="mm"   alt_unit="m">altitude above WGS84 reference ellipsoid</field> //
    //   <field name="hmsl"    type="int32" unit="mm"   alt_unit="m">height above mean sea level (geoid)</field>      // 7
    //   <field name="ecef_xd" type="int32" unit="cm/s" alt_unit="m/s"/>                                              //
    //   <field name="ecef_yd" type="int32" unit="cm/s" alt_unit="m/s"/>                                              //
    //   <field name="ecef_zd" type="int32" unit="cm/s" alt_unit="m/s"/>                                              //
    //   <field name="pacc"    type="uint32" unit="cm"   alt_unit="m"/>                                               //
    //   <field name="sacc"    type="uint32" unit="cm/s" alt_unit="m/s"/>                                             //
    //   <field name="tow"     type="uint32"/>                                                                        // 13
    //   <field name="pdop"    type="uint16"/>                                                                        //
    //   <field name="numsv"   type="uint8"/>                                                                         //
    //   <field name="fix"     type="uint8" values="NONE|UKN1|UKN2|3D"/>                                              //
    // </message>
    //
    // bebop_NPS           | 202 GPS_INT 462759583 11966883 437325680 435639942 14813288 203708 153907 -2 5 2 0 0 145000 0 0 3
    String[] message = tokens.split(" ");
  //  for(int i=0; i<message.length; i++)
  //    System.out.println("---------------> " + i + " = " + message[i]);
  //      ---------------> 0 = 
  //      ---------------> 1 = 423254542
  //      ---------------> 2 = -33857543
  //      ---------------> 3 = 474366991
  //      ---------------> 4 = 483594016
  //      ---------------> 5 = -45735420
  //      ---------------> 6 = 195739
  //      ---------------> 7 = 147090
  //      ---------------> 8 = 0
  //      ---------------> 9 = 0
  //      ---------------> 10 = 0
  //      ---------------> 11 = 0
  //      ---------------> 12 = 0
  //      ---------------> 13 = 62250
  //      ---------------> 14 = 0
  //      ---------------> 15 = 0
  //      ---------------> 16 = 3
  
    switch (UAV3iSettings.getMode())
    {
      case PAPARAZZI_DIRECT:
        UAVModel.addUAVDataPoint(Integer.parseInt(message[4]),
                                 Integer.parseInt(message[5]),
                                 uavNavStatusListener.getLastCourseValue(),
                                 Integer.parseInt(message[7]),
                                 Integer.parseInt(message[13]));
        break;
      case VETO:
      case VETO_AUTO:
        // On transmet via RMI à l'IHM table tactile la position du drone.
        if(uav3iTransmitter != null && Veto.getVetoState() == VetoState.RECEIVING)
        {
          try
          {
            uav3iTransmitter.addUAVDataPoint(Integer.parseInt(message[4]),
                                             Integer.parseInt(message[5]),
                                             uavNavStatusListener.getLastCourseValue(),
                                             Integer.parseInt(message[7]),
                                             Integer.parseInt(message[13]));
          }
          catch (RemoteException e)
          {
            LoggerUtil.LOG.severe(e.getMessage().replace("\n", " "));
          }
          
          // On transmet aussi la position du drone à l'IHM Veto pour l'affichage local.
          UAVModel.addUAVDataPoint(Integer.parseInt(message[4]),
                                   Integer.parseInt(message[5]),
                                   uavNavStatusListener.getLastCourseValue(),
                                   Integer.parseInt(message[7]),
                                   Integer.parseInt(message[13]));
        }
        else
          LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
        
        break;
      default:
        break;
    }
  }
  //-----------------------------------------------------------------------------
}

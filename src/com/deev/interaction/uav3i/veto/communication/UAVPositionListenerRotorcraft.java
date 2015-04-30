package com.deev.interaction.uav3i.veto.communication;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Listener utilisé pour l'écoute de la position du drone.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVPositionListenerRotorcraft implements IvyMessageListener
{
  //-----------------------------------------------------------------------------
  private IUav3iTransmitter uav3iTransmitter = null;
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour du stub : utilisé dans le cas d'une communication RMI :<br/>
   * <code>uav3i</code> &lt;---&gt; <code>Paparazzi Tranmitter</code> &lt;---&gt; <code>Paparazzi</code>.
   * 
   * @param uav3iTransmitter stub RMI utilisé pour la transmission.
   */
  public void setUav3iTransmitter(IUav3iTransmitter uav3iTransmitter)
  {
    this.uav3iTransmitter = uav3iTransmitter;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    // <message name="GPS_INT" id="155">
    //   <field name="ecef_x"  type="int32" unit="cm"   alt_unit="m"/>
    //   <field name="ecef_y"  type="int32" unit="cm"   alt_unit="m"/>
    //   <field name="ecef_z"  type="int32" unit="cm"   alt_unit="m"/>
    //   <field name="lat"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
    //   <field name="lon"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
    //   <field name="alt"     type="int32" unit="mm"   alt_unit="m">altitude above WGS84 reference ellipsoid</field>
    //   <field name="hmsl"    type="int32" unit="mm"   alt_unit="m">height above mean sea level (geoid)</field>
    //   <field name="ecef_xd" type="int32" unit="cm/s" alt_unit="m/s"/>
    //   <field name="ecef_yd" type="int32" unit="cm/s" alt_unit="m/s"/>
    //   <field name="ecef_zd" type="int32" unit="cm/s" alt_unit="m/s"/>
    //   <field name="pacc"    type="uint32" unit="cm"   alt_unit="m"/>
    //   <field name="sacc"    type="uint32" unit="cm/s" alt_unit="m/s"/>
    //   <field name="tow"     type="uint32"/>
    //   <field name="pdop"    type="uint16"/>
    //   <field name="numsv"   type="uint8"/>
    //   <field name="fix"     type="uint8" values="NONE|UKN1|UKN2|3D"/>
    // </message>

//    UAVModel.addUAVDataPoint(Integer.parseInt(message[4]),
//        Integer.parseInt(message[5]),
//        0,
//        Integer.parseInt(message[6]),
//        System.currentTimeMillis());

    
    System.out.println("Longueur du tableau args = " + args.length);
    for(int i=0; i<args.length; i++)
      System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
  }
  //-----------------------------------------------------------------------------
}

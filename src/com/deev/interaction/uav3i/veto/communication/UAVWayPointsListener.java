package com.deev.interaction.uav3i.veto.communication;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Listener utilisé pour l'écoute de la position du drone.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVWayPointsListener implements IvyMessageListener
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
    System.out.println("####### ");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    // Definition of messages in paparazzi_v5.0.3_stable/conf/messages.xml
    //
    // <message name="WAYPOINT_MOVED" id="30">
    //   <field name="ac_id" type="string"/>                  1
    //   <field name="wp_id" type="uint8"/>                   2
    //   <field name="lat"  type="float" unit="deg"></field>  3
    //   <field name="long"  type="float" unit="deg"></field> 4
    //   <field name="alt" type="float" unit="m"></field>     5
    // </message>

    //System.out.println("Longueur du tableau args = " + args.length);
    //for(int i=0; i<args.length; i++)
    //  System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
    
    String tokens = args[1];

    String[] message = tokens.split(" ");
    //for(int i=0; i<message.length; i++)
    //  System.out.println("---------------> " + i + " = " + message[i]);
    //System.out.print("---------------> ");
    //for(int i=0; i<message.length; i++)
    //  System.out.print("["+i+" = " + message[i] + "] ");
    //System.out.println();
    // ---------------> [0 = ] [1 = 5] [2 = 2] [3 = 48.189271] [4 = -4.295421] [5 = 100.000000]
    
    int    wpId = Integer.parseInt(message[2]);
    double lat  = Double.parseDouble(message[3]);
    double lng  = Double.parseDouble(message[4]);
    double alt  = Double.parseDouble(message[5]);
    
    UAVWayPoint wayPoint = new UAVWayPoint(wpId, lat, lng, alt);

    switch (UAV3iSettings.getMode())
    {
      case PAPARAZZI_DIRECT:
        UAVModel.getWayPoints().updateWayPoint(wayPoint);
        break;
      case VETO:
        if(UAVModel.getWayPoints().updateWayPoint(wayPoint))
        {
          
        }
        break;
      default:
        break;
    }


//    switch (UAV3iSettings.getMode())
//    {
//      case PAPARAZZI_DIRECT:
//        UAVModel.setAltitude(altitude);
//        UAVModel.setVerticalSpeed(verticalSpeed);
//        UAVModel.setGroundSpeed(groundSpeed);
//        UAVModel.setGroundAltitude(groundAltitude);
//        LoggerUtil.LOG.info("Flight params : altitude = " + altitude + " / ground altitude = " + groundAltitude + " / vertical speed = " + verticalSpeed + " / ground speed = " + groundSpeed);
//        break;
//      case VETO:
//        // On transmet via RMI à l'IHM table tactile la position du drone.
//        if(uav3iTransmitter != null && Veto.state == StateVeto.RECEIVING)
//        {
//          try
//          {
//            uav3iTransmitter.addFlightParams(altitude, verticalSpeed, groundAltitude, groundSpeed);
//          }
//          catch (RemoteException e)
//          {
//            LoggerUtil.LOG.severe(e.getMessage().replace("\n", " "));
//          }
//          
//          // On transmet aussi l'altitude et la vitesse ascentionnelle à l'IHM Veto pour l'affichage local.
//          UAVModel.setAltitude(altitude);
//          UAVModel.setVerticalSpeed(verticalSpeed);
//        }
//        else
//          LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);
//        
//        break;
//      default:
//        break;
//    }
  }
  //-----------------------------------------------------------------------------
}

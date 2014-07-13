package com.deev.interaction.uav3i.veto.communication;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;
import com.deev.interaction.uav3i.veto.ui.Veto2;
import com.deev.interaction.uav3i.veto.ui.Veto2.StateVeto;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Listener utilisé pour l'écoute de la position du drone.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVFlightParamsListener implements IvyMessageListener
{
  //-----------------------------------------------------------------------------
  private IUav3iTransmitter uav3iTransmitter = null;
  private int cpt = 0;
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
    // On n'envoie les infos qu'une fois sur 10...
    cpt++;
    if(cpt%10 != 0)
      return;
    else
      cpt = 0;

    // Definition of messages in paparazzi_v5.0.3_stable/conf/messages.xml
    //
    // <message name="FLIGHT_PARAM" ID="11">
    //   <field name="ac_id"  type="string"/>                            1
    //   <field name="roll"   type="float" unit="deg"/>                  
    //   <field name="pitch"  type="float" unit="deg"/>                  4
    //   <field name="lat"    type="float" unit="deg"/>                  5
    //   <field name="long"   type="float" unit="deg"/>                  6
    //   <field name="speed"  type="float" unit="m/s"/>                  7
    //   <field name="course" type="float" unit="deg" format="%.1f"/>    8
    //   <field name="alt"    type="float" unit="m"/>                    9
    //   <field name="climb"  type="float" unit="m/s"/>                 10
    //   <field name="agl"    type="float" unit="m"/>                   11
    //   <field name="unix_time"    type="float" unit="s (Unix time)"/> 12
    //   <field name="itow"   type="uint32" unit="ms"/>                 13
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
    // ---------------> [0 = ] [1 = 5] [2 = 11.883718] [3 = 1.340206] [4 = 54.695411] [5 = 48.186651] [6 = -4.305033] [7 = 12.660000] [8 = 59.8] [9 = 100.000000] [10 = -0.000703] [11 = 100.000000] [12 = 1398065619.114756] [13 = 113635999]
    double altitude       = Double.parseDouble(message[9]);
    double verticalSpeed  = Double.parseDouble(message[10]);
    double groundAltitude = Double.parseDouble(message[11]);
    double groundSpeed    = Double.parseDouble(message[7]);

    switch (UAV3iSettings.getMode())
    {
      case PAPARAZZI_DIRECT:
        UAVModel.setAltitude(altitude);
        UAVModel.setVerticalSpeed(verticalSpeed);
        UAVModel.setGroundSpeed(groundSpeed);
        UAVModel.setGroundAltitude(groundAltitude);
        LoggerUtil.LOG.info("Flight params : altitude = " + altitude + " / ground altitude = " + groundAltitude + " / vertical speed = " + verticalSpeed + " / ground speed = " + groundSpeed);
        break;
      case VETO:
        // On transmet via RMI à l'IHM table tactile la position du drone.
        if(uav3iTransmitter != null && Veto2.state == StateVeto.RECEIVING)
        {
          try
          {
            uav3iTransmitter.addFlightParams(altitude, verticalSpeed, groundAltitude, groundSpeed);
          }
          catch (RemoteException e)
          {
            LoggerUtil.LOG.severe(e.getMessage().replace("\n", " "));
          }
          
          // On transmet aussi les infos à l'IHM Veto pour l'affichage local.
          UAVModel.setAltitude(altitude);
          UAVModel.setVerticalSpeed(verticalSpeed);
          UAVModel.setGroundSpeed(groundSpeed);
          UAVModel.setGroundAltitude(groundAltitude);
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

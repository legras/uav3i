package com.deev.interaction.uav3i.veto.communication;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.StateVeto;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Listener utilisé pour l'écoute de la position du drone.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVPositionListener implements IvyMessageListener
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
//    System.out.println("Longueur du tableau args = " + args.length);
//    for(int i=0; i<args.length; i++)
//      System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
    
    String tokens = args[1];

    //    <message name="GPS" ID="8">
    //      <field name="mode" type="uint8" unit="byte_mask"/>
    //      <field name="utm_east" type="int32" unit="cm"/>       2
    //      <field name="utm_north" type="int32" unit="cm"/>      3
    //      <field name="course" type="int16" unit="decideg"/>    4
    //      <field name="alt" type="int32" unit="cm"/>            5
    //      <field name="speed" type="uint16" unit="cm/s"/>
    //      <field name="climb" type="int16" unit="cm/s"/>
    //      <field name="itow" type="uint32" unit="ms"/>          8 Time ?
    //      <field name="utm_zone" type="uint8"/>                 9 Erreur ?
    //      <field name="gps_nb_err" type="uint8"/>
    //    </message>
    // Il doit y avoir une erreur dans la doc !!! A l'évidence le 8ème n'est pas le temps/

    String[] message = tokens.split(" ");
    //for(int i=0; i<message.length; i++)
    //  System.out.println("---------------> " + i + " = " + message[i]);

    // Les messages "GPS_SOL" passe par le pattern, on les filtre ici. 
    if(message[0].equals("_SOL")) return;

    //System.out.print("---------------> ");
    //for(int i=0; i<message.length; i++)
    //  System.out.print("["+i+" = " + message[i] + "] ");
    //System.out.println();

    // ---------------> [0 = ] [1 = 3] [2 = 72344664] [3 = 532066912] [4 = 932] [5 = 75826] [6 = 1443] [7 = 447] [8 = 0] [9 = 127047240] [10 = 30] [11 = 8]
  
    switch (UAV3iSettings.getMode())
    {
      case PAPARAZZI_DIRECT:
        UAVModel.addUAVDataPoint(Integer.parseInt(message[2]),  // utmEast
                                 Integer.parseInt(message[3]),  // utmNorth
                                 Integer.parseInt(message[10]), // utm_zone
                                 Integer.parseInt(message[4]),  // course
                                 Integer.parseInt(message[5]),  // alt
                                 Long.parseLong(message[9]));   // t
        break;
      case VETO:
        // On transmet via RMI à l'IHM table tactile la position du drone.
        if(uav3iTransmitter != null && Veto.state == StateVeto.RECEIVING)
        {
          try
          {
            uav3iTransmitter.addUAVDataPoint(Integer.parseInt(message[2]),  // utmEast
                                             Integer.parseInt(message[3]),  // utmNorth
                                             Integer.parseInt(message[10]), // utm_zone
                                             Integer.parseInt(message[4]),  // course
                                             Integer.parseInt(message[5]),  // alt
                                             Long.parseLong(message[9]));   // t
          }
          catch (RemoteException e)
          {
            LoggerUtil.LOG.severe(e.getMessage().replace("\n", " "));
          }
          
          // On transmet aussi la position du drone à l'IHM Veto pour l'affichage local.
          UAVModel.addUAVDataPoint(Integer.parseInt(message[2]),  // utmEast
                                   Integer.parseInt(message[3]),  // utmNorth
                                   Integer.parseInt(message[10]), // utm_zone
                                   Integer.parseInt(message[4]),  // course
                                   Integer.parseInt(message[5]),  // alt
                                   Long.parseLong(message[9]));   // t
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

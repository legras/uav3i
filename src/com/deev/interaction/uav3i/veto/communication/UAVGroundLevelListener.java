package com.deev.interaction.uav3i.veto.communication;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.StateVeto;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

/**
 * Listener utilisé pour l'écoute de la position du drone.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVGroundLevelListener implements IvyMessageListener
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
        
    
    //System.out.println("Longueur du tableau args = " + args.length);
    //for(int i=0; i<args.length; i++)
    //  System.out.println("---------------> Message IVY (client="+client.getApplicationName()+") ["+i+"]= " + args[i]);
    
    String tokens = args[1];
    String[] message = tokens.split(" ");
    //for(int i=0; i<message.length; i++)
    //  System.out.println("---------------> " + i + " = " + message[i]);
    
    double groundLevel   = Double.parseDouble(message[1]);
    double verticalSpeed = Double.parseDouble(message[2]);
    
    switch (UAV3iSettings.getMode())
    {
      case PAPARAZZI_DIRECT:
        UAVModel.setGroundLevel(groundLevel);
        UAVModel.setVerticalSpeed(verticalSpeed);
        LoggerUtil.LOG.info("Ground Level = " + groundLevel + " / Vertical Speed = " + verticalSpeed);
        break;
      case VETO:
        // On transmet via RMI à l'IHM table tactile l'altitude et la vitesse ascentionnelle.
        if(uav3iTransmitter != null && Veto.state == StateVeto.RECEIVING)
        {
          try
          {
            uav3iTransmitter.addGroundLevel(groundLevel, verticalSpeed);
          }
          catch (RemoteException e)
          {
            LoggerUtil.LOG.severe(e.getMessage().replace("\n", " "));
          }
          
          // On transmet aussi l'altitude et la vitesse ascentionnelle à l'IHM Veto pour l'affichage local.
          UAVModel.setGroundLevel(groundLevel);
          UAVModel.setVerticalSpeed(verticalSpeed);
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
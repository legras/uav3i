package com.deev.interaction.uav3i.veto.communication.uavListener;

import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

public abstract class UAVListener implements IvyMessageListener
{
  //-----------------------------------------------------------------------------
  protected IUav3iTransmitter uav3iTransmitter = null;
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
  /**
   * Display args received from the Ivy message. Used only for debug purpose.
   * 
   * @param client
   * @param args
   */
  public void displayArgs(IvyClient client, String[] args)
  {
    System.out.println("args.length = " + args.length);
    for (int i = 0; i < args.length; i++)
      System.out.println("---------------> IVY message (client=" + client.getApplicationName() + ") [" + i + "]= " + args[i]);
  }
  //-----------------------------------------------------------------------------
  @Override
  public abstract void receive(IvyClient client, String[] args);
  //-----------------------------------------------------------------------------
}

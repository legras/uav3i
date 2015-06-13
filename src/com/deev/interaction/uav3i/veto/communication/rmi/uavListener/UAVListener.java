package com.deev.interaction.uav3i.veto.communication.rmi.uavListener;

import com.deev.interaction.uav3i.veto.communication.rmi.IUav3iTransmitter;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

public abstract class UAVListener implements IvyMessageListener
{
  //-----------------------------------------------------------------------------
  protected IUav3iTransmitter uav3iTransmitter = null;
  //-----------------------------------------------------------------------------
  /**
   * <code>uav3iTransmitter</code> is the RMI stub used to transmit informations
   * when a RMI communication is needed:<br/>
   * <code>uav3i</code> &lt;---&gt; <code>Paparazzi Tranmitter</code> &lt;---&gt; <code>Paparazzi</code>.
   * 
   * @param uav3iTransmitter RMI stub used for the transmission.
   */
  public void setUav3iTransmitter(IUav3iTransmitter uav3iTransmitter)
  {
    this.uav3iTransmitter = uav3iTransmitter;
  }
  //-----------------------------------------------------------------------------
  /**
   * Display args received from the Ivy message. To be used for debug purpose.
   * 
   * @param client
   * @param args
   */
  public void displayArgs(String from, IvyClient client, String[] args)
  {
    System.out.println("From '" + from + "': args.length = " + args.length);
    for (int i = 0; i < args.length; i++)
      System.out.println("---------------> IVY message (client=" + client.getApplicationName() + ") [" + i + "]= " + args[i]);
  }
  //-----------------------------------------------------------------------------
  @Override
  public abstract void receive(IvyClient client, String[] args);
  //-----------------------------------------------------------------------------
}

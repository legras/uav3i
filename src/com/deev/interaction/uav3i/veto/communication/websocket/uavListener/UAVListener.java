package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyMessageListener;

public abstract class UAVListener implements IvyMessageListener
{
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

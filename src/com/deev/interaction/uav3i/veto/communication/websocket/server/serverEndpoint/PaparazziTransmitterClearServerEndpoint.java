package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/PaparazziTransmitterClear")
public class PaparazziTransmitterClearServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(String emptyValue)
  {
    System.out.println("####### PaparazziTransmitterClearServerEndpoint.receive()");
  }
  //-----------------------------------------------------------------------------
}

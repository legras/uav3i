package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/PaparazziTransmitterExecute")
public class PaparazziTransmitterExecuteServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(String idManoeuver)
  {
  }
  //-----------------------------------------------------------------------------
}

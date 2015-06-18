package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/Register")
public class RegisterServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public String register(String ip)
  {
    return ip + " is now connected!";
  }
  //-----------------------------------------------------------------------------
}

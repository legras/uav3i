package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.websocket.Veto2ClientWebsocketFacade;

@ServerEndpoint(value = "/Register")
public class RegisterServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public String register(String ip)
  {
    LoggerUtil.LOG.info("Client registers from " + ip);
    Veto2ClientWebsocketFacade.register();
    return ip + " is now connected!";
  }
  //-----------------------------------------------------------------------------
}

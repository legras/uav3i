package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.ui.Veto;

@ServerEndpoint(value = "/PaparazziTransmitterClear")
public class PaparazziTransmitterClearServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(String emptyValue)
  {
    LoggerUtil.LOG.info("clearManoeuver()");
    Veto.getSymbolMapVeto().clearManoeuver();
  }
  //-----------------------------------------------------------------------------
}

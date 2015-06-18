package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import java.io.IOException;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
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
  @OnClose
  public void onClose(Session session, CloseReason reason) throws IOException
  {
    Veto2ClientWebsocketFacade.unRegister();
    LoggerUtil.LOG.log(reason.getCloseCode() != CloseCodes.NORMAL_CLOSURE ? Level.INFO : Level.WARNING,
                       reason.getCloseCode() + " - " + reason.getReasonPhrase());
  }
  //-----------------------------------------------------------------------------
  @OnError
  public void onError(Session session, Throwable t) throws IOException
  {
    session.close(new CloseReason(CloseCodes.CLOSED_ABNORMALLY, t.getMessage()));
    t.printStackTrace();
  }
  //-----------------------------------------------------------------------------
}

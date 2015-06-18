package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import java.io.IOException;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.log.LoggerUtil;

@ServerEndpoint(value="/Uav3iTransmitterAddUavDataPoint")
public class Uav3iTransmitterAddUavDataPointServerEndpoint
{
  //-----------------------------------------------------------------------------
  private static Session session = null;
  //-----------------------------------------------------------------------------
  @OnOpen
  public void onOpen(Session session)
  {
    this.session = session;
  }
  //-----------------------------------------------------------------------------
  public static void addUAVDataPoint(String what, String message) throws IOException
  {
    System.out.println("Uav3iTransmitterAddUavDataPointServerEndpoint.addUAVDataPoint("+what+"|"+message+")");
    session.getBasicRemote().sendText(what + "|" + message);
  }
  //-----------------------------------------------------------------------------
  @OnClose
  public void onClose(Session session, CloseReason reason) throws IOException
  {
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

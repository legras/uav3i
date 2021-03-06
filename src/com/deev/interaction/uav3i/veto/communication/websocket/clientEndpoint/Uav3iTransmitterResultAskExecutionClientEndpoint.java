package com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint;

import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

@ClientEndpoint
public class Uav3iTransmitterResultAskExecutionClientEndpoint
{
  //-----------------------------------------------------------------------------
  @SuppressWarnings("unused")
  private Session   session   = null;
  //-----------------------------------------------------------------------------
  public Uav3iTransmitterResultAskExecutionClientEndpoint(URI uriServerEndpoint) throws DeploymentException, IOException
  {
    ClientManager client = ClientManager.createClient();
    // Voir si on peut récupérer la session à la connexion (ici par l'appel de la
    // méthode connectToServer) ou lors de l'appel de la méthode annotée @OnOpen
    //session = client.connectToServer(this, uriServerEndpoint);
    client.connectToServer(this, uriServerEndpoint);
  }
  //-----------------------------------------------------------------------------
  @OnOpen
  public void onOpen(Session session)
  {
    this.session = session;
  }
  //-----------------------------------------------------------------------------
  @OnMessage
  public void resultAskExecution(String messageFromServer)
  {
    StringTokenizer st = new StringTokenizer(messageFromServer,"*");
    int idMnvr = Integer.parseInt(st.nextToken());
    boolean result = Boolean.parseBoolean(st.nextToken());
    
    MainFrame.getSymbolMap().answerManoeuver(idMnvr, result);
    LoggerUtil.LOG.info("Result for execution: " + result + " on manoeuver id: " + idMnvr);
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

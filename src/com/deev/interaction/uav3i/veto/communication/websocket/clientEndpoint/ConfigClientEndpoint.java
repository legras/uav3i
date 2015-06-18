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

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.airframe.AirframeFacade;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;

@ClientEndpoint
public class ConfigClientEndpoint
{
  //-----------------------------------------------------------------------------
  private Session session = null;
  //-----------------------------------------------------------------------------
  public ConfigClientEndpoint(URI uriServerEndpoint) throws DeploymentException, IOException
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
  public void getConfig(String which) throws IOException
  {
    session.getBasicRemote().sendText(which);
    LoggerUtil.LOG.log(Level.INFO, "config: " + which + " asked.");
  }
  //-----------------------------------------------------------------------------
  @OnMessage
  public void onMessage(String receivedConfig) throws IOException
  {
    StringTokenizer st = new StringTokenizer(receivedConfig,"|");
    String which = st.nextToken();
    if(st.hasMoreTokens())
    {
      String config = st.nextToken();
      switch (which)
      {
        case "flight_plan":
          FlightPlanFacade.init(config);
          break;
        case "airframe":
          AirframeFacade.init(config);
          break;
        case "ivy_messages":
          IvyMessagesFacade.init(config);
          break;
        default:
          break;
      }
    }

    //System.out.println(receivedConfig);
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

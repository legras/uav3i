package com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.glassfish.tyrus.client.ClientManager;

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTODecoder2;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTOEncoder2;

@ClientEndpoint(decoders = ManoeuverDTODecoder2.class,
                encoders = ManoeuverDTOEncoder2.class)
public class PaparazziTransmitterCommunicateClientEndpoint
{
  //-----------------------------------------------------------------------------
  private Session session = null;
  //-----------------------------------------------------------------------------
  public PaparazziTransmitterCommunicateClientEndpoint(URI uriServerEndpoint) throws DeploymentException, IOException
  {
    ClientManager client = ClientManager.createClient();
    // Voir si on peut récupérer la session à la connexion (ici par l'appel de la
    // méthode connectToServer) ou lors de l'appel de la méthode annotée @OnOpen
    //session = client.connectToServer(this, uriServerEndpoint);
    client.connectToServer(this, uriServerEndpoint);
  }
  //-----------------------------------------------------------------------------
  @OnOpen
  public void onOpen(Session session) throws IOException
  {
    this.session = session;
  }
  //-----------------------------------------------------------------------------
  public void communicateManoeuver(ManoeuverDTO mnvrDTO) throws IOException, EncodeException
  {
    session.getBasicRemote().sendObject(mnvrDTO);
  }
  //-----------------------------------------------------------------------------
  @OnClose
  public void onClose(Session session, CloseReason reason) throws IOException
  {
    Level level;
    if(reason.getCloseCode() != CloseCodes.NORMAL_CLOSURE)
      level = Level.INFO;
    else
      level = Level.WARNING;

    LoggerUtil.LOG.log(level, reason.getCloseCode() + " - " + reason.getReasonPhrase());
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

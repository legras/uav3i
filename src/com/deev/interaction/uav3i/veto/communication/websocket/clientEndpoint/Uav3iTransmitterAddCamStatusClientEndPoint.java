package com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint;

import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;

import org.glassfish.tyrus.client.ClientManager;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

@ClientEndpoint
public class Uav3iTransmitterAddCamStatusClientEndPoint
{
  //-----------------------------------------------------------------------------
  @SuppressWarnings("unused")
  private Session   session   = null;
  //-----------------------------------------------------------------------------
  public Uav3iTransmitterAddCamStatusClientEndPoint(URI uriServerEndpoint) throws DeploymentException, IOException
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
  public void addCamStatus(String messageFromServer)
  {
    StringTokenizer st = new StringTokenizer(messageFromServer,"*");
    
    double camLat        = Double.parseDouble(st.nextToken());
    double camLong       = Double.parseDouble(st.nextToken());
    double camTargetLat  = Double.parseDouble(st.nextToken());
    double camTargetLong = Double.parseDouble(st.nextToken());

    LatLng camCoord       = new LatLng(camLat, camLong);
    LatLng camTargetCoord = new LatLng(camTargetLat, camTargetLong);

//    UAVModel.setAltitude(altitude);
//    UAVModel.setVerticalSpeed(verticalSpeed);
//    UAVModel.setGroundSpeed(groundSpeed);
//    UAVModel.setGroundAltitude(groundAltitude);
    LoggerUtil.LOG.info("Cam status: camCoord = " + camCoord + " - camTargetCoord = " + camTargetCoord);
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

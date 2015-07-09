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

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

@ClientEndpoint
public class Uav3iTransmitterAddUavDataPointClientEndpoint
{
  //-----------------------------------------------------------------------------
  @SuppressWarnings("unused")
  private Session   session   = null;
  //-----------------------------------------------------------------------------
  public Uav3iTransmitterAddUavDataPointClientEndpoint(URI uriServerEndpoint) throws DeploymentException, IOException
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
  public void addUAVDataPoint(String messageFromServer)
  {
    StringTokenizer st1 = new StringTokenizer(messageFromServer,"|");
    String what = st1.nextToken();
    if(st1.hasMoreTokens())
    {
      String values = st1.nextToken();
      StringTokenizer st2 = new StringTokenizer(values,"*");
      if(what.equalsIgnoreCase("latlon"))  
      {
        /*
         * lat,lon coordinates received
         */
        int    latitude      = Integer.parseInt(st2.nextToken());
        int    longitude     = Integer.parseInt(st2.nextToken());
        int    course        = Integer.parseInt(st2.nextToken());
        int    altitude      = Integer.parseInt(st2.nextToken());
        long   time          = Long.parseLong(st2.nextToken());
        double camTargetLat  = Double.parseDouble(st2.nextToken());
        double camTargetLong = Double.parseDouble(st2.nextToken());

        UAVModel.addUAVDataPoint(latitude, longitude, course, altitude, time, camTargetLat, camTargetLong);
      }
      else if(what.equalsIgnoreCase("utm"))
      {
        /*
         * utm coordinates received
         */
        // (int utm_east, int utm_north, int utm_zone, int course, int alt, long t
        int  utmEast  = Integer.parseInt(st2.nextToken());
        int  utmNorth = Integer.parseInt(st2.nextToken());
        int  utmZone  = Integer.parseInt(st2.nextToken());
        int  course   = Integer.parseInt(st2.nextToken());
        int  altitude = Integer.parseInt(st2.nextToken());
        long time     = Long.parseLong(st2.nextToken());
        UAVModel.addUAVDataPoint(utmEast, utmNorth, utmZone, course, altitude, time);
      }
    }
  }
  //-----------------------------------------------------------------------------
  @OnClose
  public void onClose(Session session, CloseReason reason) throws IOException
  {
    LoggerUtil.LOG.log(reason.getCloseCode() == CloseCodes.NORMAL_CLOSURE ? Level.INFO : Level.WARNING,
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

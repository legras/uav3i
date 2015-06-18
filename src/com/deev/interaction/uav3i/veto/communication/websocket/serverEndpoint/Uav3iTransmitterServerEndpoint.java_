package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import java.io.IOException;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.websocket.coder.UAVWayPointDecoder;
import com.deev.interaction.uav3i.veto.communication.websocket.coder.UAVWayPointEncoder;

@ServerEndpoint(value = "/Uav3iTransmitter",
                decoders = UAVWayPointDecoder.class,
                encoders = UAVWayPointEncoder.class)
public class Uav3iTransmitterServerEndpoint
{
  //-----------------------------------------------------------------------------
  public static void addUAVDataPoint(int  utm_east, int  utm_north, int  utm_zone, int  course, int  alt, long t)
  {
  }
  //-----------------------------------------------------------------------------
  public static void addUAVDataPoint(int lat, int lon, int c, int alt, long t)
  {
  }
  //-----------------------------------------------------------------------------
  public static void addFlightParams(double altitude, double verticalSpeed, double groundAltitude,  double groundSpeed)
  {
  }
  //-----------------------------------------------------------------------------
  public static void updateWayPoint(UAVWayPoint wayPoint)
  {
  }
  //-----------------------------------------------------------------------------
  public static void resultAskExecution(int idMnvr, boolean result)
  {
  }
  //-----------------------------------------------------------------------------
  public static void ping()
  {
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

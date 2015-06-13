package com.deev.interaction.uav3i.veto.communication.websocket.coder;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.deev.interaction.uav3i.model.UAVWayPoint;

public class UAVWayPointDecoder implements Decoder.BinaryStream<UAVWayPoint>
{
  //-----------------------------------------------------------------------------
  @Override
  public UAVWayPoint decode(InputStream serializedInstance) throws DecodeException, IOException
  {
    try
    {
      ObjectInputStream ois = new ObjectInputStream(serializedInstance);
      UAVWayPoint uavWayPoint = (UAVWayPoint) ois.readObject();
      return uavWayPoint;
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void init(EndpointConfig config) { /* do nothing. */ }
  //-----------------------------------------------------------------------------
  @Override
  public void destroy()                   { /* do nothing. */ }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.dto;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ManoeuverDTOEncoder implements Encoder.BinaryStream<ManoeuverDTO>
{
  //-----------------------------------------------------------------------------
  @Override
  public void encode(ManoeuverDTO instance, OutputStream serializedInstance) throws EncodeException, IOException
  {
    ObjectOutputStream oos = new ObjectOutputStream(serializedInstance);
    oos.writeObject(instance);
    oos.flush();
  }
  //-----------------------------------------------------------------------------
  @Override
  public void init(EndpointConfig config) { /* do nothing. */ }
  //-----------------------------------------------------------------------------
  @Override
  public void destroy()                   { /* do nothing. */ }
  //-----------------------------------------------------------------------------
}

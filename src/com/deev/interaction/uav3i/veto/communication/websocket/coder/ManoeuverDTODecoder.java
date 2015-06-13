package com.deev.interaction.uav3i.veto.communication.websocket.coder;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class ManoeuverDTODecoder implements Decoder.BinaryStream<ManoeuverDTO>
{
  //-----------------------------------------------------------------------------
  @Override
  public ManoeuverDTO decode(InputStream serializedInstance) throws DecodeException, IOException
  {
    try
    {
      ObjectInputStream ois = new ObjectInputStream(serializedInstance);
      ManoeuverDTO manoeuverDTO = (ManoeuverDTO) ois.readObject();
      return manoeuverDTO;
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

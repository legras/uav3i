package com.deev.interaction.uav3i.veto.communication.dto;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ManoeuverDTODecoder implements Decoder.Text<ManoeuverDTO>
{
  //-----------------------------------------------------------------------------
  @Override
  public ManoeuverDTO decode(String jsonInstance) throws DecodeException
  {
    ObjectMapper mapper = new ObjectMapper();
    try
    {
      return mapper.readValue(jsonInstance, ManoeuverDTO.class);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  //-----------------------------------------------------------------------------
  @Override
  public boolean willDecode(String arg0)
  {
    return true;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void init(EndpointConfig config) { /* do nothing. */ }
  //-----------------------------------------------------------------------------
  @Override
  public void destroy()                   { /* do nothing. */ }
  //-----------------------------------------------------------------------------
}

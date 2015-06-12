package com.deev.interaction.uav3i.veto.communication.dto;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ManoeuverDTOEncoder implements Encoder.Text<ManoeuverDTO>
{
  //-----------------------------------------------------------------------------
  @Override
  public String encode(ManoeuverDTO instance) throws EncodeException
  {
    ObjectMapper mapper = new ObjectMapper();
    try
    {
      return mapper.writeValueAsString(instance);
    }
    catch (JsonProcessingException e)
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

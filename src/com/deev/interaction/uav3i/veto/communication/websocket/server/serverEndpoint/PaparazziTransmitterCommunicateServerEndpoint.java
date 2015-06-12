package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTODecoder2;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTOEncoder2;

@ServerEndpoint(value    = "/PaparazziTransmitterCommunicate",
                decoders = ManoeuverDTODecoder2.class,
                encoders = ManoeuverDTOEncoder2.class)
public class PaparazziTransmitterCommunicateServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(ManoeuverDTO mnvrDTO)
  {
    System.out.println("####### PaparazziTransmitterServerEndpoint.receive(" + mnvrDTO + ")");
  }
  //-----------------------------------------------------------------------------
}

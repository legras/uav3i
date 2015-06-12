package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTODecoder;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTOEncoder;

@ServerEndpoint(value    = "/PaparazziTransmitterCommunicate",
                decoders = ManoeuverDTODecoder.class,
                encoders = ManoeuverDTOEncoder.class)
public class PaparazziTransmitterCommunicateServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(ManoeuverDTO mnvrDTO)
  {
    System.out.println("####### PaparazziTransmitterServerEndpoint.receive(" + mnvrDTO + ")");
    System.out.println("          ---> manoeuver is a " + mnvrDTO.getClass().getSimpleName());
  }
  //-----------------------------------------------------------------------------
}

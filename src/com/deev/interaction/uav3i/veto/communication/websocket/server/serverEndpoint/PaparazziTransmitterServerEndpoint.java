package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTODecoder;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTOEncoder;

@ServerEndpoint(value    = "/PaparazziTransmitter/{command}",
                decoders = ManoeuverDTODecoder.class,
                encoders = ManoeuverDTOEncoder.class)
public class PaparazziTransmitterServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(@PathParam("command") String command, ManoeuverDTO mnvrDTO)
  {
    System.out.println("####### PaparazziTransmitterServerEndpoint.receive(" + command + ", " + mnvrDTO + ")");
    switch (command)
    {
      case "communicate":
        break;
      case "execute":
        break;
      case "clear":
        break;
      default:
        break;
    }
  }
  //-----------------------------------------------------------------------------
}

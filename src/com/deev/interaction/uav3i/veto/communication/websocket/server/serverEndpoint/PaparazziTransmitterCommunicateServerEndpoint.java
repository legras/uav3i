package com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.websocket.coder.ManoeuverDTODecoder;
import com.deev.interaction.uav3i.veto.communication.websocket.coder.ManoeuverDTOEncoder;
import com.deev.interaction.uav3i.veto.ui.Veto;

@ServerEndpoint(value    = "/PaparazziTransmitterCommunicate",
                decoders = ManoeuverDTODecoder.class,
                encoders = ManoeuverDTOEncoder.class)
public class PaparazziTransmitterCommunicateServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public void receive(ManoeuverDTO mnvrDTO)
  {
    LoggerUtil.LOG.info("communicateManoeuver("+mnvrDTO+")");
    Veto.getSymbolMapVeto().addManoeuver(mnvrDTO);
    Veto.centerManoeuverOnMap(mnvrDTO);
  }
  //-----------------------------------------------------------------------------
}

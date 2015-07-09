package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import java.io.IOException;
import java.util.logging.Level;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.CloseReason.CloseCodes;
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
  @OnClose
  public void onClose(Session session, CloseReason reason) throws IOException
  {
    LoggerUtil.LOG.log(reason.getCloseCode() == CloseCodes.NORMAL_CLOSURE ? Level.INFO : Level.WARNING,
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

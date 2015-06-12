package com.deev.interaction.uav3i.veto.communication.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.communication.PaparazziCommunication;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint.ConfigClientEndpoint;

public class PaparazziWebsocketCommunication extends PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  public PaparazziWebsocketCommunication() throws DeploymentException, IOException, URISyntaxException
  {
    String baseURI = "ws://" + UAV3iSettings.getVetoServerIP()+":" + UAV3iSettings.getVetoServerPort() + "/berisuas";
    ConfigClientEndpoint config = new ConfigClientEndpoint(new URI(baseURI + "/Config"));
    config.getConfig("flight_plan");
//    config.getConfig("airframe");
//    config.getConfig("ivy_messages");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void communicateManoeuver(ManoeuverDTO mnvrDTO)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(int idMnvr)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(Manoeuver mnvr)
  {
    // Not used in PAPARAZZI_WEBSOCKET mode.
  }
  //-----------------------------------------------------------------------------
  @Override
  public void clearManoeuver()
  {
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.PaparazziCommunication;
import com.deev.interaction.uav3i.veto.communication.dto.CircleMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint.ConfigClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint.PaparazziTransmitterClearClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint.PaparazziTransmitterCommunicateClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.client.clientEndpoint.PaparazziTransmitterExecuteClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.PaparazziTransmitterClearServerEndpoint;

public class PaparazziWebsocketCommunication extends PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  private ConfigClientEndpoint               config;
  private PaparazziTransmitterCommunicateClientEndpoint paparazziTransmitterCommunicate;
  private PaparazziTransmitterExecuteClientEndpoint     paparazziTransmitterExecute;
  private PaparazziTransmitterClearClientEndpoint       paparazziTransmitterClear;
  //-----------------------------------------------------------------------------
  public PaparazziWebsocketCommunication() throws DeploymentException, IOException, URISyntaxException
  {
    String baseURI = "ws://" + UAV3iSettings.getVetoServerIP()+":" + UAV3iSettings.getVetoServerPort() + "/berisuas";
    config = new ConfigClientEndpoint(new URI(baseURI + "/Config"));
    paparazziTransmitterCommunicate = new PaparazziTransmitterCommunicateClientEndpoint(new URI(baseURI + "/PaparazziTransmitterCommunicate"));
    paparazziTransmitterExecute     = new PaparazziTransmitterExecuteClientEndpoint(new URI(baseURI + "/PaparazziTransmitterExecute"));
    paparazziTransmitterClear       = new PaparazziTransmitterClearClientEndpoint(new URI(baseURI + "/PaparazziTransmitterClear"));

    config.getConfig("flight_plan");
    // TODO le fichier XML airframe est utile uniquement pour trouver le "default circle radius" : absent dans le cas d'un rotorcraft... 
    config.getConfig("airframe");
    // TODO le fichier XML des messages Ivy ne doit être utilsé que côté serveur... à confirmer !
    config.getConfig("ivy_messages");
    
    CircleMnvrDTO c = new CircleMnvrDTO(7, new LatLng(0, 0), 12);
    communicateManoeuver(c);
//    executeManoeuver(7);
//    clearManoeuver();
    
  }
  //-----------------------------------------------------------------------------
  @Override
  public void communicateManoeuver(ManoeuverDTO mnvrDTO)
  {
    try
    {
      paparazziTransmitterCommunicate.communicateManoeuver(mnvrDTO);
    }
    catch (IOException | EncodeException e)
    {
      e.printStackTrace();
    }
//    
//    LoggerUtil.LOG.info("communicateManoeuver("+mnvrDTO+")");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(int idMnvr)
  {
    try
    {
      paparazziTransmitterExecute.executeManoeuver(idMnvr);
    }
    catch (IOException | EncodeException e)
    {
      e.printStackTrace();
    }
    
    //LoggerUtil.LOG.info("executeManoeuver("+mDTO+") asked");
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
    try
    {
      paparazziTransmitterClear.clearManoeuver();
    }
    catch (IOException | EncodeException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

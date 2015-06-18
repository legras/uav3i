package com.deev.interaction.uav3i.veto.communication.websocket;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.communication.Client2VetoFacade;
import com.deev.interaction.uav3i.veto.communication.dto.BoxMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.CircleMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.LineMnvrDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.ConfigClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterClearClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterCommunicateClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterExecuteClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.Uav3iTransmitterAddUavDataPointClientEndpoint;

/**
 * Classe instanciée côté client dans le cas d'une communication websocket :<br/>
 * Elle instancie les endpoints clients qui se connecteront sur leur équivalents
 * côté serveur.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class Client2VetoWebsocketFacade extends Client2VetoFacade
{
  //-----------------------------------------------------------------------------
  private ConfigClientEndpoint               config;
  private PaparazziTransmitterCommunicateClientEndpoint paparazziTransmitterCommunicate;
  private PaparazziTransmitterExecuteClientEndpoint     paparazziTransmitterExecute;
  private PaparazziTransmitterClearClientEndpoint       paparazziTransmitterClear;
  private Uav3iTransmitterAddUavDataPointClientEndpoint uav3iTransmitterAddUavDataPoint;
  //-----------------------------------------------------------------------------
  public Client2VetoWebsocketFacade() throws DeploymentException, IOException, URISyntaxException
  {
    // Connection to server.
    String baseURI = "ws://" + UAV3iSettings.getVetoServerIP()+":" + UAV3iSettings.getVetoServerPort() + "/berisuas";
    config                          = new ConfigClientEndpoint(new URI(baseURI + "/Config"));
    paparazziTransmitterCommunicate = new PaparazziTransmitterCommunicateClientEndpoint(new URI(baseURI + "/PaparazziTransmitterCommunicate"));
    paparazziTransmitterExecute     = new PaparazziTransmitterExecuteClientEndpoint(new URI(baseURI + "/PaparazziTransmitterExecute"));
    paparazziTransmitterClear       = new PaparazziTransmitterClearClientEndpoint(new URI(baseURI + "/PaparazziTransmitterClear"));
    uav3iTransmitterAddUavDataPoint = new Uav3iTransmitterAddUavDataPointClientEndpoint(new URI(baseURI + "/Uav3iTransmitterAddUavDataPoint"));

    config.getConfig("flight_plan");
    // TODO le fichier XML airframe est utile uniquement pour trouver le "default circle radius" : absent dans le cas d'un rotorcraft... 
    config.getConfig("airframe");
    // TODO le fichier XML des messages Ivy ne doit être utilsé que côté serveur... à confirmer !
    config.getConfig("ivy_messages");

    CircleMnvrDTO c = new CircleMnvrDTO(7, new LatLng(0, 0), 12);
    LineMnvrDTO   l = new LineMnvrDTO(12, new LatLng(10, 10), new LatLng(20, 20), 14, new Point2D.Double(-1,-1), new Point2D.Double(-7,-7));
    BoxMnvrDTO    b = new BoxMnvrDTO(21, new LatLng(30, 30), new LatLng(40, 40), true);
    communicateManoeuver(c);
    communicateManoeuver(l);
    communicateManoeuver(b);
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

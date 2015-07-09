package com.deev.interaction.uav3i.veto.communication.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.ui.Launcher;
import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.Client2VetoFacade;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.ConfigClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterClearClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterCommunicateClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.PaparazziTransmitterExecuteClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.RegisterClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.Uav3iTransmitterAddFlightParamsClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.Uav3iTransmitterAddUavDataPointClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.Uav3iTransmitterResultAskExecutionClientEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.clientEndpoint.Uav3iTransmitterUpdateWayPointClientEndpoint;

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
  private RegisterClientEndpoint                           register;
  private ConfigClientEndpoint                             config;
  private PaparazziTransmitterCommunicateClientEndpoint    paparazziTransmitterCommunicate;
  private PaparazziTransmitterExecuteClientEndpoint        paparazziTransmitterExecute;
  private PaparazziTransmitterClearClientEndpoint          paparazziTransmitterClear;
  private Uav3iTransmitterAddUavDataPointClientEndpoint    uav3iTransmitterAddUavDataPoint;
  private Uav3iTransmitterUpdateWayPointClientEndpoint     uav3iTransmitterUpdateWayPoint;
  private Uav3iTransmitterAddFlightParamsClientEndpoint    uav3iTransmitterAddFlightParams;
  private Uav3iTransmitterResultAskExecutionClientEndpoint uav3iTransmitterResultAskExecution;
  //-----------------------------------------------------------------------------
//  public Client2VetoWebsocketFacade() throws DeploymentException, IOException, URISyntaxException
  public Client2VetoWebsocketFacade()
  {
    while(!Launcher.connected)
    {
      try
      {
        connect();
      }
      catch (DeploymentException | IOException | URISyntaxException e)
      {
      }
      try { Thread.sleep(500); } catch (InterruptedException e) {}
    }
  }
  //-----------------------------------------------------------------------------
  private void connect() throws DeploymentException, IOException, URISyntaxException
  {
    // Connection to server.
    String baseURI = "ws://" + UAV3iSettings.getVetoServerIP()+":" + UAV3iSettings.getVetoServerPort() + "/berisuas";

    register                           = new RegisterClientEndpoint(new URI(baseURI + "/Register"));
    config                             = new ConfigClientEndpoint(new URI(baseURI + "/Config"));
    paparazziTransmitterCommunicate    = new PaparazziTransmitterCommunicateClientEndpoint(new URI(baseURI + "/PaparazziTransmitterCommunicate"));
    paparazziTransmitterExecute        = new PaparazziTransmitterExecuteClientEndpoint(new URI(baseURI + "/PaparazziTransmitterExecute"));
    paparazziTransmitterClear          = new PaparazziTransmitterClearClientEndpoint(new URI(baseURI + "/PaparazziTransmitterClear"));
    uav3iTransmitterAddUavDataPoint    = new Uav3iTransmitterAddUavDataPointClientEndpoint(new URI(baseURI + "/Uav3iTransmitterAddUavDataPoint"));
    uav3iTransmitterUpdateWayPoint     = new Uav3iTransmitterUpdateWayPointClientEndpoint(new URI(baseURI + "/Uav3iTransmitterUpdateWayPoint"));
    uav3iTransmitterAddFlightParams    = new Uav3iTransmitterAddFlightParamsClientEndpoint(new URI(baseURI + "/Uav3iTransmitterAddFlightParams"));
    uav3iTransmitterResultAskExecution = new Uav3iTransmitterResultAskExecutionClientEndpoint(new URI(baseURI + "/Uav3iTransmitterResultAskExecution"));

    register.register();
    
    config.getConfig("flight_plan");
    // TODO le fichier XML airframe est utile uniquement pour trouver le "default circle radius" : absent dans le cas d'un rotorcraft... 
    config.getConfig("airframe");
    // TODO le fichier XML des messages Ivy ne doit être utilsé que côté serveur... à confirmer !
    //config.getConfig("ivy_messages");
    
    Launcher.connected = true;
  }
  //-----------------------------------------------------------------------------
  public static void disconnect()
  {
    Launcher.connected = false;
    FlightPlanFacade.init();
    UAVModel.reinit();
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

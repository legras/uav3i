package com.deev.interaction.uav3i.veto.communication.websocket;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.ConfigServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.PaparazziTransmitterClearServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.PaparazziTransmitterCommunicateServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.PaparazziTransmitterExecuteServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.RegisterServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddFlightParamsServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddUavDataPointServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterResultAskExecutionServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterUpdateWayPointServerEndpoint;

import fr.dgac.ivy.IvyException;

/**
 * Classe instanciée côté serveur dans le cas d'une communication websocket :<br/>
 * Elle instancie les endpoints serveurs qui se mettent en écoute de leur équivalents
 * côté clients.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class Veto2ClientWebsocketFacade
{
  //-----------------------------------------------------------------------------
  private static boolean connected = false;
  //-----------------------------------------------------------------------------
  public Veto2ClientWebsocketFacade() throws DeploymentException, IvyException
  {
    // Lecture du fichier de configuration pour le système de logs.
    System.setProperty("java.util.logging.config.file", "uav3i_logging.properties");
    
    Class<?>[] endpoints =
    {
      RegisterServerEndpoint.class,
      ConfigServerEndpoint.class,
      PaparazziTransmitterCommunicateServerEndpoint.class,
      PaparazziTransmitterExecuteServerEndpoint.class,
      PaparazziTransmitterClearServerEndpoint.class,
      Uav3iTransmitterAddUavDataPointServerEndpoint.class,
      Uav3iTransmitterUpdateWayPointServerEndpoint.class,
      Uav3iTransmitterAddFlightParamsServerEndpoint.class,
      Uav3iTransmitterResultAskExecutionServerEndpoint.class
    };
    
    Server server = new Server(UAV3iSettings.getVetoServerIP(),   // hostname or IP address
                               UAV3iSettings.getVetoServerPort(), // port
                               "/berisuas",                       // context path
                               null,                              // properties
                               endpoints);                        // endpoint(s)
    server.start();
    
    PaparazziTransmitterWebsocket.getInstance();
  }
  //-----------------------------------------------------------------------------
  /**
   * @return the connected
   */
  public static boolean isConnected()
  {
    return connected;
  }
  //-----------------------------------------------------------------------------
  /**
   * @param connected the connected to set
   */
  public static void setConnected(boolean connected)
  {
    Veto2ClientWebsocketFacade.connected = connected;
  }
  //-----------------------------------------------------------------------------
  public static void register()
  {
    try
    {
      PaparazziTransmitterWebsocket.getInstance().register();
    }
    catch (IvyException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public static void unRegister()
  {
    try
    {
      PaparazziTransmitterWebsocket.getInstance().unRegister();
    }
    catch (IvyException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.websocket;

import javax.websocket.DeploymentException;

import org.glassfish.tyrus.server.Server;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.ConfigServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.PaparazziTransmitterClearServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.PaparazziTransmitterCommunicateServerEndpoint;
import com.deev.interaction.uav3i.veto.communication.websocket.server.serverEndpoint.PaparazziTransmitterExecuteServerEndpoint;

public class PaparazziTransmitterLauncher
{
  //-----------------------------------------------------------------------------
  public PaparazziTransmitterLauncher() throws DeploymentException
  {
    Class<?>[] endpoints =
    {
      ConfigServerEndpoint.class,
      PaparazziTransmitterCommunicateServerEndpoint.class,
      PaparazziTransmitterExecuteServerEndpoint.class,
      PaparazziTransmitterClearServerEndpoint.class
    };
    
    Server server = new Server(UAV3iSettings.getVetoServerIP(),   // hostname or IP address
                               UAV3iSettings.getVetoServerPort(), // port
                               "/berisuas",                       // context path
                               null,                              // properties
                               endpoints);                        // endpoint(s)
    server.start();
  }
  //-----------------------------------------------------------------------------
}

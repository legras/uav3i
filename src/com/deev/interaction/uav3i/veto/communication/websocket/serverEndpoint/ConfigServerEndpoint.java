package com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;

@ServerEndpoint(value = "/Config")
public class ConfigServerEndpoint
{
  //-----------------------------------------------------------------------------
  @OnMessage
  public String getConfig(String which) throws FileNotFoundException
  {
    LoggerUtil.LOG.info("Client wants to get config -> " + which);
    String filename = null;
    switch (which.toLowerCase())
    {
      case "flight_plan":
        filename = UAV3iSettings.getPaparazziFlightPlan();
        break;
      case "airframe":
        filename = UAV3iSettings.getPaparazziAirframe();
        break;
      case "ivy_messages":
        filename = UAV3iSettings.getPaparazziIvyMessages();
        break;
    }
    String content = which + "|";
    Scanner scanner = new Scanner(new File(filename));
    while(scanner.hasNext())
      content += scanner.nextLine();
    scanner.close();
    
    return content;
  }
  //-----------------------------------------------------------------------------
}

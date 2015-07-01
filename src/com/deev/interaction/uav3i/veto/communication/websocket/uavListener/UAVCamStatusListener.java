package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import java.io.IOException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.Veto2ClientWebsocketFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddCamStatusServerEndPoint;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterAddUavDataPointServerEndpoint;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

import fr.dgac.ivy.IvyClient;

public class UAVCamStatusListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int indexCAM_LAT, indexCAM_LONG, indexCAM_TARGET_LAT, indexCAM_TARGET_LONG;
  //-----------------------------------------------------------------------------
  public UAVCamStatusListener()
  {
    indexCAM_LAT         = IvyMessagesFacade.getInstance().getFieldIndex("CAM_STATUS", "cam_lat");
    indexCAM_LONG        = IvyMessagesFacade.getInstance().getFieldIndex("CAM_STATUS", "cam_long");
    indexCAM_TARGET_LAT  = IvyMessagesFacade.getInstance().getFieldIndex("CAM_STATUS", "cam_target_lat");
    indexCAM_TARGET_LONG = IvyMessagesFacade.getInstance().getFieldIndex("CAM_STATUS", "cam_target_long");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    // ground CAM_STATUS 202 48.358928 -4.573853 48.359401 -4.573541
    String tokens = args[1];
    String[] message = tokens.split(" ");
    
    double camLat        = Double.parseDouble(message[indexCAM_LAT]);
    double camLong       = Double.parseDouble(message[indexCAM_LONG]);
    double camTargetLat  = Double.parseDouble(message[indexCAM_TARGET_LAT]);
    double camTargetLong = Double.parseDouble(message[indexCAM_TARGET_LONG]);
    String message2Client = camLat + "*" + camLong + "*" + camTargetLat + "*" + camTargetLong;
    
    if(Veto2ClientWebsocketFacade.isConnected() && Veto.getVetoState() == VetoState.RECEIVING)
    {
      try
      {
        Uav3iTransmitterAddCamStatusServerEndPoint.addCamStatus(message2Client);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    else
      LoggerUtil.LOG.warning("Je suis en écoute du bus Ivy mais uav3iTransmitter est null et je ne peux rien transmettre..." + this);

    
  }
  //-----------------------------------------------------------------------------
}

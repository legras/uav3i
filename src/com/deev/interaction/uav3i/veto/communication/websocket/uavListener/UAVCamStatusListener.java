package com.deev.interaction.uav3i.veto.communication.websocket.uavListener;

import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;

import fr.dgac.ivy.IvyClient;

public class UAVCamStatusListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int indexCAM_TARGET_LAT, indexCAM_TARGET_LONG;
  private double camTargetLat  = 0;
  private double camTargetLong = 0;
  //-----------------------------------------------------------------------------
  public UAVCamStatusListener()
  {
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

    camTargetLat  = Double.parseDouble(message[indexCAM_TARGET_LAT]);
    camTargetLong = Double.parseDouble(message[indexCAM_TARGET_LONG]);
  }
  //-----------------------------------------------------------------------------
  public double getCamTargetLat()  { return camTargetLat;  }
  public double getCamTargetLong() { return camTargetLong; }
  //-----------------------------------------------------------------------------
}

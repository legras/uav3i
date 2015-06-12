package com.deev.interaction.uav3i.veto.communication.websocket;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.veto.communication.PaparazziCommunication;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class PaparazziWebsocketCommunication extends PaparazziCommunication
{
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

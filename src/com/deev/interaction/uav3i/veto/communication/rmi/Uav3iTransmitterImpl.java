package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.UAVWayPoint;
import com.deev.interaction.uav3i.ui.MainFrame;
import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.ui.SymbolMap;
import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public class Uav3iTransmitterImpl implements IUav3iTransmitter
{
  //-----------------------------------------------------------------------------
  @Override
  public void addUAVDataPoint(int  utm_east,
                              int  utm_north, 
                              int  utm_zone, 
                              int  course, 
                              int  alt, 
                              long t) throws RemoteException
  {
      UAVModel.addUAVDataPoint(utm_east, utm_north, utm_zone, course, alt, t);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void addFlightParams(double altitude,
                              double verticalSpeed,
                              double groundAltitude,
                              double groundSpeed) throws RemoteException
  {
    UAVModel.setAltitude(altitude);
    UAVModel.setVerticalSpeed(verticalSpeed);
    UAVModel.setGroundSpeed(groundSpeed);
    UAVModel.setGroundAltitude(groundAltitude);
    //LoggerUtil.LOG.info("Flight params: altitude = " + altitude + " / ground altitude = " + groundAltitude + " / vertical speed = " + verticalSpeed + " / ground speed = " + groundSpeed);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void updateWayPoint(UAVWayPoint wayPoint) throws RemoteException
  {
    UAVModel.getWayPoints().updateWayPoint(wayPoint);
    LoggerUtil.LOG.info("WayPoint updated: " + wayPoint);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void resultAskExecution(ManoeuverDTO mnvrDTO, boolean result) throws RemoteException
  {
    Manoeuver mnvr = MainFrame.getSymbolMap().findManoeuverByManoeuverDTO(mnvrDTO);
    mnvr.setRequestedStatus(result?ManoeuverRequestedStatus.ACCEPTED:ManoeuverRequestedStatus.REFUSED);
      
    System.out.println("####### Uav3iTransmitterImpl.resultAskExecution(" + mnvrDTO + ", " + result + ")");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void ping() throws RemoteException
  {
    // Rien à faire, le boulot idéal...
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVModel;

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
  public void ping() throws RemoteException
  {
    // Rien à faire, le boulot idéal...
  }
  //-----------------------------------------------------------------------------
}

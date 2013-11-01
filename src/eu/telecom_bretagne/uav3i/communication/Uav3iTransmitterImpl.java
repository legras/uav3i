package eu.telecom_bretagne.uav3i.communication;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.deev.interaction.uav3i.model.UAVDataStore;

public class Uav3iTransmitterImpl extends UnicastRemoteObject implements IUav3iTransmitter
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -2681812926439223331L;
  //-----------------------------------------------------------------------------
  protected Uav3iTransmitterImpl() throws RemoteException
  {
    super();
  }
  //-----------------------------------------------------------------------------
  @Override
  public void addUAVDataPoint(int  utm_east,
                              int  utm_north, 
                              int  utm_zone, 
                              int  course, 
                              int  alt, 
                              long t) throws RemoteException
  {
    UAVDataStore.addUAVDataPoint(utm_east, utm_north, utm_zone, course, alt, t);
  }
  //-----------------------------------------------------------------------------
}

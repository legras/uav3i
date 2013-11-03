package eu.telecom_bretagne.uav3i.communication.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IUav3iTransmitter extends Remote
{
  //-----------------------------------------------------------------------------
  /**
   * Ajout d'une nouvelle position pour le drone.
   * 
   * @param utm_east coordonnée UTM Est
   * @param utm_north coordonnée UTM Nord
   * @param utm_zone fuseau UTM
   * @param course cap du drone
   * @param alt altitude
   * @param t temps
   * @throws RemoteException
   */
  public void addUAVDataPoint(int  utm_east,
                              int  utm_north,
                              int  utm_zone,
                              int  course,
                              int  alt,
                              long t) throws RemoteException;
  //-----------------------------------------------------------------------------
}

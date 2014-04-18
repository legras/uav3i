package com.deev.interaction.uav3i.veto.communication.rmi;

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
  public void addGroundLevel(double groundLevel, double verticalSpeed) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Appelé par l'appli côté Paparazzi pour savoir si le client est en vie. La méthode
   * ne fait rien mais son appel réussi indique qur tout va bien...
   * 
   * @throws RemoteException
   */
  public void ping() throws RemoteException;
  //-----------------------------------------------------------------------------
}

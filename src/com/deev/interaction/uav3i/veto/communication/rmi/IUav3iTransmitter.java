package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.deev.interaction.uav3i.model.UAVWayPoint;

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
  /**
   * Ajout d'une nouvelle position pour le drone (version Rotocraft).
   * 
   * @param lat
   * @param lon
   * @param c
   * @param alt
   * @param t
   * @throws RemoteException
   */
  public void addUAVDataPoint(int lat, int lon, int c, int alt, long t) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Transmission au logiciel côté table des paramètres de vol du drone.
   * 
   * @param altitude
   * @param verticalSpeed
   * @param groundAltitude
   * @param groundSpeed
   * @throws RemoteException
   */
  public void addFlightParams(double altitude,
                              double verticalSpeed,
                              double groundAltitude, 
                              double groundSpeed) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour d'un WayPoint.
   * 
   * @param wayPoint
   * @throws RemoteException
   */
  public void updateWayPoint(UAVWayPoint wayPoint) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Transmission du résultat de la demande de la demande d'exécution
   * d'une manoeuvre.
   * 
   * @param idMnvr
   * @param result
   * @throws RemoteException
   */
  public void resultAskExecution(int idMnvr, boolean result) throws RemoteException;
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

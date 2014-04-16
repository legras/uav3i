package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

import uk.me.jstott.jcoord.LatLng;

public interface IPaparazziTransmitter extends Remote
{
  //-----------------------------------------------------------------------------
  /**
   * Mise à jour du rayon (en mètres ?) pour le vol circulaire.
   * @param radius rayon en mètres.
   * @throws RemoteException
   */
  public void setNavRadius(double radius) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Déplacement d'un waypoint défini dans le plan de vol.
   * 
   * @param waypointName nom du waypoint (attribut <code>name</code> de l'élément <code>&lt;waypoint&gt:</code>).
   * @param coordinate nouvelles coordonnées en {@link LatLng} du point.
   * @throws RemoteException
   */
  public void moveWayPoint(String waypointName, LatLng coordinate) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Demande d'exécution d'un template défini dans le plan de vol.
   * 
   * @param blockName nom du template de vol à exécuter.
   * @throws RemoteException
   */
  public void jumpToBlock(String blockName) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * uav3i signale qu'il est prêt : la partie cliente de PaparazziTransmitter va
   * pouvoir se connecter à la partie serveur de uav3i.
   * 
   * @param uav3iHostname nom ou adresse IP de la machine uav3i.
   * @param uav3iPort numéro de port où uav3i se met en écoute.
   * @throws RemoteException
   */
  public void register(String uav3iHostname, int uav3iPort) throws RemoteException;
  //-----------------------------------------------------------------------------
//  public boolean submitManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException;
  //-----------------------------------------------------------------------------
  public void communicateManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException;
  //-----------------------------------------------------------------------------
  public boolean executeManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException;
  //-----------------------------------------------------------------------------
}
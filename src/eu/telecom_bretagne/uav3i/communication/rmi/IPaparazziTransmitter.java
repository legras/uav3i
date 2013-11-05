package eu.telecom_bretagne.uav3i.communication.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
  public void register(String hostname, int port) throws RemoteException;
  //-----------------------------------------------------------------------------
}

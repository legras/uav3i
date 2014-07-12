package com.deev.interaction.uav3i.veto.communication.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

public interface IPaparazziTransmitter extends Remote
{
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
  /**
   * Communication d'une manoeuvre au responsable du vol pour affichage sur
   * l'interface Veto.
   * 
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   * @throws RemoteException
   */
  public void communicateManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Exécution de la manoeuvre sur Paparazzi.
   * 
   * @param mnvrDTO objet DTO (transfert de données) représentant les données de
   *                la manoeuvre.
   * @throws RemoteException
   */
  public void executeManoeuver(ManoeuverDTO mnvrDTO) throws RemoteException;
  //-----------------------------------------------------------------------------
  /**
   * Demande d'effacement d'une manoeuvre sur l'interface Veto.
   */
  public void clearManoeuver() throws RemoteException;
  //-----------------------------------------------------------------------------
}
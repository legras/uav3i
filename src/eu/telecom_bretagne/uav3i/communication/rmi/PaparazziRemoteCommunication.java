package eu.telecom_bretagne.uav3i.communication.rmi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import uk.me.jstott.jcoord.LatLng;
import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.communication.PaparazziCommunication;

/**
 * Organe de transmission, <i><b>côté uav3i</b></i>, des communications entre <b>uav3i</b> et <b>Paparazzi</b>.
 * <pre>
 * +-------+ +-----------------------+                      +--------------------------+ +--------------+     +-----------+
 * |       |-| Uav3iTransmitterImpl  |<-30001---------------|     IUav3iTransmitter    |-|              |     |           |
 * |       | +-----------------------+         (b)          |          (Remote)        | |              |     |           |
 * | uav3i | +-----------------------+                      +--------------------------+ |    uav3i     |     | Paparazzi |
 * |       |-| IPaparazziTransmitter |                      +--------------------------+ | PaparaziSide |     |           |
 * |       | |       (Remote)        |---------------30000->| PaparazziTransmitterImpl |-|              |     |           |
 * +-------+ +-----------------------+         (c)          +--------------------------+ +--------------+     +-----------+
 *                                                                                           .    |              .    |
 *                                                                                          / \   |  (d)        / \   |
 *                                                                                      (a)  |   \ /             |   \ /
 *                                                                                           |    .              |    .
 *                                                                                      +--------------------------------+
 *                                                                                      |              Bus Ivy           |
 *                                                                                      +--------------------------------+
 *                                                                                      
 * <................................>                      <.............................................................>
 *         (Machine UAV3I)                                                       (Machine Paparazzi)
 *         
 * a : La position GPS du drone est récupérée à partir du le bus Ivi par la partie d'uav3i
 *     qui fonctionne du côté de Paparazzi. 
 * b : La position est transmise à la partie serveur d'uav3i qui met à jour la position du
 *     drone sur l'IHM (RMI).
 *     Méthode invocable à distance : addUAVDataPoint(...).
 * c : Les mises à jour de la mission du drone effectuées sur l'IHM sont transmises à la
 *     partie d'uav3i côté Paparazzi (RMI).
 *     Méthodes invocables à distance : setNavRadius(...), moveWayPoint(...) et jumpToBlock(...).
 * d : À la réception, la mise à jour est relayée sur le bus Ivy à destination de Paparazzi.
 * </pre>
 * 
 * @author Philippe TANGUY
 */
public class PaparazziRemoteCommunication extends PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  //Uav3iTransmitterImpl uav3iTransmitterImpl;
  IPaparazziTransmitter paparazziTransmitter;
  //-----------------------------------------------------------------------------
  public PaparazziRemoteCommunication() throws RemoteException, NotBoundException
  {
    if(UAV3iSettings.getMultihomedHost())
    {
      // Pour une utilisation avec VMware...
      // Le fonctionnement de RMI est problématique sur des machines avec plusieurs
      // adresses IP (cas d'un ordinateur hébergeant une machine virtuelle) :
      // l'adresse IP utilisée pour les stubs RMI n'est alors pas la bonne.
      // Voir : http://www.chipkillmar.net/2011/06/22/multihomed-hosts-and-java-rmi/
      // Il faut alors, côté serveur, renseigner les propiétés système
      // "java.rmi.server.hostname" et "java.rmi.server.useLocalHostName".
      // Deux options sont possibles :
      //   - Définir les propriétés de manière programmatique : System.setProperty(...).
      //   - Lancer le serveur avec les oprions -Djava.rmi.server.hostname=<adresse IP serveur>
      //     et -Djava.rmi.server.useLocalHostName=true.
      System.setProperty("java.rmi.server.hostname",         UAV3iSettings.getUav3iServerIP());
      System.setProperty("java.rmi.server.useLocalHostName", "true");
    }

    // Enregistrement de la partie serveur : PaparazziTransmitter pourra se connecter à uav3i.
    int portNumber = UAV3iSettings.getUav3iServerPort(); 
    Registry localRegistry = LocateRegistry.createRegistry(portNumber);
    IUav3iTransmitter skeleton = (IUav3iTransmitter) UnicastRemoteObject.exportObject(new Uav3iTransmitterImpl(),
                                                                                      portNumber);
    localRegistry.rebind(UAV3iSettings.getUav3iServerServiceName(), skeleton);
    System.out.println("####### " + UAV3iSettings.getUav3iServerServiceName() + " started on port " + portNumber + ".");

    // Connexion en tant que client : uav3i se connecte à PaparazziTransmitter.
    Registry remoteRegistry = LocateRegistry.getRegistry(UAV3iSettings.getVetoServerIP(),
                                                         UAV3iSettings.getVetoServerPort());
    //paparazziTransmitter  = (IPaparazziTransmitter) remoteRegistry.lookup("PaparazziTransmitter");
    paparazziTransmitter  = (IPaparazziTransmitter) remoteRegistry.lookup(UAV3iSettings.getVetoServerServiceName());
    // On signale à PaparazziTansmitter qu'il peut maintenant se connecter à uav3i :
    // on lui transmet l'@ IP d'uav3i et le numéro de port où il écoute.
    paparazziTransmitter.register(UAV3iSettings.getUav3iServerIP(),
                                  UAV3iSettings.getUav3iServerPort());
//    while (true)
//    {
//      try { Thread.sleep(Long.MAX_VALUE); }
//      catch (InterruptedException e) {}
//    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void setNavRadius(double radius)
  {
    try
    {
      paparazziTransmitter.setNavRadius(radius);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void moveWayPoint(String waypointName, LatLng coordinate)
  {
    try
    {
      paparazziTransmitter.moveWayPoint(waypointName, coordinate);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void jumpToBlock(String blockName)
  {
    try
    {
      paparazziTransmitter.jumpToBlock(blockName);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

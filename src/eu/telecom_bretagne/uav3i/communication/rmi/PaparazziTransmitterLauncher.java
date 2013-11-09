package eu.telecom_bretagne.uav3i.communication.rmi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import fr.dgac.ivy.IvyException;

/**
 * Organe de transmission, <i><b>côté Paparazzi</b></i>, des communications entre <b>uav3i</b> et <b>Paparazzi</b>.
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
public class PaparazziTransmitterLauncher
{
  //-----------------------------------------------------------------------------
  public PaparazziTransmitterLauncher() throws IvyException, RemoteException, NotBoundException
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
      System.setProperty("java.rmi.server.hostname",         UAV3iSettings.getVetoServerIP());
      System.setProperty("java.rmi.server.useLocalHostName", "true");
    }

    // Enregistrement de la partie serveur : uav3i pourra se connecter à PaparazziTransmitter
    int portNumber = UAV3iSettings.getVetoServerPort();
    Registry localRegistry = LocateRegistry.createRegistry(portNumber);
    IPaparazziTransmitter skeleton = (IPaparazziTransmitter) UnicastRemoteObject.exportObject(new PaparazziTransmitterImpl(),
                                                                                              portNumber);
    localRegistry.rebind(UAV3iSettings.getVetoServerServiceName(), skeleton);
    System.out.println("####### " + UAV3iSettings.getVetoServerServiceName() + " started on port " + portNumber + ".");
  }
  //-----------------------------------------------------------------------------
  public static void main(String[] args)
  {
    try
    {
      new PaparazziTransmitterLauncher();
    }
    catch (RemoteException | IvyException | NotBoundException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

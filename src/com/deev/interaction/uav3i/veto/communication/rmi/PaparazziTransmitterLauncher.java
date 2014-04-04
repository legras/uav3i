package com.deev.interaction.uav3i.veto.communication.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
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
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class PaparazziTransmitterLauncher
{
  //-----------------------------------------------------------------------------
  static
  {
    /*
     * Permet de fixer facilement le timeout lors de la connexion sur la partie
     * serveur. Le timeout doit être positif ou nul. La valeur 0 indique un
     * timeout infini.
     * À explorer : la valeur maxi semble être de 20 secondes. Toute valeur
     * supérieure (comme la valeur 0 = infini) ne génère pas de timout supérieur.
     * Dépendant de la plateforme ?
     */
    try
    {
      RMISocketFactory.setSocketFactory(new RMISocketFactory()
      {
        private int timeout = 1000;
        public Socket createSocket(String host, int port) throws IOException
        {
          Socket socket = new Socket();
          socket.setSoTimeout(timeout);
          socket.setSoLinger(false, 0);
          socket.connect(new InetSocketAddress(host, port), timeout);
          return socket;
        }

        public ServerSocket createServerSocket(int port) throws IOException
        {
          return new ServerSocket(port);
        }
      });
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public PaparazziTransmitterLauncher() throws IvyException, RemoteException, NotBoundException
  {
    // Lecture du fichier de configuration pour le système de logs.
    System.setProperty("java.util.logging.config.file", "uav3i_logging.properties");
    
    if(UAV3iSettings.getMultihomedHost())
    {
      // Pour une utilisation avec VMware...
      // Le fonctionnement de RMI est problématique sur des machines avec plusieurs
      // adresses IP (cas d'un ordinateur hébergeant une machine virtuelle) :
      // l'adresse IP utilisée pour les stubs RMI n'est alors pas la bonne.
      // Voir : http://www.chipkillmar.net/2011/06/22/multihomed-hosts-and-java-rmi/
      // Il faut alors, côté serveur, renseigner les propriétés système
      // "java.rmi.server.hostname" et "java.rmi.server.useLocalHostName".
      // Deux options sont possibles :
      //   - Définir les propriétés de manière programmatique (choix opéré ici) : System.setProperty(...).
      //   - Lancer le serveur avec les oprions -Djava.rmi.server.hostname=<adresse IP serveur>
      //     et -Djava.rmi.server.useLocalHostName=true.
      System.setProperty("java.rmi.server.hostname",         UAV3iSettings.getVetoServerIP());
      System.setProperty("java.rmi.server.useLocalHostName", "true");
      LoggerUtil.LOG.config("PaparazziTransmitterLauncher -> configuration multihomed host");
    }

    // Enregistrement de la partie serveur : uav3i pourra se connecter à PaparazziTransmitter
    int portNumber = UAV3iSettings.getVetoServerPort();
    Registry localRegistry = LocateRegistry.createRegistry(portNumber);
    IPaparazziTransmitter skeleton = (IPaparazziTransmitter) UnicastRemoteObject.exportObject(new PaparazziTransmitterImpl(),
                                                                                              portNumber);
    localRegistry.rebind(UAV3iSettings.getVetoServerServiceName(), skeleton);
    LoggerUtil.LOG.info(UAV3iSettings.getVetoServerServiceName() + " started on port " + portNumber + ".");
  }
  //-----------------------------------------------------------------------------
}

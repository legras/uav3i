package com.deev.interaction.uav3i.veto.communication.rmi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JOptionPane;

import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.communication.PaparazziCommunication;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;

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
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class PaparazziRemoteCommunication extends PaparazziCommunication
{
  //-----------------------------------------------------------------------------
  IPaparazziTransmitter paparazziTransmitter;
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
      LoggerUtil.LOG.config("PaparazziRemoteCommunication -> configuration multihomed host");
    }

    // Enregistrement de la partie serveur : PaparazziTransmitter pourra se connecter à uav3i.
    int portNumber = UAV3iSettings.getUav3iServerPort(); 
    Registry localRegistry = LocateRegistry.createRegistry(portNumber);
    IUav3iTransmitter skeleton = (IUav3iTransmitter) UnicastRemoteObject.exportObject(new Uav3iTransmitterImpl(),
                                                                                      portNumber);
    localRegistry.rebind(UAV3iSettings.getUav3iServerServiceName(), skeleton);
    LoggerUtil.LOG.info(UAV3iSettings.getUav3iServerServiceName() + " started on port " + portNumber + ".");

    // Connexion en tant que client : uav3i se connecte à PaparazziTransmitter.
    Registry remoteRegistry = LocateRegistry.getRegistry(UAV3iSettings.getVetoServerIP(),
                                                         UAV3iSettings.getVetoServerPort());
    try
    {
      paparazziTransmitter  = (IPaparazziTransmitter) remoteRegistry.lookup(UAV3iSettings.getVetoServerServiceName());
    }
    catch (ConnectIOException cioe)
    {
      JOptionPane.showMessageDialog(null,
                                    "<html>The veto must be running on "+UAV3iSettings.getVetoServerIP()+" : "+UAV3iSettings.getVetoServerPort()+"<br><br><i>"+cioe.getMessage(),
                                    "Impossible to connect to Veto",
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(-1);
    }
    // On signale à PaparazziTansmitter qu'il peut maintenant se connecter à uav3i :
    // on lui transmet l'@ IP d'uav3i et le numéro de port où il écoute.
    paparazziTransmitter.register(UAV3iSettings.getUav3iServerIP(),
                                  UAV3iSettings.getUav3iServerPort());
  }
  //-----------------------------------------------------------------------------
  @Override
  public void communicateManoeuver(ManoeuverDTO mnvrDTO)
  {
    try
    {
      paparazziTransmitter.communicateManoeuver(mnvrDTO);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(int idMnvr)
  {
    try
    {
      paparazziTransmitter.executeManoeuver(idMnvr);
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void executeManoeuver(Manoeuver mnvr)
  {
    // Not used in PAPARAZZI_REMOTE mode.
  }
  //-----------------------------------------------------------------------------
  @Override
  public void clearManoeuver()
  {
    try
    {
      paparazziTransmitter.clearManoeuver();
    }
    catch (RemoteException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
}

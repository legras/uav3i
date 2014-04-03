package eu.telecom_bretagne.uav3i.veto.ui;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.communication.rmi.PaparazziTransmitterLauncher;
import fr.dgac.ivy.IvyException;

public class Veto extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -5099360406109028956L;
  //-----------------------------------------------------------------------------
  public Veto()
  {
    super("uav3i - Veto");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(1280, 800);
    this.setLocationRelativeTo(null);
    final JMapViewer mapViewer = new JMapViewer();
    
    mapViewer.setDisplayPositionByLatLon(UAV3iSettings.getInitialLatitude(),
                                         UAV3iSettings.getInitialLongitude(),
                                         UAV3iSettings.getInitialZoom());
    mapViewer.setTileSource(new BingAerialTileSource());
    
    this.getContentPane().add(mapViewer);
    
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

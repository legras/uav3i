package eu.telecom_bretagne.uav3i.veto.ui;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

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
    try
    {
      setIconImage(ImageIO.read(this.getClass().getResource("/com/deev/interaction/uav3i/ui/img/3i_icon_small.png")));
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
    
    final JMapViewer mapViewer = new JMapViewer();
    mapViewer.setDisplayPositionByLatLon(UAV3iSettings.getInitialLatitude(),
                                         UAV3iSettings.getInitialLongitude(),
                                         UAV3iSettings.getInitialZoom());
    mapViewer.setTileSource(new BingAerialTileSource());
    switch (UAV3iSettings.getMapType())
    {
      case MAPNIK:
        mapViewer.setTileSource(new OsmTileSource.Mapnik()); // Default value
        break;
      case BING_AERIAL:
        mapViewer.setTileSource(new BingAerialTileSource());
        break;
      case OSM_CYCLE_MAP:
        mapViewer.setTileSource(new OsmTileSource.CycleMap());
        break;
      case OFF_LINE:
        mapViewer.setTileSource(new OfflineOsmTileSource(UAV3iSettings.getOffLinePath(),
                                                         UAV3iSettings.getOffLineMinZoom(), 
                                                         UAV3iSettings.getOffLineMaxZoom()));
    }

    
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

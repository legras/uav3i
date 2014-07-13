package com.deev.interaction.uav3i.veto.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OfflineOsmTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.ComponentLayer;
import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.ui.FlightParamsPanel;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
import com.deev.interaction.uav3i.veto.communication.rmi.PaparazziTransmitterLauncher;

import fr.dgac.ivy.IvyException;

public class Veto2 extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 4871323813688143568L;
  
//  private JPanel contentPane;
  private static JMapViewer        mapViewer;
  private static SymbolMapVeto     symbolMapVeto;
  private        ComponentLayer    clayer;
  private        FlightParamsPanel flightParamsPanel;
  private        int               diffWidth, diffHeight;
  private        Dimension         initialDimension = new Dimension(1024, 768);
  public static  StateVeto         state         = StateVeto.IDLE;
  public static  Veto2             frame;
  //-----------------------------------------------------------------------------
  public enum StateVeto {IDLE, RECEIVING}
  //-----------------------------------------------------------------------------
  /**
   * Create the frame.
   */
  public Veto2()
  {
    super("uav3i - Veto");
    
    // Affichage du dialogue de confirmation de fermeture
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    this.addWindowListener(new WindowAdapter()
    { 
      public void windowClosing(WindowEvent we)
      {
        askIfReallyQuit();
      }

      public void windowActivated(WindowEvent e)
      {            
        getContentPane().requestFocus();  
      }
    });
    
    // Icone
    try
    {
      setIconImage(ImageIO.read(this.getClass().getResource("/com/deev/interaction/uav3i/ui/img/3i_icon_small.png")));
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }

    // Gestion du redimensionnement de la fenêtre
    addComponentListener(new ComponentAdapter()
    {
      @Override
      public void componentResized(ComponentEvent e)
      {
        Rectangle bounds = ((Component)e.getSource()).getBounds();
        Rectangle newBounds = new Rectangle(bounds.width - diffWidth, bounds.height - diffHeight);
        
        mapViewer.setBounds    (0, 0, newBounds.width, newBounds.height);
        symbolMapVeto.setBounds(0, 0, newBounds.width, newBounds.height);
        clayer.setBounds       (0, 0, newBounds.width, newBounds.height);
        flightParamsPanel.setBounds(newBounds.width-(flightParamsPanel.getWidth()+5),
                                    newBounds.height-(flightParamsPanel.getHeight()+15), 
                                    flightParamsPanel.getWidth(), 
                                    flightParamsPanel.getHeight());

      }
    });


    // Initialisation de JMapViewer
    mapViewer = new JMapViewer(UAV3iSettings.getInteractionMode());
    LatLng startPoint = FlightPlanFacade.getInstance().getStartPoint();
    mapViewer.setDisplayPositionByLatLon(startPoint.getLat(),
                                         startPoint.getLng(),
                                         UAV3iSettings.getTrajectoryZoom() - 3);
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
    UAVScope scope = new UAVScope();
    mapViewer.addMapMarker(scope);
    mapViewer.setBounds(0, 0, initialDimension.width, initialDimension.height);

    // Initialisation de la couche affichant les données 3i : manoeuvres, drone, etc.
    symbolMapVeto = new SymbolMapVeto();
    symbolMapVeto.setBounds(0, 0, initialDimension.width, initialDimension.height);

    // Initialisation de la couche affichant les composants graphiques : boutons, paramètres de vol, etc.
    clayer = new ComponentLayer();
    clayer.setBounds(0, 0, initialDimension.width, initialDimension.height);
    flightParamsPanel = new FlightParamsPanel();
    clayer.add(flightParamsPanel);
    flightParamsPanel.setBounds(initialDimension.width-(flightParamsPanel.getWidth()+5),
                                initialDimension.height-(flightParamsPanel.getHeight()+15), 
                                flightParamsPanel.getWidth(), 
                                flightParamsPanel.getHeight());
    
    // JLayeredPane accueille les différenches couches de composants
    JLayeredPane lpane = new JLayeredPane();
    lpane.setPreferredSize(initialDimension);
    lpane.add(symbolMapVeto,  0);
    lpane.add(mapViewer,     10);
    lpane.add(clayer,        new Integer(20));

    this.getContentPane().add(lpane);

    pack();

    Animator.addComponent(symbolMapVeto);
    Animator.go();

    this.setLocationRelativeTo(null);
    
    try
    {
      new PaparazziTransmitterLauncher();
    }
    catch (RemoteException | IvyException | NotBoundException e)
    {
      e.printStackTrace();
    }
    this.setLocationRelativeTo(null);

    frame = this;

    diffWidth  = this.getSize().width - initialDimension.width;
    diffHeight = this.getSize().height - initialDimension.height;
  }
  //-----------------------------------------------------------------------------
  //public static JMapViewer    getMapViewer()     { return osmMapGround.getMapViewer(); }
  public static JMapViewer    getMapViewer()     { return mapViewer; }
  public static SymbolMapVeto getSymbolMapVeto() { return symbolMapVeto;               }
  //-----------------------------------------------------------------------------
  public static void reinit()
  {
    symbolMapVeto.reinit();
    UAVModel.reinit();
  }
  //-----------------------------------------------------------------------------
  private void askIfReallyQuit()
  {
    int response = JOptionPane.showConfirmDialog(this,
                                                 "<html><b>Are you sure?</b<br>This will stop the communication between<br>the table screen and Paparazzi!",
                                                 "Stop the Veto User Interface",
                                                 JOptionPane.YES_NO_OPTION,
                                                 JOptionPane.WARNING_MESSAGE);
    if(response == 0)
      System.exit(0);
  }
  //-----------------------------------------------------------------------------
}

package com.deev.interaction.uav3i.veto.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

import com.deev.interaction.touch.Animator;
import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.ui.maps.OsmMapGround;
import com.deev.interaction.uav3i.veto.communication.rmi.PaparazziTransmitterLauncher;

import fr.dgac.ivy.IvyException;

public class Veto extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -5099360406109028956L;
  
  private static OsmMapGround  osmMapGround  = null;
  private static SymbolMapVeto symbolMapVeto = null;
  public static  StateVeto     state = StateVeto.IDLE;
  //-----------------------------------------------------------------------------
  public enum StateVeto {IDLE, RECEIVING}
  //-----------------------------------------------------------------------------
  public Veto()
  {
    super("uav3i - Veto");
    this.addWindowListener(new WindowAdapter()
    { 
      public void windowClosing(WindowEvent we)
      {
        System.exit(0);
      }

      public void windowActivated(WindowEvent e)
      {            
        getContentPane().requestFocus();  
      }
    });
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
    
    this.getContentPane().setPreferredSize(new Dimension(960, 600));
    this.pack();
    
    JLayeredPane lpane = new JLayeredPane();
    
    osmMapGround = new OsmMapGround();
    osmMapGround.setBounds(0, 0, 960, 600);
    lpane.add(osmMapGround, 10);

    symbolMapVeto = new SymbolMapVeto();
    symbolMapVeto.setBounds(0, 0, 960, 600);
    lpane.add(symbolMapVeto, 0);

    this.getContentPane().add(lpane);
    
    Animator.addComponent(symbolMapVeto);
    //Animator.addAnimation(osmMapGround);
    Animator.go();


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
  public static JMapViewer    getMapViewer()     { return osmMapGround.getMapViewer(); }
  public static SymbolMapVeto getSymbolMapVeto() { return symbolMapVeto;               }
  //-----------------------------------------------------------------------------
  public static void reinit()
  {
    symbolMapVeto.reinit();
    UAVModel.reinit();
  }
  //-----------------------------------------------------------------------------
}

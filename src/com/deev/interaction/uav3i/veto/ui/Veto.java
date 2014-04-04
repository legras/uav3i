package com.deev.interaction.uav3i.veto.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;

import com.deev.interaction.uav3i.ui.maps.OsmMapGround;
import com.deev.interaction.uav3i.veto.communication.rmi.PaparazziTransmitterLauncher;

import fr.dgac.ivy.IvyException;

public class Veto extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -5099360406109028956L;
  protected static OsmMapGround osmMapGround = null;
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
    
    //JLayeredPane lpane = this.getLayeredPane();
    
    osmMapGround = new OsmMapGround();
    //lpane.add(osmMapGround,-20);
    
//    SymbolMapVeto symbolMapVeto = new SymbolMapVeto();
    //lpane.add(symbolMapVeto);
//    this.getContentPane().add(symbolMapVeto);
    
    this.getContentPane().add(osmMapGround);
    
    //lpane.setVisible(true);

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
  
//  new Thread(new Test()).start();
//  private class Test implements Runnable
//  {
//
//    @Override
//    public void run()
//    {
//      JMapViewer map = osmMapGround.getMapViewer();
//      while(true)
//      {
//        try { Thread.sleep(1000); } catch (InterruptedException e) {}
//        Point center = map.getCenter();
//        Coordinate coordinate = map.getPosition();
//        
//        System.out.println("####### center coordinate = " +  coordinate);
//      }
//    }
//    
//  }
}

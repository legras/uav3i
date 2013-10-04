package com.deev.interaction.uav3i.ui;

import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.deev.interaction.common.ui.DIModernPlaf;
import com.deev.interaction.uav3i.model.MediaStorefront;
import com.deev.interaction.uav3i.model.UAVDataStore;

/**
 * @author legras
 *
 */
public class Launcher
{
	public static boolean TUIO = false;
	public static boolean FULLSCREEN = false;
	
	/**
	 * @param args
	 * @throws IvyException
	 */
	public static void main(String[] args)
	{		
		final String domain = "224.5.6.7:8910";
		Launcher.TUIO = true;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			  public void run()
			  {	
				DIModernPlaf.initModernLookAndFeel();
				
				final MainFrame frame = new MainFrame();
				frame.setVisible(true);	
				frame.requestFocusInWindow();
				if (FULLSCREEN)
					java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
			  }
		});

		// Lancement avec replay (infos dans le fichier).
		UAVDataStore.initialize(UAVDataStore.class.getResourceAsStream("13_10_01__10_41_07.data"));

		// Lancement en écoute sur le bus Ivy des infos transmises par Paparazzi.
		// TODO A finaliser, rien ne s'affiche si Paparazzi n'est pas lancé.
		//UAVDataStore.initialize();

		MediaStorefront.start();
		MediaStorefront.testFill();
	}
}

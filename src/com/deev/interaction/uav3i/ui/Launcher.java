package com.deev.interaction.uav3i.ui;

import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.SwingUtilities;

import com.deev.interaction.common.ui.DIModernPlaf;
import com.deev.interaction.uav3i.model.MediaStorefront;
import com.deev.interaction.uav3i.model.UAVModel;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.paparazzi_settings.flight_plan.FlightPlanFacade;

/**
 * @author legras
 *
 */
public class Launcher
{

	/**
	 * @param args
	 * @throws IvyException
	 */
	public static void main(String[] args)
	{		
		// final String domain = "224.5.6.7:8910";
	  
    // Lecture du fichier de configuration pour le système de logs.
    System.setProperty("java.util.logging.config.file", "uav3i_logging.properties");

	   switch (UAV3iSettings.getMode())
	    {
	      case REPLAY: // Lancement avec replay (infos dans le fichier).
	        UAVModel.initialize(UAVModel.class.getResourceAsStream("13_10_01__10_41_07.data"));
	        break;
//	      case IVY: // Lancement en écoute sur le bus Ivy des infos transmises par Paparazzi.
//	        // TODO A finaliser, rien ne s'affiche si Paparazzi n'est pas lancé.
//	        UAVDataStore.initialize();
	      case PAPARAZZI_DIRECT:
	        UAVModel.initialize();
	        break;
	      case PAPARAZZI_REMOTE:
	        UAVModel.initialize();
	        break;
	      default:
	        break;
	    }

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{	
				// DIModernPlaf.initModernLookAndFeel();

				final MainFrame frame = new MainFrame();
				frame.setVisible(true);	
				frame.requestFocusInWindow();
				if (UAV3iSettings.getFullscreen())
					java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
			}
		});

//		switch (UAV3iSettings.getMode())
//		{
//			case REPLAY: // Lancement avec replay (infos dans le fichier).
//				UAVDataStore.initialize(UAVDataStore.class.getResourceAsStream("13_10_01__10_41_07.data"));
//				break;
////			case IVY: // Lancement en écoute sur le bus Ivy des infos transmises par Paparazzi.
////				// TODO A finaliser, rien ne s'affiche si Paparazzi n'est pas lancé.
////				UAVDataStore.initialize();
//			case PAPARAZZI_DIRECT:
//        UAVDataStore.initialize();
//			  break;
//			case PAPARAZZI_REMOTE:
//        UAVDataStore.initialize();
//        break;
//			default:
//				break;
//		}

		MediaStorefront.start();
		MediaStorefront.testFill();
	}
}

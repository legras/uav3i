package com.deev.interaction.uav3i.ui;

import javax.swing.SwingUtilities;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.VideoModel;
import com.deev.interaction.uav3i.veto.ui.Veto;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import fr.dgac.ivy.IvyException;

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
				UAVModel.initialize(UAVModel.class.getResourceAsStream("pentrez.data"));
				VideoModel.initialize();
				break;
			case PAPARAZZI_DIRECT:
			case PAPARAZZI_REMOTE:
				UAVModel.initialize();
				VideoModel.initialize();
		    SwingUtilities.invokeLater(new Runnable()
		    {
		      public void run()
		      { 
		        final MainFrame frame = new MainFrame();
		        frame.setVisible(true); 
		        frame.requestFocusInWindow();
		        if (UAV3iSettings.getFullscreen())
		          java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
		      }
		    });
				break;
			case VETO:
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          { 
            final Veto frame = new Veto();
            frame.setVisible(true); 
            frame.requestFocusInWindow();
            
            //UAVModel.initialize();
          }
        });
			  break;
			default:
				break;
		}

	}
}

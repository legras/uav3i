package com.deev.interaction.uav3i.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import com.deev.interaction.uav3i.model.UAVModel;
import com.deev.interaction.uav3i.model.VideoModel;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.veto.ui.Veto;
import com.deev.interaction.uav3i.veto.ui.Veto;

import fr.dgac.ivy.IvyException;

/**
 * @author legras
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

		// TODO améliorer la gestion des différents cas de figure...
		switch (UAV3iSettings.getMode())
		{
			case REPLAY: // Lancement avec replay (infos dans le fichier).
				UAVModel.initialize(UAVModel.class.getResourceAsStream("pentrez.data"));
				VideoModel.initialize();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{ 
						final MainFrame frame = new MainFrame();
						frame.setVisible(true); 
						frame.requestFocusInWindow();
					
						final VideoFrame vframe = new VideoFrame();
						vframe.setVisible(true);
						
						if (UAV3iSettings.getFullscreen())
						{
							GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
							GraphicsDevice[] gs = ge.getScreenDevices();
							
							GraphicsDevice primaryGD = ge.getDefaultScreenDevice();
							primaryGD.setFullScreenWindow(frame);

							if (gs.length > 1)
							{
								GraphicsDevice secondaryGD = primaryGD == gs[0] ? gs[1] : gs[0];
								secondaryGD.setFullScreenWindow(vframe);
							}
						}						
					}
				});
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
			  // TODO À tester : l'initialisation d'UAVModel se faisait auparavant dans le contructeur de PaparazziTransmitterImpl. Remis ici pour être conforme au lancement des autres modes.
        UAVModel.initialize();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
            //final Veto frame = new Veto();
            final Veto frame = new Veto();
						frame.setVisible(true); 
						frame.requestFocusInWindow();
					}
				});
				break;
			default:
				break;
		}
		
    // Lancement éventuel du bridge Windows/TUIO (Touch2Tuio_x64.exe)
    if (UAV3iSettings.getTUIO() && System.getProperty("os.name").startsWith("Windows"))
    {
      switch (UAV3iSettings.getMode())
      {
        case REPLAY:
        case PAPARAZZI_DIRECT:
        case PAPARAZZI_REMOTE:
          startTouch2Tuio();
          LoggerUtil.LOG.log(Level.CONFIG, "Bridge Windows/TUIO started (Touch2Tuio_x64.exe)");
        default:
          // Does nothing in other cases
          break;
      }
    }
	}
	
	/**
	 * Touch2Tuio assure la transformation des touchers Windows en touchers TUIO sur
	 * une fenêtre donnée, identifiée par le nom dans sa barre de titre. 
	 */
	private static void startTouch2Tuio()
	{
	  // Si l'outil est lancé avant l'apparation de la fenêtre (ce qui peut être
	  // relativement long avec les initialisations diverses), Touch2Tuio s'arrête
	  // et renvoie l'erreur suivante :
	  //   - Could not install hook for "uav3i"
	  // Délai d'une seconde avant le lancement.
    try { Thread.sleep(1000); } catch (InterruptedException e) {}

	  try
	  {
	    // Creation du processus : "uav3i" est le titre de la fenêtre sur laquelle
	    // Touch2Tuio assurera la traduction touchers Windows/touchers TUIO. Le titre
	    // de la fenêtre est donné dans le contructeur de MainFrame.
	    Process p = Runtime.getRuntime().exec("Touch2Tuio_0.2/Touch2Tuio_x64.exe uav3i");

	    // Récupération des flux : utile ici ? Au lancement, Touch2Tuio affiche "Successfully
	    // installed hook and started TUIO server" qui du coup s'affiche. Aucune erreur
	    // remontée jusqu'à présent...
	    new Thread(new AfficheurFlux(p.getErrorStream())).start();
	    new Thread(new AfficheurFlux(p.getInputStream())).start();

	    // Arrêt du thread courant (bloquage) usqu'à ce que le processus s'arrête : il
	    // ne s'arrête jamais et c'est tant mieux !
	    // Le problème de l'arrêt du fonctionnement de Touch2Tuio n'apparait plus.
	    // Mécanisme derrière ? Voir plus de détails sur :
	    // http://labs.excilys.com/2012/06/26/runtime-exec-pour-les-nuls-et-processbuilder/
	    p.waitFor();

	    System.out.println("####### TUIO problem: 'Touch2Tuio_x64' process stopped when it should not have!");
	  }
	  catch (Exception e)
	  {
	    e.printStackTrace();
	  }
	}

  private static class AfficheurFlux implements Runnable
  {
    private final InputStream inputStream;
    AfficheurFlux(InputStream inputStream)
    {
      this.inputStream = inputStream;
    }
    @Override
    public void run()
    {
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
      String ligne = "";
      try
      {
        while ((ligne = br.readLine()) != null)
        {
          System.out.println("####### Log from Touch2Tuio : " + ligne);
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
  }

	
}

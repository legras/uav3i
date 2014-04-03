package eu.telecom_bretagne.uav3i.veto.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class VetoLauncher extends JFrame
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -5099360406109028956L;
  //-----------------------------------------------------------------------------
  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      { 
        // DIModernPlaf.initModernLookAndFeel();

        final JFrame frame = new VetoLauncher();
        frame.setVisible(true); 
        frame.requestFocusInWindow();
      }
    });
  }
  //-----------------------------------------------------------------------------
}

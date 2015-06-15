package com.deev.interaction.uav3i.veto.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.Animator;
import com.deev.interaction.touch.RoundButton;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.RemoteType;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.veto.communication.rmi.PaparazziTransmitterRMIImpl;
import com.deev.interaction.uav3i.veto.communication.websocket.PaparazziTransmitterWebsocket;
import com.deev.interaction.uav3i.veto.communication.websocket.serverEndpoint.Uav3iTransmitterServerEndpoint;

import fr.dgac.ivy.IvyException;

public class VetoManoeuverButtons implements Animation, ActionListener
{
  //-----------------------------------------------------------------------------
  private enum ManoeuverButtonsStates {SHOWING, HIDING, IDLE};
  private ManoeuverButtonsStates state = ManoeuverButtonsStates.IDLE;
  private static double RATE = .3;

  private static BufferedImage acceptIcon        = null;
  private static BufferedImage acceptIconPressed = null;
  private static BufferedImage refuseIcon        = null;
  private static BufferedImage refuseIconPressed = null;

  private RoundButton acceptButton; 
  private RoundButton refuseButton;
  
  private ManoeuverDTO mnvrDTO;
  private JComponent   layer;
  private int          size;

  private Point2D.Double positions[] = {null, null, null, null};
  private Point2D.Double posVect[] = {null, null, null, null};
  private double         offset = 3000;
  private boolean        isDead = false;

  private static int PAD = 13;
  //-----------------------------------------------------------------------------
  public VetoManoeuverButtons(ManoeuverDTO mnvrDTO, JComponent layer) throws IOException
  {
    if (acceptIcon == null)        acceptIcon        = ImageIO.read(this.getClass().getResource("/img/acceptIcon.png"));
    if (acceptIconPressed == null) acceptIconPressed = ImageIO.read(this.getClass().getResource("/img/acceptIconPressed.png"));
    if (refuseButton == null)      refuseIcon        = ImageIO.read(this.getClass().getResource("/img/refuseIcon.png"));
    if (refuseIconPressed == null) refuseIconPressed = ImageIO.read(this.getClass().getResource("/img/refuseIconPressed.png"));

    this.mnvrDTO = mnvrDTO;
    this.layer   = layer;
    this.size    = acceptIcon.getWidth();
    
    SwingUtilities.invokeLater(new VetoManoeuverButtonsSwingBuilder(this, layer));
    Animator.addAnimation(this);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void actionPerformed(ActionEvent e)
  {
    try
    {
      // Dans les deux cas de figure, on n'a plus besoin des boutons.
      // Attention : les boutons doivent être supprimés AVANT la transmission
      // sur la table (ils ne sont pas 'Serializable') sinon, erreur !
      mnvrDTO.hidebuttons();
      isDead = true;

      if (e.getSource() == acceptButton)
      {
        if(UAV3iSettings.getRemoteType() == RemoteType.RMI)
        {
          // On transmet à la table le résultat de l'évaluation de la manoeuvre
          // par l'opérateur Paparazzi pour mise à jour de l'affichage.
          PaparazziTransmitterRMIImpl.getInstance().getUav3iTransmitter().resultAskExecution(mnvrDTO.getId(), true);
          // On met à jour localement le statut de la manoeuvre pour mise
          // à jour de l'affichage sur le Veto.
          mnvrDTO.setRequestedStatus(ManoeuverRequestedStatus.ACCEPTED);
          // On lance l'exécution de la manoeuvre.
          PaparazziTransmitterRMIImpl.getInstance().startManoeuver(mnvrDTO);
        }
        else if(UAV3iSettings.getRemoteType() == RemoteType.RMI)
        {
          // On transmet à la table le résultat de l'évaluation de la manoeuvre
          // par l'opérateur Paparazzi pour mise à jour de l'affichage.
          Uav3iTransmitterServerEndpoint.resultAskExecution(mnvrDTO.getId(), true);
          // On met à jour localement le statut de la manoeuvre pour mise
          // à jour de l'affichage sur le Veto.
          mnvrDTO.setRequestedStatus(ManoeuverRequestedStatus.ACCEPTED);
          // On lance l'exécution de la manoeuvre.
          PaparazziTransmitterWebsocket.getInstance().startManoeuver(mnvrDTO);
        }
      }
      else if(e.getSource() == refuseButton)
      {
        if(UAV3iSettings.getRemoteType() == RemoteType.RMI)
        {
          // On transmet à la table le résultat de l'évaluation de la manoeuvre par l'opérateur Paparazzi.
          PaparazziTransmitterRMIImpl.getInstance().getUav3iTransmitter().resultAskExecution(mnvrDTO.getId(), false);
        }
        else if(UAV3iSettings.getRemoteType() == RemoteType.RMI)
        {
          // On transmet à la table le résultat de l'évaluation de la manoeuvre par l'opérateur Paparazzi.
          Uav3iTransmitterServerEndpoint.resultAskExecution(mnvrDTO.getId(), false);
        }
        // On met à jour localement le statut de la manoeuvre pour mise
        // à jour de l'affichage sur le Veto.
        mnvrDTO.setRequestedStatus(ManoeuverRequestedStatus.REFUSED);
      }
    }
    catch (RemoteException | IvyException e1)
    {
      e1.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public int tick(int time)
  {
    switch (state)
    {
      case SHOWING:
        offset *= RATE;
        if (offset < 1)
        {
          offset = 0;
          state = ManoeuverButtonsStates.IDLE;
        }
      setBounds();
      break;

      case HIDING:
        offset /= RATE;
        if (offset > 3000)
        {
          state = ManoeuverButtonsStates.IDLE;
        }
        setBounds();
        break;
      case IDLE:
        if (isDead)
          remove();

      default:
        break;
    }

    return 1;
  }
  //-----------------------------------------------------------------------------
  @Override
  public int life()
  {
    return isDead ? 0 : 1;
  }
  //-----------------------------------------------------------------------------
  public void addActionListener(ActionListener listener)
  {
    acceptButton.addActionListener(listener);
    refuseButton.addActionListener(listener);
  }
  //-----------------------------------------------------------------------------
  public void show()
  {
    state = ManoeuverButtonsStates.SHOWING;

//    if (_BUTTONS_SHOWN != this && _BUTTONS_SHOWN != null)
//      _BUTTONS_SHOWN.hide();
//
//    _BUTTONS_SHOWN = this;
  }
  //-----------------------------------------------------------------------------
  public void hide()
  {
    if (offset < 1)
      offset = 1;

    state = ManoeuverButtonsStates.HIDING;
  }
  //-----------------------------------------------------------------------------
  public void remove()
  {
    if (layer == null)
      return;

    layer.remove(acceptButton);
    layer.remove(refuseButton);

    layer = null;
  }
  //-----------------------------------------------------------------------------
  public void setPositions(Point2D.Double ref, double distance, double theta, boolean isArc)
  {
    if (isArc)
    {
      double delta = (size + PAD) / distance;

      for (int i=0; i<4; i++)
      {
        double a = theta + (-1.5+i)*delta;
        positions[i] = new Point2D.Double(ref.x + distance * Math.cos(a), ref.y + distance * Math.sin(a));
      }
    }
    else
    { 
      Point2D.Double middle = new Point2D.Double(ref.x, ref.y);

      middle.x += distance * Math.cos(theta);
      middle.y += distance * Math.sin(theta);

      double u = - (size + PAD) * Math.sin(theta);
      double v =   (size + PAD) * Math.cos(theta);

      double l;

      for (int i=0; i<4; i++)
      {
        l = -1.5 + i;
        positions[i] = new Point2D.Double(middle.x+l*u, middle.y+l*v);
      }
    }

    for (int i=0; i<4; i++) 
    {
      posVect[i] = new Point2D.Double(positions[i].x-ref.x, positions[i].y-ref.y);
      posVect[i].x /= positions[i].distance(ref);
      posVect[i].y /= positions[i].distance(ref);
    }
    
    setBounds();
  }
  //-----------------------------------------------------------------------------
  public void setBounds()
  {
    if (positions[0] == null || posVect[0] == null)
      return;

    if (acceptButton == null || refuseButton == null)
      return;

    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      { 
        acceptButton.setBounds((int) positions[1].x-size/2 + (int) (offset * posVect[1].x),
                               (int) positions[1].y-size/2 + (int) (offset * posVect[1].y),
                               size, size);

        refuseButton.setBounds((int) positions[2].x-size/2 + (int) (offset * posVect[2].x),
                               (int) positions[2].y-size/2 + (int) (offset * posVect[2].y),
                               size, size);
      }
    });
  }
  //-----------------------------------------------------------------------------
  
  
  
  
  

  
  //-----------------------------------------------------------------------------
  class VetoManoeuverButtonsSwingBuilder implements Runnable
  {
    VetoManoeuverButtons buttons;
    JComponent           layer;

    public VetoManoeuverButtonsSwingBuilder(final VetoManoeuverButtons buttons, final JComponent layer)
    {
      this.buttons = buttons;
      this.layer = layer;
    }

    public void run()
    {
      acceptButton = new RoundButton(acceptIcon, acceptIconPressed);
      refuseButton = new RoundButton(refuseIcon, refuseIconPressed);

      layer.add(acceptButton);
      layer.add(refuseButton);

      acceptButton.setBounds(0, 0, size, size);
      refuseButton.setBounds(0, 0, size, size);

      acceptButton.setVisible(true);
      refuseButton.setVisible(true);

      acceptButton.setEnabled(true);
      refuseButton.setEnabled(true);

      buttons.addActionListener(buttons);

      show();
    }
  }
  //-----------------------------------------------------------------------------
}

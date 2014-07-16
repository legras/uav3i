package com.deev.interaction.uav3i.veto.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.deev.interaction.uav3i.veto.ui.Veto.VetoState;

public class VetoStateUI extends JComponent
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 9194094283537195769L;

  private static BufferedImage idleIcon      = null;
  private static BufferedImage receivingIcon = null;
  //-----------------------------------------------------------------------------
  public VetoStateUI()
  {
    if(idleIcon == null)
    {
      try
      {
        idleIcon = ImageIO.read(this.getClass().getResource("img/red.png"));
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    if(receivingIcon == null)
    {
      try
      {
        receivingIcon = ImageIO.read(this.getClass().getResource("img/green.png"));
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    this.setPreferredSize(new Dimension(idleIcon.getWidth(), idleIcon.getHeight()));
  }
  //-----------------------------------------------------------------------------
  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    if(Veto.getVetoState() == VetoState.IDLE)
      g2.drawImage(idleIcon, 0, 0, null);
    else if(Veto.getVetoState() == VetoState.RECEIVING)
      g2.drawImage(receivingIcon, 0, 0, null);
  }
  //-----------------------------------------------------------------------------
  @Override
  public int getWidth()
  {
    return idleIcon.getWidth();
  }
  //-----------------------------------------------------------------------------
  @Override
  public int getHeight()
  {
    return idleIcon.getHeight();
  }
  //-----------------------------------------------------------------------------
}
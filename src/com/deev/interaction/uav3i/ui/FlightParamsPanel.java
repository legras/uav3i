package com.deev.interaction.uav3i.ui;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import javax.swing.JComponent;

import com.deev.interaction.uav3i.model.UAVModel;

public class FlightParamsPanel extends JComponent
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -2034288293344875287L;
  
  private int width  = 300;
  private int heigth = 100;
  Font fontPlain     = new Font("HelveticaNeue-UltraLight", Font.PLAIN, 14);
  Font fontBold      = new Font("HelveticaNeue-UltraLight", Font.BOLD, 14);
  private int col1   = 10;
  private int col2   = 150;
  DecimalFormat df1  = new DecimalFormat("0.0");
  DecimalFormat df2  = new DecimalFormat("0.00");
  DecimalFormat df3  = new DecimalFormat("0.000");
  //-----------------------------------------------------------------------------
  @Override
  public void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHTER));
    g2.fillRoundRect(0, 0, 300, 100, 10, 10);
    g2.setPaint(Palette3i.getPaint(Palette3i.TIME_DARK));
    g2.drawRoundRect(0, 0, 299, 99, 10, 10);

    // Flight params: altitude = 100.001625 / ground altitude = 100.001625 / vertical speed = -0.001025 / ground speed = 12.66
    g2.setPaint(Palette3i.getPaint(Palette3i.TIME_LIGHT_TEXT));
    g2.setFont(fontBold);
    g2.drawString("Ground speed:", col1, 20);
    g2.drawString("Vertical speed:", col1, 40);
    g2.drawString("Altitude:", col1, 60);
    g2.drawString("Ground altitude:", col1, 80);
    g2.setFont(fontPlain);
    g2.drawString(UAVModel.getGroundSpeed() + " m/s (" + df1.format(UAVModel.getGroundSpeed() * 3.6) + " km/h)", col2, 20);
    g2.drawString(df3.format(UAVModel.getVerticalSpeed()) + " m/s", col2, 40);
    g2.drawString(df2.format(UAVModel.getAltitude()) + " m", col2, 60);
    g2.drawString(df2.format(UAVModel.getGroundAltitude()) + " m", col2, 80);
  }
  //-----------------------------------------------------------------------------
  @Override
  public int getWidth()
  {
    return width;
  }
  //-----------------------------------------------------------------------------
  @Override
  public int getHeight()
  {
    return heigth;
  }
  //-----------------------------------------------------------------------------
}

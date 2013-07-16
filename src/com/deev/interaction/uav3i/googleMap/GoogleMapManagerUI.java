package com.deev.interaction.uav3i.googleMap;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.deev.interaction.uav3i.ui.GoogleMap;

/**
 * IHM de l'indicateur de chargement des cartes Google.
 * 
 * @author Philippe TANGUY
*
 */
public class GoogleMapManagerUI extends JPanel
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -5581286287857022427L;
  
  private Image fond = null;
  private Color loaded    = new Color(66, 255, 88);
  private Color notLoaded = new Color(255, 117, 33);
  private Color allLoaded = new Color(0, 127, 14);
  
  //private MapZone mapZone;
  private GoogleMap googleMap;
  
  //private boolean started  = false;
  private boolean X        = false;
  private boolean NW       = false;
  private boolean N        = false;
  private boolean NE       = false;
  private boolean W        = false;
  private boolean E        = false;
  private boolean SW       = false;
  private boolean S        = false;
  private boolean SE       = false;
  private boolean finished = false;
  
  private boolean drawConpleted = false;
  //-----------------------------------------------------------------------------
  /**
   * Create the panel.
   */
  //public GoogleMapManagerUI(MapZone mapZone)
  public GoogleMapManagerUI(GoogleMap googleMap)
  {
    //this.mapZone = mapZone;
    this.googleMap = googleMap;
    setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
  }
  //-----------------------------------------------------------------------------
  @Override
  protected void paintComponent(Graphics g)
  {
    // Création de l'image à la première utilisation.
    if(fond == null)
      fond = new BufferedImage(46, 46, BufferedImage.TYPE_INT_RGB);
    
    // Récupération du contexte graphique.
    Graphics2D g2D = (Graphics2D) fond.getGraphics();
    
    if(!finished)
    {
      init(g2D);
      drawNW(g2D);
      drawN(g2D);
      drawNE(g2D);
      drawW(g2D);
      drawX(g2D);
      drawE(g2D);
      drawSW(g2D);
      drawS(g2D);
      drawSE(g2D);
    }
    else
    {
      drawFinished(g2D);
    }

    g2D.dispose();
    g.drawImage(fond, 1, 1, null);
  }
  //-----------------------------------------------------------------------------
  public synchronized void start()
  {
    //mapZone.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    googleMap.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    NW = N = NE = W = X = E = SW = S = SE = finished = drawConpleted = false;
    notifyAll();
  }
  //-----------------------------------------------------------------------------
  private void init(Graphics2D g2D)
  {
    g2D.setColor(Color.white);
    g2D.fillRect(0, 0, 46, 46);
  }
  //-----------------------------------------------------------------------------
  private void drawNW(Graphics2D g2D)
  {
    if(NW)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(4, 4, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawN(Graphics2D g2D)
  {
    if(N)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(18, 4, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawNE(Graphics2D g2D)
  {
    if(NE)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(32, 4, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawW(Graphics2D g2D)
  {
    if(W)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(4, 18, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawX(Graphics2D g2D)
  {
    if(X)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(18, 18, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawE(Graphics2D g2D)
  {
    if(E)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(32, 18, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawSW(Graphics2D g2D)
  {
    if(SW)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect( 4, 32, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawS(Graphics2D g2D)
  {
    if(S)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(18, 32, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawSE(Graphics2D g2D)
  {
    if(SE)
      g2D.setColor(loaded);
    else
      g2D.setColor(notLoaded);
    g2D.fillRect(32, 32, 10, 10);
  }
  //-----------------------------------------------------------------------------
  private void drawFinished(Graphics2D g2D)
  {
    g2D.setColor(allLoaded);
    g2D.fillRect(0, 0, 46, 46);
    drawConpleted = true;
    //mapZone.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    googleMap.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
  }
  //-----------------------------------------------------------------------------
  public void setNW(boolean nW)             { NW            = nW;       }
  public void setN(boolean n)               { N             = n;        }
  public void setNE(boolean nE)             { NE            = nE;       }
  public void setW(boolean w)               { W             = w;        }
  public void setX(boolean x)               { X             = x;        }
  public void setE(boolean e)               { E             = e;        }
  public void setSW(boolean sW)             { SW            = sW;       }
  public void setS(boolean s)               { S             = s;        }
  public void setSE(boolean sE)             { SE            = sE;       }
  public void setFinished(boolean finished) { this.finished = finished; }
  //-----------------------------------------------------------------------------
  public boolean isDrawConpleted()
  {
    return drawConpleted;
  }
  //-----------------------------------------------------------------------------
  public synchronized void wait4Update()
  {
    if(finished)
    {
      // La construction de la carte complète est une action assez longue (méthode
      // GoogleMapManager.mergeMaps()) quand on la sauvegarde sur le disque et doit
      // en plus se dérouler dans un autre Thread (?) : mergeMaps() a déjà positioné
      // le booléen "finished" à true, ce qui bloque le Thread avant qu'il ait pu
      // lancer la mise à jour finale de l'UI de l'indicateur de chargement
      // Le booléen "allDrawn" est positionné à true quand tout est effectivement
      // dessiné.
      if(drawConpleted)
      {
        // Blocage du thread de surveillance du chargement des cartes (GoogleMapManagerUISupervisor)
        // quand c'est inutile : toutes les cartes sont chargées et l'indicateur
        // est vert.
        try { wait(); } catch (InterruptedException e) { e.printStackTrace(); }
      }
    }
  }
  //-----------------------------------------------------------------------------
}

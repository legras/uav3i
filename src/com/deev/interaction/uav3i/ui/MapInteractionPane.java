package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.CircleAnim;
import com.deev.interaction.common.ui.ImageLighteningAnim;
import com.deev.interaction.common.ui.Touchable;

public class MapInteractionPane extends JComponent implements Touchable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 7524614533712153989L;
  
  //private HashMap<Object, Animation> anims;
  private List<Animation> anims = new ArrayList<Animation>();
  private GoogleMapGround googleMapGround;
  
  private boolean panStarted = false;
  private int panStartX, panStartY;
  private int panDeltaX, panDeltaY;
  private BufferedImage panIcon;
  
  //-----------------------------------------------------------------------------
  public MapInteractionPane(GoogleMapGround googleMapGround)
  {
    this.googleMapGround = googleMapGround;
    try
    {
      panIcon = ImageIO.read(this.getClass().getResource("panIcon.png"));
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public float getInterestForPoint(float x, float y)
  {
    return 100.0f;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void addTouch(float x, float y, Object touchref)
  {
//    CircleAnim circleAnim = new CircleAnim(x, y, Color.RED);
//    Animator.addAnimation(circleAnim);
//    anims.add(circleAnim);
//    System.out.println("MapInteractionPane : c'est moi qu'ait chopé le touch !!!");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void updateTouch(float x, float y, Object touchref)
  {
    // panStarted : indicateur de déplacement dans la carte. Il est positionné à
    // true au début du déplacement et à false à la fin (par la méthode removeTouch).
    if(!panStarted)
    {
      CircleAnim circleAnim = new CircleAnim(x, y, Color.RED);
      Animator.addAnimation(circleAnim);
      anims.add(circleAnim);

      panStarted = true;
      panStartX = (int) x;
      panStartY = (int) y;
    }
    else
    {
      panDeltaX = ((int) x) - panStartX;
      panDeltaY = ((int) y) - panStartY;
      //googleMapGround.setMap(panDeltaX, panDeltaY);
      googleMapGround.panPx(panDeltaX, panDeltaY);
    }

  }
  //-----------------------------------------------------------------------------
  @Override
  public void removeTouch(float x, float y, Object touchref)
  {
    System.out.println("####### removeTouch(" + x + ", " + y + ")");
    panStarted = false;
    anims = new ArrayList<Animation>();
//    CircleAnim circleAnim = new CircleAnim(x, y, Color.BLUE);
//    Animator.addAnimation(circleAnim);
//    anims.add(circleAnim);
    ImageLighteningAnim imageLighteningAnim;
    try
    {
      imageLighteningAnim = new ImageLighteningAnim(ImageIO.read(this.getClass().getResource("panIcon.png")),
                                                    panStartX + panDeltaX - panIcon.getWidth()/2,
                                                    panStartY + panDeltaY - panIcon.getHeight()/2);
      Animator.addAnimation(imageLighteningAnim);
      anims.add(imageLighteningAnim);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
  }
  //-----------------------------------------------------------------------------
  @Override
  public void canceltouch(Object touchref)
  {
    System.out.println("MapInteractionPane : canceltouch()");
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   * La méthode paintComponent n'est pas en charge du rendu de la carte (c'est
   * à la charge de la classe GoogleMapGround) mais uniquement des animations.
   */
  @Override
  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    
    Animation animToBeRemoved = null;
    for(Animation anim : anims)
    {
      animToBeRemoved = (anim.life() < 0 ? anim : null);
      if(anim instanceof ImageLighteningAnim)
        ((ImageLighteningAnim) anim).paintAnimation(g2);
      else if(anim instanceof CircleAnim)
        ((CircleAnim) anim).paintAnimation(g2);
    }
    if(animToBeRemoved != null)
      anims.remove(animToBeRemoved);
    
    if(panStarted)
      g2.drawImage(panIcon,
                   panStartX + panDeltaX - panIcon.getWidth()/2,
                   panStartY+panDeltaY - panIcon.getHeight()/2,
                   null);
  }
  //-----------------------------------------------------------------------------
}

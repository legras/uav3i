package com.deev.interaction.uav3i.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.CircleAnim;
import com.deev.interaction.common.ui.Touchable;

public class MapInteractionPane extends JComponent implements Touchable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 7524614533712153989L;
  
  //private HashMap<Object, Animation> anims;
  private List<CircleAnim> anims = new ArrayList<CircleAnim>();
  private GoogleMapGround googleMapGround;
  
  private boolean panStarted = false;
  private int panStartX, panStartY;
  private int panDeltaX, panDeltaY;
  
  //-----------------------------------------------------------------------------
  public MapInteractionPane(GoogleMapGround googleMapGround)
  {
    this.googleMapGround = googleMapGround;
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
    anims = new ArrayList<CircleAnim>();
    CircleAnim circleAnim = new CircleAnim(x, y, Color.BLUE);
    Animator.addAnimation(circleAnim);
    anims.add(circleAnim);
  }
  //-----------------------------------------------------------------------------
  @Override
  public void canceltouch(Object touchref)
  {
    System.out.println("MapInteractionPane : canceltouch()");
  }
  //-----------------------------------------------------------------------------
  @Override
  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;
    
    for(CircleAnim anim : anims)
    {
      anim.paintAnimation(g2);
    }

//    synchronized (_drawings)
//    {
//      Iterator<FingerDrawing> itr = _drawings.values().iterator();
//      
//      while(itr.hasNext())
//        itr.next().paintAnimation(g2);
//    }
  }
  //-----------------------------------------------------------------------------
}

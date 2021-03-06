package com.deev.interaction.uav3i.ui.maps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.deev.interaction.touch.Animation;
import com.deev.interaction.touch.CircleAnim;
import com.deev.interaction.touch.ImageLighteningAnim;
import com.deev.interaction.touch.Touchable;

public class OsmMapInteractionPanel extends JComponent implements Touchable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 6264763662244222715L;

  private OsmMapGround    osmMapGround;
  private Point           lastDragPoint;
  private List<Animation> anims = new ArrayList<Animation>();
  //-----------------------------------------------------------------------------
  public OsmMapInteractionPanel(OsmMapGround osmMapGround)
  {
    this.osmMapGround = osmMapGround;
  }
  //-----------------------------------------------------------------------------
  @Override
  public float getInterestForPoint(float x, float y)
  {
    return 5.0f;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void addTouch(float x, float y, Object touchref)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public void updateTouch(float x, float y, Object touchref)
  {    
    Point p = new Point((int)x, (int)y);
    if (lastDragPoint != null)
    {
      int diffx = lastDragPoint.x - p.x;
      int diffy = lastDragPoint.y - p.y;
      osmMapGround.getMapViewer().moveMap(diffx, diffy);
    }
    lastDragPoint = p;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void removeTouch(float x, float y, Object touchref)
  {    
    if (lastDragPoint != null)
    {

      lastDragPoint = null;
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void cancelTouch(Object touchref)
  {
  }
  //-----------------------------------------------------------------------------
  /* (non-Javadoc)
   * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
   * La methode paintComponent n'est pas en charge du rendu de la carte (c'est
   * a la charge de la classe GoogleMapGround) mais uniquement des animations.
   */
  @Override
  protected void paintComponent(Graphics g)
  {
    Graphics2D g2 = (Graphics2D) g;

    @SuppressWarnings("unused")
    Animation animToBeRemoved = null;
    for(Animation anim : anims)
    {
      animToBeRemoved = (anim.life() < 0 ? anim : null);
      if(anim instanceof ImageLighteningAnim)
        ((ImageLighteningAnim) anim).paintAnimation(g2);
      else if(anim instanceof CircleAnim)
        ((CircleAnim) anim).paintAnimation(g2);
    }
  }
  //-----------------------------------------------------------------------------
}

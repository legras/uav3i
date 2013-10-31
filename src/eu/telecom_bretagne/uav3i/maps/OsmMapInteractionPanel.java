package eu.telecom_bretagne.uav3i.maps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import com.deev.interaction.common.ui.Animation;
import com.deev.interaction.common.ui.Animator;
import com.deev.interaction.common.ui.CircleAnim;
import com.deev.interaction.common.ui.ImageLighteningAnim;
import com.deev.interaction.common.ui.Touchable;

public class OsmMapInteractionPanel extends JComponent implements Touchable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 6264763662244222715L;

  private OsmMapGround    osmMapGround;
  private Point           lastDragPoint;
  private List<Animation> anims = new ArrayList<Animation>();
  private BufferedImage   panIcon, icon3i;
  //-----------------------------------------------------------------------------
  public OsmMapInteractionPanel(OsmMapGround osmMapGround)
  {
    this.osmMapGround = osmMapGround;

    try
    {
      panIcon = ImageIO.read(this.getClass().getResource("panIcon.png"));
      //icon3i  = ImageIO.read(this.getClass().getResource("3i_icon_small.png"));
      icon3i  = ImageIO.read(this.getClass().getResource("3i_icon.png"));
    }
    catch (IOException e) { e.printStackTrace(); }
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
    //System.out.println("####### updateTouch(" + x + ", " + y + ", ...)");
    
    Point p = new Point((int)x, (int)y);
    if (lastDragPoint != null)
    {
      int diffx = lastDragPoint.x - p.x;
      int diffy = lastDragPoint.y - p.y;
      osmMapGround.getMapViewer().moveMap(diffx, diffy);
      //System.out.println("####### updateTouch --> osmMapGround.getMapViewer().moveMap(" + diffx + ", " + diffy + ")");
    }
    lastDragPoint = p;
  }
  //-----------------------------------------------------------------------------
  @Override
  public void removeTouch(float x, float y, Object touchref)
  {
    //System.out.println("###### removeTouch(" + x + ", " + y + ")");
    
    if (lastDragPoint != null)
    {
      anims = new ArrayList<Animation>();

      ImageLighteningAnim imageLighteningAnim;
      try
      {
        imageLighteningAnim = new ImageLighteningAnim(ImageIO.read(this.getClass().getResource("panIcon.png")),
                                                      lastDragPoint.x - panIcon.getWidth()/2,
                                                      lastDragPoint.y - panIcon.getHeight()/2);
        Animator.addAnimation(imageLighteningAnim);
        anims.add(imageLighteningAnim);
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
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

    // logo 3i
    g2.drawImage(icon3i, 40, 10, null);

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
    
    if(lastDragPoint != null)
      g2.drawImage(panIcon,
                   lastDragPoint.x - panIcon.getWidth()/2,
                   lastDragPoint.y - panIcon.getHeight()/2,
                   null);
  }
  //-----------------------------------------------------------------------------
}

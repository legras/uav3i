package com.deev.interaction.touch;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageLighteningAnim implements Animation
{
  //-----------------------------------------------------------------------------
  private static int           LIFE = 1000;
  
  private        BufferedImage image;
  private        int           life;
  private        int           x, y;
  private        int           alphaValue;
  //-----------------------------------------------------------------------------
  public ImageLighteningAnim(BufferedImage image, int x, int y)
  {
    this.image = image;
    life = LIFE;
    this.x = x;
    this.y = y;
    alphaValue = 255;
  }
  //-----------------------------------------------------------------------------
  public void paintAnimation(Graphics2D g2)
  {
    if (life < 0)
      return;
    g2.drawImage(image, x, y, null);
  }
  //-----------------------------------------------------------------------------
  @Override
  public int tick(int time)
  {
    int decr = (int) (255 * ((double)time/LIFE));
    alphaValue -= decr;
    alphaValue = (alphaValue<=0?0:alphaValue);
    
    for(int w=0; w<image.getWidth(); w++)
    {
      for(int h=0; h<image.getHeight(); h++)
      {
        int rgba = image.getRGB(w, h);
        int alpha = (rgba >>24 ) & 0xFF;
        if(alpha != 0)
        {
          int rouge = (rgba >> 16 ) & 0xFF;
          int vert  = (rgba >> 8 ) & 0xFF;
          int bleu  = rgba & 0xFF;
          int newValue = (alphaValue<<24)+(rouge<<16)+(vert<<8)+bleu;
          image.setRGB(w, h, newValue);
        }
      }
    }
    
    return life -= time;
  }
  //-----------------------------------------------------------------------------
  @Override
  public int life()
  {
    return life;
  }
  //-----------------------------------------------------------------------------
}

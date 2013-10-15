package eu.telecom_bretagne.uav3i;

/**
 * Quelques petits tests sur l'opérateur '>>'. À garder pour le moment...
 * @author Philippe TANGUY
 */
public class Test
{

  public static void main(String[] args)
  {
    // int rouge = (rgba >> 16 ) & 0xFF;
    // int vert  = (rgba >> 8 ) & 0xFF;
    // int bleu  = rgba & 0xFF;
    // int newValue = (alphaValue<<24)+(rouge<<16)+(vert<<8)+bleu;
    // image.setRGB(w, h, newValue);

    int rgba = 1405775525;
    affiche("0xFF", 0xFF);
    affiche("rgba", rgba);
    affiche("(rgba >> 0) & 0xFF", (rgba >> 0) & 0xFF);
    affiche("(rgba >> 8) & 0xFF", (rgba >> 8) & 0xFF);
    affiche("(rgba >> 16) & 0xFF", (rgba >> 16) & 0xFF);
    affiche("(rgba >> 24) & 0xFF", (rgba >> 24) & 0xFF);
 
    affiche("65538", 65538);
    affiche("65538 & 0xFF", 65538 & 0xFF);
    
    //System.out.println(rgba + " --> " + Integer.toBinaryString(rgba));
    //System.out.println(rgba + " --> " + Integer.toHexString(rgba));
    
  }
  
  private static void affiche(String s, int i)
  {
    System.out.println(s + " --> " + Integer.toBinaryString(i));
  }
  
}

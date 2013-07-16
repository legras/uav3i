package com.deev.interaction.uav3i.googleMap;

/**
 * Représentation d'une coordonnée en pixels d'un point sur une carte Google.<br/>
 * <i><u>Note</u></i> : la représentation classique d'un point dans un repère
 * orthonormé donne l'abcisse en première coordonnée et l'ordonnée en seconde
 * -> (x,y). La représentation est ici inversée -> (y,x) pour conserver le sens
 * des coordonnées sur une carte : (latitude <i>[en y]</i>, longitude <i>[en x]</i>).
 * 
 * @author Philippe TANGUY
 */
public class GoogleImageCoordinate
{
  //-----------------------------------------------------------------------------
  /** Coordonnée en pixels de la latitude. */
  public int y;
  /** Coordonnée en pixels de la longitude. */
  public int x;
  //-----------------------------------------------------------------------------
  /**
   * Constructeur.
   * 
   * @param y : Coordonnée en pixels de la latitude.
   * @param x : Coordonnée en pixels de la longitude.
   */
  public GoogleImageCoordinate(int y, int x)
  {
    this.y = y;
    this.x = x;
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "GoogleImageCoordinate [y (latitude) = " + y + ", x (longitude) = " + x + "]";
  }
  //-----------------------------------------------------------------------------
}

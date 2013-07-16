package com.deev.interaction.uav3i.googleMap;

/**
 * Représentation décimale d'une coordonnée en latitude et en longitude.
 * 
 * @author Philippe TANGUY.
 */
public class GoogleMapCoordinate
{
  //-----------------------------------------------------------------------------
  /** Latitude du point. */
  public double latitude;
  /** Longitude du point. */
  public double longitude;
  //-----------------------------------------------------------------------------
  /**
   * Constructeur.
   * 
   * @param latitude :  latitude du point.
   * @param longitude : longitude du point.
   * @throws CoordinateOutOfBoundsException  pour des coordonnées fantaisistes...
   */
  public GoogleMapCoordinate(double latitude, double longitude) throws CoordinateOutOfBoundsException
  {
    if(    latitude > 90 || latitude < -90
        || longitude > 180 || longitude < -180)
      throw new CoordinateOutOfBoundsException("Coordinate values are outside the limits: (lat = " + latitude + ", long = " + longitude + ")");
    this.latitude  = latitude;
    this.longitude = longitude;
  }
  //-----------------------------------------------------------------------------
  public String getCoordinatePair()
  {
    return GoogleMapManager.floor(latitude, 6) + "," + GoogleMapManager.floor(longitude, 6);
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "GoogleMapCoordinate [latitude = " + latitude + ", longitude = " + longitude + "]";
  }
  //-----------------------------------------------------------------------------
}

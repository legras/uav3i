package com.deev.interaction.uav3i.googleMap;

/*
* @author Philippe TANGUY
 */
public enum Maptype
{
  //-----------------------------------------------------------------------------
  ROADMAP("roadmap"),
  SATELLITE("satellite"),
  TERRAIN("terrain"),
  HYBRID("hybrid");
  //-----------------------------------------------------------------------------
  public final String value;
  //-----------------------------------------------------------------------------
  private Maptype(String value)
  {
    this.value = value;
  }
  //-----------------------------------------------------------------------------
}

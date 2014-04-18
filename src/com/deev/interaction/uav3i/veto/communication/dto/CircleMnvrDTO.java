package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;

import uk.me.jstott.jcoord.LatLng;

public class CircleMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 8781703792280948005L;
  //-----------------------------------------------------------------------------
  private LatLng _center;
  private double _currentRm = 500.;
  //-----------------------------------------------------------------------------
  public CircleMnvrDTO(LatLng center, double currentRm)
  {
    _center    = center;
    _currentRm = currentRm;
  }
  //-----------------------------------------------------------------------------
  public LatLng getCenter()    { return _center;    }
  public double getCurrentRm() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public void paint(Graphics2D g2)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "CircleMnvrDTO [_center=" + _center + ", _currentRm=" + _currentRm + "]";
  }
  //-----------------------------------------------------------------------------
}

package eu.telecom_bretagne.uav3i.communication.dto;

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
  public LatLng get_center()    { return _center;    }
  public double get_currentRm() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "CircleMnvrDTO [_center=" + _center + ", _currentRm=" + _currentRm + "]";
  }
  //-----------------------------------------------------------------------------
}

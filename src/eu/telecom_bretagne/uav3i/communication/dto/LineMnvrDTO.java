package eu.telecom_bretagne.uav3i.communication.dto;

import uk.me.jstott.jcoord.LatLng;

public class LineMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -3630611531901790503L;
  // Points de la zone à regarder
  private LatLng _A, _B;
  // Distance entre la zone à regarder et la trajectoire
  private double _currentRm = 500.;
  //-----------------------------------------------------------------------------
  public LineMnvrDTO(LatLng A, LatLng B, double currentRm)
  {
    _A         = A;
    _B         = B;
    _currentRm = currentRm;
  }
  //-----------------------------------------------------------------------------
  public LatLng getA()         { return _A;         }
  public LatLng getB()         { return _B;         }
  public double getCurrentRm() { return _currentRm; }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "LineMnvrDTO [_A=" + _A + ", _B=" + _B + ", _currentRm=" + _currentRm + "]";
  }
  //-----------------------------------------------------------------------------
}

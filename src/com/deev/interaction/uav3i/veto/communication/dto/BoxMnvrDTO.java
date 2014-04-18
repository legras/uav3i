package com.deev.interaction.uav3i.veto.communication.dto;

import java.awt.Graphics2D;

import uk.me.jstott.jcoord.LatLng;

public class BoxMnvrDTO extends ManoeuverDTO
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = -4968937130195723726L;
  // Points de la zone à regarder
  private LatLng _A, _B;
  private boolean _isNorthSouth = true; // else is East-West
  //-----------------------------------------------------------------------------
  public BoxMnvrDTO(LatLng A, LatLng B, boolean isNorthSouth)
  {
    _A            = A;
    _B            = B;
    _isNorthSouth = isNorthSouth;
  }
  //-----------------------------------------------------------------------------
  public LatLng  get_A()        { return _A;            }
  public LatLng  get_B()        { return _B;            }
  public boolean isNorthSouth() { return _isNorthSouth; }
  //-----------------------------------------------------------------------------
  @Override
  public void paint(Graphics2D g2)
  {
  }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "BoxMnvrDTO [_A=" + _A + ", _B=" + _B + ", _isNorthSouth=" + _isNorthSouth + "]";
  }
  //-----------------------------------------------------------------------------
}

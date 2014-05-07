package com.deev.interaction.uav3i.model;

import java.io.Serializable;

import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;

import uk.me.jstott.jcoord.LatLng;

public class UAVWayPoint implements Serializable
{
  //-----------------------------------------------------------------------------
  private static final long serialVersionUID = 1544931872905688488L;

  private int    wayPointId;
  private String wayPointName;
  private LatLng wayPointPosition;
  private double altitude;
  //-----------------------------------------------------------------------------
  public UAVWayPoint(int wayPointId, double lat, double lng, double altitude)
  {
    this.wayPointId       = wayPointId;
    this.wayPointName     = FlightPlanFacade.getInstance().getWaypoint(wayPointId).getName();
    this.wayPointPosition = new LatLng(lat, lng);
    this.altitude         = altitude;
  }
  //-----------------------------------------------------------------------------
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(altitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + wayPointId;
    result = prime * result + ((wayPointPosition == null) ? 0 : wayPointPosition.hashCode());
    return result;
  }
  //-----------------------------------------------------------------------------
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)                  return true;
    if (obj == null)                  return false;
    if (getClass() != obj.getClass()) return false;
    
    UAVWayPoint other = (UAVWayPoint) obj;
    
    if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
      return false;
    
    if (wayPointId != other.wayPointId)
      return false;
    if (wayPointPosition == null)
    {
      if (other.wayPointPosition != null)
        return false;
    }
    //else if (!wayPointPosition.equals(other.wayPointPosition))
    // equals(...) is not defined inside the LatLng class so the equality
    // between values for latitude ang longitude are tested.
    else if (!(wayPointPosition.getLat() == other.wayPointPosition.getLat() &&  wayPointPosition.getLng() == other.wayPointPosition.getLng()))
      return false;
    return true;
  }
  //-----------------------------------------------------------------------------
  public int    getWayPointId()       { return wayPointId;       }
  public String getWayPointName()     { return wayPointName;     }
  public LatLng getWayPointPosition() { return wayPointPosition; }
  public double getAltitude()         { return altitude;         }
  //-----------------------------------------------------------------------------
  public void setWayPointId(int wayPointId)                { this.wayPointId       = wayPointId;       }
  public void setWayPointName(String wayPointName)         { this.wayPointName     = wayPointName;     }
  public void setWayPointPosition(LatLng wayPointPosition) { this.wayPointPosition = wayPointPosition; }
  public void setAltitude(double altitude)                 { this.altitude         = altitude;         }
  //-----------------------------------------------------------------------------
  @Override
  public String toString()
  {
    return "UAVWayPoint [wayPointId=" + wayPointId + ", wayPointName=" + wayPointName + ", wayPointPosition=" + wayPointPosition + ", altitude=" + altitude + "]";
  }
  //-----------------------------------------------------------------------------
}
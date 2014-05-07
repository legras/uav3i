package com.deev.interaction.uav3i.model;

import java.util.HashMap;

public class UAVWayPoints
{
  //-----------------------------------------------------------------------------
  private HashMap<Integer, UAVWayPoint> wayPoints;  
  //-----------------------------------------------------------------------------
  public UAVWayPoints()
  {
    wayPoints = new HashMap<>();
  }
//  //-----------------------------------------------------------------------------
//  public void addWayPoint(int wayPointId, double lat, double lng, double altitude)
//  {
//    if(wayPoints.get(wayPointId) == null)
//      wayPoints.put(wayPointId, new UAVWayPoint(wayPointId, lat, lng, altitude));
//  }
//  //-----------------------------------------------------------------------------
//  public void addWayPoint(UAVWayPoint wayPoint)
//  {
//    if(wayPoints.get(wayPoint.getWayPointId()) == null)
//      wayPoints.put(wayPoint.getWayPointId(), wayPoint);
//  }
//  //-----------------------------------------------------------------------------
//  public boolean isWayPointChanged(UAVWayPoint wayPoint)
//  {
//    if(wayPoints.get(wayPoint.getWayPointId()) == null)
//      return true;
//    else
//      return wayPoint.equals(wayPoints.get(wayPoint.getWayPointId()));
//  }
  //-----------------------------------------------------------------------------
  public boolean updateWayPoint(UAVWayPoint wayPoint)
  {
    int wayPointId = wayPoint.getWayPointId();
    if(wayPoints.get(wayPointId) == null)
    {
      wayPoints.put(wayPointId, wayPoint);
      return true;
    }
    else
    {
      if(!wayPoint.equals(wayPoints.get(wayPointId)))
      {
        wayPoints.put(wayPointId, wayPoint);
        return true;
      }
      else
        return false;
    }
    //-----------------------------------------------------------------------------
  }
  //-----------------------------------------------------------------------------
}

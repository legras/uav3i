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
  //-----------------------------------------------------------------------------
  /**
   * Three scenarios exist:
   *   - The waypoint to be updated doen't exist, it is added to the HashMap
   *     (return <code>true</code>).
   *   - The waypoint exists but not modified: no change needs to be done (return
   *     <code>false</code>).
   *   - The waypoint exists and modified: the value is updated in the HashMap
   *     (return <code>true</code>).
   * 
   * @param wayPoint the waypoint to be updated.
   * @return <code>true</code> if the waypoint was added or modified, <code>false</code> otherwise.
   */
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
  }
  //-----------------------------------------------------------------------------
}

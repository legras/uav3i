package eu.telecom_bretagne.uav3i;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UAV3iSettings
{
  //-----------------------------------------------------------------------------
  private static Properties props;
  public static enum Mode    { REPLAY, IVY }
  public static enum MapType { MAPNIK, BING_AERIAL, OSM_CYCLE_MAP }
  //-----------------------------------------------------------------------------
  static
  {
    props = new Properties();
    try { props.load(new FileInputStream("uav3i.properties")); }
    catch (IOException e) { e.printStackTrace(); }
  }
  //-----------------------------------------------------------------------------
  public static Mode getMode()
  {
    String type = props.getProperty("MODE");
    if (type.equalsIgnoreCase("replay"))
      return Mode.REPLAY;
    else if (type.equalsIgnoreCase("ivy"))
      return Mode.IVY;
    else
      return null;
  }
  //-----------------------------------------------------------------------------
  public static MapType getMapType()
  {
    String type = props.getProperty("MAP_TYPE");
    if (type.equalsIgnoreCase("mapnik"))
      return MapType.MAPNIK;
    else if (type.equalsIgnoreCase("bing_aerial"))
      return MapType.BING_AERIAL;
    else if (type.equalsIgnoreCase("osm_cycle_map"))
      return MapType.OSM_CYCLE_MAP;
    else
      return null;
  }
  //-----------------------------------------------------------------------------
  public static int     getInitialZoom()      { return Integer.parseInt(props.getProperty("INITIAL_ZOOM"));         }
  public static int     getTrajectoryZoom()   { return Integer.parseInt(props.getProperty("TRAJECTORY_ZOOM"));      }
  public static double  getInitialLatitude()  { return Double.parseDouble(props.getProperty("INITIAL_LATITUDE"));   }
  public static double  getInitialLongitude() { return Double.parseDouble(props.getProperty("INITIAL_LONGITUDE"));  }
  public static boolean getTUIO()             { return Boolean.parseBoolean(props.getProperty("TUIO"));             }
  public static boolean getFullscreen()       { return Boolean.parseBoolean(props.getProperty("FULLSCREEN"));       }
  public static boolean getInteractionMode()  { return Boolean.parseBoolean(props.getProperty("INTERACTION_MODE")); }
  //-----------------------------------------------------------------------------
}

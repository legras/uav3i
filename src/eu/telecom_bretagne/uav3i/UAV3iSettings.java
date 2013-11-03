package eu.telecom_bretagne.uav3i;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UAV3iSettings
{
  //-----------------------------------------------------------------------------
  private static Properties props;
  //public static enum Mode    { REPLAY, IVY }
  public  static enum Mode    { REPLAY, PAPARAZZI_DIRECT, PAPARAZZI_REMOTE }
  public  static enum MapType { MAPNIK, BING_AERIAL, OSM_CYCLE_MAP, OFF_LINE }
  // Attributs fréquemment lu : pour éviter la lecture du fichier à chaque fois...
  private static Mode    mode           = null;
  private static MapType mapType        = null;
  private static Integer trajectoryZoom = null;
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
    if(mode == null)
    {
      String type = props.getProperty("MODE");
      if (type.equalsIgnoreCase("replay"))
        mode = Mode.REPLAY;
//    else if (type.equalsIgnoreCase("ivy"))
//    return Mode.IVY;
      else if (type.equalsIgnoreCase("paparazzi_direct"))
        mode = Mode.PAPARAZZI_DIRECT;
      else if (type.equalsIgnoreCase("paparazzi_remote"))
        mode = Mode.PAPARAZZI_REMOTE;
      else
        mode = null;
    }
    return mode;
  }
  //-----------------------------------------------------------------------------
  public static MapType getMapType()
  {
    if(mapType == null)
    {
      String type = props.getProperty("MAP_TYPE");
      if (type.equalsIgnoreCase("mapnik"))
        mapType = MapType.MAPNIK;
      else if (type.equalsIgnoreCase("bing_aerial"))
        mapType = MapType.BING_AERIAL;
      else if (type.equalsIgnoreCase("osm_cycle_map"))
        mapType = MapType.OSM_CYCLE_MAP;
      else if (type.equalsIgnoreCase("off_line"))
        mapType = MapType.OFF_LINE;
      else
        return null;
    }
    return mapType;
  }
  //-----------------------------------------------------------------------------
  public static String  getIvyDomainBus()
  {
    String domainBus = props.getProperty("IVY_DOMAIN_BUS");
    if(domainBus.length() == 0 || domainBus.equalsIgnoreCase("null"))
      return null;
    else
      return domainBus;
  }
  public static int getTrajectoryZoom()
  {
    if(trajectoryZoom == null)
      trajectoryZoom = Integer.parseInt(props.getProperty("TRAJECTORY_ZOOM"));
    return trajectoryZoom;
  }
  //-----------------------------------------------------------------------------
  public static String  getOffLinePath()            { return props.getProperty("OFF_LINE_PATH");                          }
  public static int     getOffLineMinZoom()         { return Integer.parseInt(props.getProperty("OFF_LINE_MIN_ZOOM"));    }
  public static int     getOffLineMaxZoom()         { return Integer.parseInt(props.getProperty("OFF_LINE_MAX_ZOOM"));    }
  public static int     getInitialZoom()            { return Integer.parseInt(props.getProperty("INITIAL_ZOOM"));         }
  public static double  getInitialLatitude()        { return Double.parseDouble(props.getProperty("INITIAL_LATITUDE"));   }
  public static double  getInitialLongitude()       { return Double.parseDouble(props.getProperty("INITIAL_LONGITUDE"));  }
  public static boolean getTUIO()                   { return Boolean.parseBoolean(props.getProperty("TUIO"));             }
  public static boolean getFullscreen()             { return Boolean.parseBoolean(props.getProperty("FULLSCREEN"));       }
  public static boolean getInteractionMode()        { return Boolean.parseBoolean(props.getProperty("INTERACTION_MODE")); }
  public static String  getPaparazziFlightPlan()    { return props.getProperty("PAPARAZZI_FLIGHT_PLAN");                  }
  public static String  getPaparazziAirframe()      { return props.getProperty("PAPARAZZI_AIRFRAME");                     }
  public static String  getVetoServerIP()           { return props.getProperty("VETO_SERVER_IP");                         }
  public static int     getVetoServerPort()         { return Integer.parseInt(props.getProperty("VETO_SERVER_PORT"));     }
  public static String  getVetoServerServiceName()  { return props.getProperty("VETO_SERVER_SERVICE_NAME");               }
  public static String  getUav3iServerIP()          { return props.getProperty("UAV3I_SERVER_IP");                        }
  public static int     getUav3iServerPort()        { return Integer.parseInt(props.getProperty("UAV3I_SERVER_PORT"));    }
  public static String  getUav3iServerServiceName() { return props.getProperty("UAV3I_SERVER_SERVICE_NAME");              }
  public static boolean getVMwareDev()              { return Boolean.parseBoolean(props.getProperty("VMWARE_DEV"));       }
  //-----------------------------------------------------------------------------
}

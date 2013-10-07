package eu.telecom_bretagne.uav3i;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class UAV3iSettings
{
	//-----------------------------------------------------------------------------
	private static Properties props;
	public static enum Type    { REPLAY, IVY }
	public static enum MapType { MAPNIK, BING_AERIAL, OSM_CYCLE_MAP }
	//-----------------------------------------------------------------------------
	// FIXME en attendant mieux
	public static boolean TUIO = true;
	public static boolean FULLSCREEN = false;
	//-----------------------------------------------------------------------------
	static
	{
		// Lecure de la clé dans le fichier "StaticMapsAPIKey.properties" (attribut "key")
		props = new Properties();
		try { props.load(new FileInputStream("uav3i.properties")); }
		catch (IOException e) { e.printStackTrace(); }
	}
	//-----------------------------------------------------------------------------
	public static Type getType()
	{
		String type = props.getProperty("TYPE");
		if (type.equalsIgnoreCase("replay"))
			return Type.REPLAY;
		else if (type.equalsIgnoreCase("ivy"))
			return Type.IVY;
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
	public static int getInitialZoom()      { return Integer.parseInt(props.getProperty("INITIAL_ZOOM"));      }
	public static int getInitialLatitude()  { return Integer.parseInt(props.getProperty("INITIAL_LATITUDE"));  }
	public static int getInitialLongitude() { return Integer.parseInt(props.getProperty("INITIAL_LONGITUDE")); }
	//-----------------------------------------------------------------------------
}

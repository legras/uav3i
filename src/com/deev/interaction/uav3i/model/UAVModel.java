package com.deev.interaction.uav3i.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.websocket.DeploymentException;

import uk.me.jstott.jcoord.LatLng;

import com.deev.interaction.uav3i.ui.BoxMnvr;
import com.deev.interaction.uav3i.ui.CircleMnvr;
import com.deev.interaction.uav3i.ui.LineMnvr;
import com.deev.interaction.uav3i.ui.Manoeuver;
import com.deev.interaction.uav3i.ui.Manoeuver.ManoeuverRequestedStatus;
import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.UAV3iSettings.Mode;
import com.deev.interaction.uav3i.util.UAV3iSettings.RemoteType;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.airframe.AirframeFacade;
import com.deev.interaction.uav3i.veto.communication.Client2VetoFacade;
import com.deev.interaction.uav3i.veto.communication.direct.Client2VetoDirectCommunication;
import com.deev.interaction.uav3i.veto.communication.dto.ManoeuverDTO;
import com.deev.interaction.uav3i.veto.communication.rmi.Client2VetoRMIFacade;
import com.deev.interaction.uav3i.veto.communication.websocket.Client2VetoWebsocketFacade;

public class UAVModel
{
	public  static UAVModel                 store = null;
	private static Client2VetoFacade client2VetoCommunication;
	private        ArrayList<UAVDataPoint>  _dataPoints = new ArrayList<UAVDataPoint>();
	private double altitude;
	private double verticalSpeed;
	private double groundAltitude;
	private double groundSpeed;
	private UAVWayPoints uavWayPoints = new UAVWayPoints();


	public static void initialize(InputStream stream)
	{
		if (store == null)	
			store = new UAVModel(stream);
	}

	public static void initialize()
	{
		if (store == null)
			store = new UAVModel();
	}

	public UAVModel(InputStream stream)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String strLine;
		long delta = 0;

		//Read File Line By Line
		try
		{
			// 4.822 5 GPS 3 39485814 534857683 899 0 0 1 0 204086959 30 3
			//      <message name="GPS" ID="8">
			//      <field name="mode" type="uint8" unit="byte_mask"/>
			//      <field name="utm_east" type="int32" unit="cm"/>       4
			//      <field name="utm_north" type="int32" unit="cm"/>      5
			//      <field name="course" type="int16" unit="decideg"/>    6
			//      <field name="alt" type="int32" unit="cm"/>            7
			//      <field name="speed" type="uint16" unit="cm/s"/>
			//      <field name="climb" type="int16" unit="cm/s"/>
			//      <field name="itow" type="uint32" unit="ms"/>          10 Time ?
			//      <field name="utm_zone" type="uint8"/>                 11 Erreur !
			//      <field name="gps_nb_err" type="uint8"/>
			//    </message>
			// Il doit y avoir une erreur dans la doc !!! A l'évidence le 10ème n'est pas le temps/

			while ((strLine = br.readLine()) != null)
			{
				String[] tokens = strLine.split(" ");

				if (!tokens[2].equalsIgnoreCase("GPS"))
					continue;

				int utm_east = Integer.parseInt(tokens[4]);
				int utm_north = Integer.parseInt(tokens[5]);
				int utm_zone = Integer.parseInt(tokens[12]);
				int course = Integer.parseInt(tokens[6]);
				int alt = Integer.parseInt(tokens[7]);
				long t = Long.parseLong(tokens[11]);

				if (delta == 0)
					delta = System.currentTimeMillis() - t + 2000;

				_dataPoints.add(new UAVDataPoint(utm_east, utm_north, utm_zone, course, alt, t+delta));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Constructeur pour une utilisation de l'<code>UAVDataStore</code> en écoute
	 * du bus Ivy.
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 */
	public UAVModel()
	{
		switch (UAV3iSettings.getMode())
		{
		case PAPARAZZI_DIRECT:
			client2VetoCommunication = new Client2VetoDirectCommunication();
			break;
		case PAPARAZZI_REMOTE:
		  if(UAV3iSettings.getRemoteType() == RemoteType.RMI)
		  {
	      try
	      {
	        client2VetoCommunication = new Client2VetoRMIFacade();
	      }
	      catch (RemoteException e)
	      {
	        LoggerUtil.LOG.log(Level.SEVERE, e.getMessage());
	        //e.printStackTrace();
	      }
		  }
		  else if(UAV3iSettings.getRemoteType() == RemoteType.WEBSOCKET)
		  {
        try
        {
          client2VetoCommunication = new Client2VetoWebsocketFacade();
        }
        catch (DeploymentException | IOException | URISyntaxException e)
        {
          LoggerUtil.LOG.log(Level.SEVERE, e.getMessage());
          //e.printStackTrace();
        }
		  }
			break;
		default:
			break;
		}
	}

	public static void reinit()
	{
		if(store != null)
		{
			store._dataPoints = new ArrayList<UAVDataPoint>();
			store.uavWayPoints = new UAVWayPoints();
		}
	}

  public static void addUAVDataPoint(int utm_east, int utm_north, int utm_zone, int course, int alt, long t)
  {
    if (store != null)
    {
      store._dataPoints.add(new UAVDataPoint(utm_east, utm_north, utm_zone, course, alt, t));
    }
  }

  public static void addUAVDataPoint(int lat, int lon, int c, int alt, long t)
  {
    if (store != null)
    {
      store._dataPoints.add(new UAVDataPoint(lat, lon, c, alt, t));
    }
  }

//	public static void addUAVDataPoint(UAVDataPoint point)
//	{
//	  if(store != null)
//	  {
//	    store._dataPoints.add(point);
//	  }  
//	}

	public static UAVDataPoint getDataPointAtTime(long time)
	{
		if (store._dataPoints == null || store._dataPoints.size() <= 1)
			return null;

		int index = store.getIndexBeforeTime(time);
		// TODO il faudra interpoler entre index et index+1

		return store._dataPoints.get(index);
	}

	public static UAVDataPoint getDataPointNow()
	{
		if (store._dataPoints == null || store._dataPoints.size() < 1)
			return null;
		else
			return store._dataPoints.get(store._dataPoints.size()-1);
	}

	public static LatLng getLatLngAtTime(long time)
	{
		UAVDataPoint point = getDataPointAtTime(time);

		if (point == null)
			return null;
		else
			return point.latlng;
	}

	public static LatLng getLatLngNow()
	{
		UAVDataPoint point = getDataPointNow();

		if (point == null)
			return null;
		else
			return point.latlng;
	}

	/**
	 * @param time
	 * @return course in degrees
	 */
	public static double getCourseAtTime(long time)
	{
		UAVDataPoint point = getDataPointAtTime(time);

		if (point == null)
			return 0.;
		else
			return point.course;
	}

	/**
	 * @return course in degrees
	 */
	public static double getCourseNow()
	{
		UAVDataPoint point = getDataPointNow();

		if (point == null)
			return 0.;
		else
			return point.course;
	}

	private int getIndexBeforeTime(long time)
	{
		int index;

		long startT = _dataPoints.get(0).time;
		if (time < startT)
			return 0;

		int last = _dataPoints.size()-1;
		long endT = _dataPoints.get(last).time;
		if (time > endT)
			return last;

		index = (int) (time-startT) / (int) (endT-startT) * last;

		if (index < 0) return 0;
		if (index > last) return last;

		while (_dataPoints.get(index).time > time) index--;
		while (_dataPoints.get(index+1).time < time) index ++;

		return index;
	}

	public static boolean isEmpty()
	{
		if(store != null)
		{
			//return store._dataPoints.size() == 0;
			return store._dataPoints.size() <= 1;
		}
		else
			return false;
	}

	public static void communicateManoeuver(Manoeuver mnvr)
	{
		// Communication of a manoeuver is only needed in PAPARAZZI_REMOTE mode.
		if(UAV3iSettings.getMode() == Mode.PAPARAZZI_REMOTE)
		{
			LoggerUtil.LOG.log(Level.INFO, "Communication of manoeuver to Veto requested");
			client2VetoCommunication.communicateManoeuver(mnvr.toDTO());
		}
	}

	public static void executeManoeuver(Manoeuver mnvr)
	{
		mnvr.setRequestedStatus(ManoeuverRequestedStatus.ASKED);
		switch (UAV3iSettings.getMode())
		{
			case REPLAY:
				LoggerUtil.LOG.log(Level.INFO, "Exection of manoeuver requested... but we are in REPLAY mode, ignored");
				break;
			case PAPARAZZI_REMOTE:
				LoggerUtil.LOG.log(Level.INFO, "Exection of manoeuver requested");
				client2VetoCommunication.executeManoeuver(mnvr.getId());
				break;
			case PAPARAZZI_DIRECT:
				LoggerUtil.LOG.log(Level.INFO, "Exection of manoeuver requested");
				client2VetoCommunication.executeManoeuver(mnvr);
				break;
			default:
				break;
		}
	}

	public static void clearManoeuver()
	{
		if (UAV3iSettings.getMode() == Mode.REPLAY)
		{
			LoggerUtil.LOG.log(Level.INFO, "Clear manoeuver requested... but we are in REPLAY mode, ignored");
			return;
		}

		client2VetoCommunication.clearManoeuver();
	}

	/**
	 * @return reference cruise speed (m/s) for ETA calculations
	 */
	public static double getReferenceCruiseSpeed()
	{
		return 30.;
	}

	/**
	 * @return the altitude
	 */
	public static Double getAltitude()
	{
		if(store == null)
			return null;
		return store.altitude;
	}

	/**
	 * @param altitude the altitude to set
	 */
	public static void setAltitude(double altitude)
	{
		if(store != null)
		{
			store.altitude = altitude;
		}
	}

	/**
	 * @return the verticalSpeed
	 */
	public static Double getVerticalSpeed()
	{
		if(store == null)
			return null;
		return store.verticalSpeed;
	}

	/**
	 * @param verticalSpeed the verticalSpeed to set
	 */
	public static void setVerticalSpeed(double verticalSpeed)
	{
		if(store != null)
		{
			store.verticalSpeed = verticalSpeed;
		}
	}

	/**
	 * @return the groundSpeed
	 */
	public static Double getGroundSpeed()
	{
		if(store == null)
			return null;
		return store.groundSpeed;
	}

	/**
	 * @param groundSpeed the groundSpeed to set
	 */
	public static void setGroundSpeed(double groundSpeed)
	{
		if(store != null)
		{
			store.groundSpeed = groundSpeed;
		}
	}

	/**
	 * @return the groundAltitude
	 */
	public static Double getGroundAltitude()
	{
		if(store == null)
			return null;
		return store.groundAltitude;
	}

	/**
	 * @param groundAltitude the groundAltitude to set
	 */
	public static void setGroundAltitude(double groundAltitude)
	{
		if(store != null)
		{
			store.groundAltitude = groundAltitude;
		}
	}

	public static UAVWayPoints getWayPoints()
	{
		if(store == null)
			return null;
		return store.uavWayPoints;
	}

}

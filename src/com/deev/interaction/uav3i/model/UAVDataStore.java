package com.deev.interaction.uav3i.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

import fr.dgac.ivy.Ivy;

public class UAVDataStore
{
	public static UAVDataStore store = null;
	
	private ArrayList<UAVDataPoint> _dataPoints;
	
	public class UAVDataPoint
	{
		public LatLng latlon;
		public double altitude;
		public double heading;
		public long time;
		
		/**
		 *  <message name="GPS" id="8">
		 *  <ul>
		 *	<li><field name="mode"       type="uint8"  unit="byte_mask"/></li>
		 *	<li><field name="utm_east"   type="int32"  unit="cm" alt_unit="m"/></li>
		 *	<li><field name="utm_north"  type="int32"  unit="cm" alt_unit="m"/></li>
		 *	<li><field name="course"     type="int16"  unit="decideg" alt_unit="deg"/></li>
		 *	<li><field name="alt"        type="int32"  unit="mm" alt_unit="m"/></li>
		 *	<li><field name="speed"      type="uint16" unit="cm/s" alt_unit="m/s"/></li>
		 *	<li><field name="climb"      type="int16"  unit="cm/s" alt_unit="m/s"/></li>
		 *	<li><field name="week"       type="uint16" unit="weeks"/></li>
		 *	<li><field name="itow"       type="uint32" unit="ms"/></li>
		 *	<li><field name="utm_zone"   type="uint8"/></li>
		 *	<li><field name="gps_nb_err" type="uint8"/></li>
		 *	</ul>
		 */
		public UAVDataPoint(int utm_east, int utm_north, int course, int alt, long t)
		{
			UTMRef utm = new UTMRef((double) utm_east/100f, (double) utm_north/100., 'U', 30);
			latlon = utm.toLatLng();
			
			System.out.println(latlon);
			
			altitude = (double) alt / 1000.;
			heading = (double) course / 10.;
			time = t;
		}
	}
	
	public static void initialize(InputStream stream)
	{
		if (store == null)	
			store = new UAVDataStore(stream);
	}
	
	public static void initialize(Ivy bus)
	{
		if (store == null)	
			store = new UAVDataStore(bus);
	}
	
	public UAVDataStore(InputStream stream)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String strLine;
		long delta = 0;
		
		_dataPoints = new ArrayList<UAVDataStore.UAVDataPoint>();
		
		//Read File Line By Line
		try
		{
			// 4.822 5 GPS 3 39485814 534857683 899 0 0 1 0 204086959 30 3
			while ((strLine = br.readLine()) != null)
			{
				String[] tokens = strLine.split(" ");
				
				if (!tokens[2].equalsIgnoreCase("GPS"))
					continue;
				
				int utm_east = Integer.parseInt(tokens[4]);
				int utm_north = Integer.parseInt(tokens[5]);
				int course = Integer.parseInt(tokens[6]);
				int alt = Integer.parseInt(tokens[7]);
				long t = Long.parseLong(tokens[11]);
				
				if (delta == 0)
					delta = System.currentTimeMillis() - t + 2000;
				
				_dataPoints.add(new UAVDataPoint(utm_east, utm_north, course, alt, t+delta));
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// System.out.println(_dataPoints.size() + " UAVDataPoint");
	}
	
	public UAVDataStore(Ivy bus)
	{
		
	}
	
	
}

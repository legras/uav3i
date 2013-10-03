package com.deev.interaction.uav3i.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.omg.CORBA._PolicyStub;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

import fr.dgac.ivy.Ivy;

public class UAVDataStore
{
	public static UAVDataStore store = null;
	
	private ArrayList<UAVDataPoint> _dataPoints;
	
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
		
		_dataPoints = new ArrayList<UAVDataPoint>();
		
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

	public static LatLng getLatLngAtTime(long time)
	{
		int index = store.getIndexBeforeTime(time);
		
		
		return store._dataPoints.get(index).latlng;
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
}

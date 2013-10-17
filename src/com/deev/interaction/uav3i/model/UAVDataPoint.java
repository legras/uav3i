package com.deev.interaction.uav3i.model;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

public class UAVDataPoint
{
	public LatLng latlng;
	public double altitude;
	public double course;
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
	public UAVDataPoint(int utm_east, int utm_north, int c, int alt, long t)
	{
		UTMRef utm = new UTMRef((double) utm_east/100f, (double) utm_north/100., 'U', 30);
		latlng = utm.toLatLng();
				
		altitude = (double) alt / 1000.;
		course = (double) c / 10.;
		time = t;
	}
}
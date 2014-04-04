package com.deev.interaction.uav3i.model;

import com.deev.interaction.uav3i.util.paparazzi_settings.flight_plan.FlightPlanFacade;
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
	public UAVDataPoint(int utm_east, int utm_north, int utm_zone, int c, int alt, long t)
	{
    //UTMRef utm = new UTMRef((double) utm_east/100f, (double) utm_north/100., 'U', 30);
    UTMRef utm = new UTMRef((double) utm_east/100f,
                            (double) utm_north/100.,
                            FlightPlanFacade.getInstance().getUTMLatitudeZoneLetterFlightPlan(), 
                            utm_zone);
		latlng = utm.toLatLng();
				
    // Sens inverse : on reconstruit un UTM à partir du latlong
    UTMRef utm2 = latlng.toUTMRef(); 
    int fuseau = utm2.getLngZone();
    char bande = utm2.getLatZone();
    // L'UTM reconstruit a bien changé la référence de la bande (quand c'est nécessaire...)
    //System.out.println("["+fuseau+","+bande+"] " + (utm2.getLngZone()==utm.getLngZone()?"même fuseau":"FUSEAU DIFFERENT ! : " + utm.getLngZone()) + " / " + (utm2.getLatZone()==utm.getLatZone()?"même bande":"BANDE DIFFERENTE ! : " + utm.getLatZone()) + " latlong = " + latlng);
    //System.out.println("utm = " + utm + " / utm2 = " + utm2);
    //System.out.println("utm_east : " + utm_east + " / utm_north : " + utm_north + " / utm_zone : " + utm_zone);
        
		altitude = (double) alt / 1000.;
		course = (double) c / 10.;
		time = t;
	}
}
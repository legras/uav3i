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
	
	protected LatLng _camTarget;
	protected CameraFootprint _cameraFootPrint = null;
	
	public static boolean DUMMY_CAMERA_FOOTPRINT = true;
	
	/**
	 * <message name="GPS" id="8">
	 *   <field name="mode"       type="uint8"  unit="byte_mask"/>
	 *   <field name="utm_east"   type="int32"  unit="cm" alt_unit="m"/>
	 *   <field name="utm_north"  type="int32"  unit="cm" alt_unit="m"/>
	 *   <field name="course"     type="int16"  unit="decideg" alt_unit="deg"/>
	 *   <field name="alt"        type="int32"  unit="mm" alt_unit="m"/>
	 *   <field name="speed"      type="uint16" unit="cm/s" alt_unit="m/s"/>
	 *   <field name="climb"      type="int16"  unit="cm/s" alt_unit="m/s"/>
	 *   <field name="week"       type="uint16" unit="weeks"/>
	 *   <field name="itow"       type="uint32" unit="ms"/>
	 *   <field name="utm_zone"   type="uint8"/>
	 *   <field name="gps_nb_err" type="uint8"/>
	 * </message>
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
    //UTMRef utm2 = latlng.toUTMRef(); 
    //int fuseau = utm2.getLngZone();
    //char bande = utm2.getLatZone();
    // L'UTM reconstruit a bien changé la référence de la bande (quand c'est nécessaire...)
    //System.out.println("["+fuseau+","+bande+"] " + (utm2.getLngZone()==utm.getLngZone()?"même fuseau":"FUSEAU DIFFERENT ! : " + utm.getLngZone()) + " / " + (utm2.getLatZone()==utm.getLatZone()?"même bande":"BANDE DIFFERENTE ! : " + utm.getLatZone()) + " latlong = " + latlng);
    //System.out.println("utm = " + utm + " / utm2 = " + utm2);
    //System.out.println("utm_east : " + utm_east + " / utm_north : " + utm_north + " / utm_zone : " + utm_zone);
        
		altitude  = (double) alt / 1000.;
		course    = (double) c / 10.;
		time      = t;

		_camTarget = latlng;
		_cameraFootPrint = new CameraFootprint(latlng, course, time);
	}
	
	/**
   * <message name="GPS_INT" id="155">
   *   <field name="ecef_x"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="ecef_y"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="ecef_z"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="lat"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
   *   <field name="lon"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
   *   <field name="alt"     type="int32" unit="mm"   alt_unit="m">altitude above WGS84 reference ellipsoid</field>
   *   <field name="hmsl"    type="int32" unit="mm"   alt_unit="m">height above mean sea level (geoid)</field>
   *   <field name="ecef_xd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="ecef_yd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="ecef_zd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="pacc"    type="uint32" unit="cm"   alt_unit="m"/>
   *   <field name="sacc"    type="uint32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="tow"     type="uint32"/>
   *   <field name="pdop"    type="uint16"/>
   *   <field name="numsv"   type="uint8"/>
   *   <field name="fix"     type="uint8" values="NONE|UKN1|UKN2|3D"/>
   * </message>
   * 
   * @param lat           degree * 10^-6
   * @param lon           degree * 10^-6
   * @param c             degree
   * @param alt           mm
   * @param t             ms
   */
	@Deprecated
  public UAVDataPoint(int lat, int lon, int c, int alt, long t)
  {
    latlng    = new LatLng(lat / 10.e6, lon / 10.e6);
    altitude  = (double) alt / 1000.;
    course    = (double) c;
    time      = t;
    _camTarget = null;
  }

  /**
   * <message name="GPS_INT" id="155">
   *   <field name="ecef_x"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="ecef_y"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="ecef_z"  type="int32" unit="cm"   alt_unit="m"/>
   *   <field name="lat"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
   *   <field name="lon"     type="int32" unit="1e7deg" alt_unit="deg" alt_unit_coef="0.0000001"/>
   *   <field name="alt"     type="int32" unit="mm"   alt_unit="m">altitude above WGS84 reference ellipsoid</field>
   *   <field name="hmsl"    type="int32" unit="mm"   alt_unit="m">height above mean sea level (geoid)</field>
   *   <field name="ecef_xd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="ecef_yd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="ecef_zd" type="int32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="pacc"    type="uint32" unit="cm"   alt_unit="m"/>
   *   <field name="sacc"    type="uint32" unit="cm/s" alt_unit="m/s"/>
   *   <field name="tow"     type="uint32"/>
   *   <field name="pdop"    type="uint16"/>
   *   <field name="numsv"   type="uint8"/>
   *   <field name="fix"     type="uint8" values="NONE|UKN1|UKN2|3D"/>
   * </message>
   * 
   * @param lat           degree * 10^-6
   * @param lon           degree * 10^-6
   * @param c             degree
   * @param alt           mm
   * @param t             ms
   * @param camTargetLat  degree
   * @param camTargetLong degree
   */
  public UAVDataPoint(int lat, int lon, int c, int alt, long t, double camTargetLat, double camTargetLong)
  {
    latlng    = new LatLng(lat / 10.e6, lon / 10.e6);
    altitude  = (double) alt / 1000.;
    course    = (double) c;
    time      = t;
    
    if (DUMMY_CAMERA_FOOTPRINT)
    {
    	_camTarget = latlng;
		_cameraFootPrint = new CameraFootprint(latlng, course, time);
    }
    else
    {
		_camTarget = new LatLng(camTargetLat, camTargetLong);
		_cameraFootPrint = new CameraFootprint(latlng, _camTarget, time);
	}
  }
  
  public LatLng getCameraTarget()
  {
	  return _camTarget;
  }
  
  public CameraFootprint getCameraFootprint()
  {
	  return _cameraFootPrint;
  }
  
  @Override
  public String toString()
  {
    return "UAVDataPoint [latlng = " + latlng + ", altitude = " + altitude + ", course = " + course + ", time = " + time + ", camTarget = " + _camTarget + "]";
  }

}
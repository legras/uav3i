package com.deev.interaction.uav3i.veto.communication.rmi.uavListener;

import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.IvyMessagesFacade;

import fr.dgac.ivy.IvyClient;

/**
 * Listen the "NAV_STATUS" message on the Ivy bus. This listener is used by
 * {@link UAVPositionListenerRotorcraft}.<br/>
 * 
 * Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
 * 
 * <message name="NAV_STATUS" id="13">
 *   <field name="ac_id" type="string"/>
 *   <field name="cur_block"   type="uint8"/>
 *   <field name="cur_stage"   type="uint8"/>
 *   <field name="block_time"  type="uint32"/>
 *   <field name="stage_time"  type="uint32"/>
 *   <field name="target_lat" type="float" unit="deg"/>
 *   <field name="target_long" type="float" unit="deg"/>
 *   <field name="target_climb"   type="float" unit="m/s"/>
 *   <field name="target_alt"     type="float" unit="m"/>
 *   <field name="target_course" type="float" unit="deg"/>     10
 *   <field name="dist_to_wp" type="float" unit="m"/>
 * </message>
 * 
 * Paparazzi server    | ground NAV_STATUS 202 10 0 489 489 48.358936 -4.573732 0.000000 149.000019 -116.759734 0.000000
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class UAVNavStatusListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int lastCourseValue = 0;
  private int indexTARGET_COURSE;
  //-----------------------------------------------------------------------------
  public UAVNavStatusListener()
  {
    indexTARGET_COURSE  = IvyMessagesFacade.getInstance().getFieldIndex("NAV_STATUS", "target_course");
  }
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    //displayArgs(this.getClass().getSimpleName(), client, args);

    String tokens = args[1];
  
    String[] message = tokens.split(" ");
    lastCourseValue = (int) Double.parseDouble(message[indexTARGET_COURSE]);
  }
  //-----------------------------------------------------------------------------
  /**
   * @return the lastCourseValue
   */
  public int getLastCourseValue()
  {
    return lastCourseValue;
  }
  //-----------------------------------------------------------------------------
}

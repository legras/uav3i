package com.deev.interaction.uav3i.veto.communication.uavListener;

import fr.dgac.ivy.IvyClient;

public class UAVNavStatusListener extends UAVListener
{
  //-----------------------------------------------------------------------------
  private int lastCourseValue = 0;
  //-----------------------------------------------------------------------------
  @Override
  public void receive(IvyClient client, String[] args)
  {
    String tokens = args[1];
  
    // Definition of messages in paparazzi_v5.5_devel-559-g6656ca7-dirty in conf/messages.xml
    //
    // <message name="NAV_STATUS" id="13">
    //   <field name="ac_id" type="string"/>
    //   <field name="cur_block"   type="uint8"/>
    //   <field name="cur_stage"   type="uint8"/>
    //   <field name="block_time"  type="uint32"/>
    //   <field name="stage_time"  type="uint32"/>
    //   <field name="target_lat" type="float" unit="deg"/>
    //   <field name="target_long" type="float" unit="deg"/>
    //   <field name="target_climb"   type="float" unit="m/s"/>
    //   <field name="target_alt"     type="float" unit="m"/>
    //   <field name="target_course" type="float" unit="deg"/>     10
    //   <field name="dist_to_wp" type="float" unit="m"/>
    // </message>
    //
    // Paparazzi server    | ground NAV_STATUS 202 10 0 489 489 48.358936 -4.573732 0.000000 149.000019 -116.759734 0.000000
    String[] message = tokens.split(" ");
    lastCourseValue = (int) Double.parseDouble(message[10]);
//    for(int i=0; i<message.length; i++)
//      System.out.println("---------------> " + i + " = " + message[i]);
    // ---------------> 0 = 
    // ---------------> 1 = 202
    // ---------------> 2 = 10
    // ---------------> 3 = 0
    // ---------------> 4 = 1739
    // ---------------> 5 = 1739
    // ---------------> 6 = 48.358974
    // ---------------> 7 = -4.573851
    // ---------------> 8 = 0.000000
    // ---------------> 9 = 149.000019
    // ---------------> 10 = 83.705553
    // ---------------> 11 = 0.000000
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

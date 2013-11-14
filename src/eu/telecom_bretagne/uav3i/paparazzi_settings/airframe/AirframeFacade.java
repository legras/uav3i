package eu.telecom_bretagne.uav3i.paparazzi_settings.airframe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXB;

import eu.telecom_bretagne.uav3i.UAV3iSettings;
import eu.telecom_bretagne.uav3i.paparazzi_settings.airframe.jaxb.Airframe;
import eu.telecom_bretagne.uav3i.paparazzi_settings.airframe.jaxb.Define;
import eu.telecom_bretagne.uav3i.paparazzi_settings.airframe.jaxb.Section;

public class AirframeFacade
{
  //-----------------------------------------------------------------------------
  private static AirframeFacade instance = null;

  private Airframe airframe;
  private Double defaultCircleRadius = null;
  //-----------------------------------------------------------------------------
  private AirframeFacade()
  {
    try
    {
      // Ouverture du fichier schéma XML
     InputStream xmlStream = new FileInputStream(UAV3iSettings.getPaparazziAirframe());
     
     // Désérialisation dans l'arborescence de classes JAXB
     airframe = JAXB.unmarshal(xmlStream, Airframe.class);
     
//     wayPointsIndex = new HashMap<>();
//     processWaypoints();
//     blocksIndex = new HashMap<>();
//     processBlocks();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  public double getDefaultCircleRadius()
  {
    if(defaultCircleRadius == null)
    {
      List<Object> objects = airframe.getIncludeOrServosOrCommands();
      je_ne_vais_pas_me_taper_toute_l_arborescence_du_XML_tout_de_meme:
      for(Object o1 : objects)
      {
        if(o1 instanceof Section)
        {
          if(((Section) o1).getName().equalsIgnoreCase("MISC"))
          {
            List<Object> definesOrLinears = ((Section) o1).getDefineOrLinear();
            for(Object o2 : definesOrLinears)
            {
              if(o2 instanceof Define)
              {
                if(((Define)o2).getName().equalsIgnoreCase("DEFAULT_CIRCLE_RADIUS"))
                {
                  defaultCircleRadius = Double.valueOf(((Define)o2).getValue());
                  break je_ne_vais_pas_me_taper_toute_l_arborescence_du_XML_tout_de_meme;
                }
              }
            }
          }
        }
      }
    }
    return defaultCircleRadius;
  }
  //-----------------------------------------------------------------------------
  public final static AirframeFacade getInstance()
  {
    if (instance == null)
    {
      synchronized(AirframeFacade.class)
      {
        if (instance == null)
          instance = new AirframeFacade();
      }
    }
    return instance;
  }
  //-----------------------------------------------------------------------------
}

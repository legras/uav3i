package com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import com.deev.interaction.uav3i.util.UAV3iSettings;
import com.deev.interaction.uav3i.util.log.LoggerUtil;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.jaxb.Field;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.jaxb.Message;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.jaxb.MsgClass;
import com.deev.interaction.uav3i.util.paparazzi_settings.ivyMessages.jaxb.Protocol;

/**
 * 
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class IvyMessagesFacade
{
  //-----------------------------------------------------------------------------
  private static IvyMessagesFacade instance = null;

  private Protocol protocol;
  private Map<String, Map<String, Integer>> fieldsIndexByMessage;
  //-----------------------------------------------------------------------------
  private IvyMessagesFacade()
  {
    try
    {
      // Ouverture du fichier schéma XML
      InputStream xmlStream = new FileInputStream(UAV3iSettings.getPaparazziIvyMessages());

      // Désérialisation dans l'arborescence de classes JAXB
      protocol = JAXB.unmarshal(xmlStream, Protocol.class);
      fieldsIndexByMessage = new HashMap<>();
      processMessages();
      LoggerUtil.LOG.config("Messages: fields recovered");
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
  }
  //-----------------------------------------------------------------------------
  /**
   * Pattern Singleton : la méthode renvoie l'unique instance de {@link FlightPlanFacade}.
   * @return l'instance de <code>FlightPlanFacade</code>.
   */
  public final static IvyMessagesFacade getInstance()
  {
    if (instance == null)
    {
      synchronized(IvyMessagesFacade.class)
      {
        if (instance == null)
          instance = new IvyMessagesFacade();
      }
    }
    return instance;
  }
  //-----------------------------------------------------------------------------
  public int getFieldIndex(String messageName, String fieldName)
  {
    Map<String, Integer> fieldsIndex = fieldsIndexByMessage.get(messageName);
    return fieldsIndex.get(fieldName);
  }
  //-----------------------------------------------------------------------------







  //-----------------------------------------------------------------------------
  private void processMessages()
  {
    for(MsgClass msgClass : protocol.getMsgClass())
    {
      for(Message message : msgClass.getMessage())
      {
        Map<String, Integer> fieldsIndex = new HashMap<>();
        List<Field> fields = message.getField();
        for(int i=1; i<=fields.size(); i++)
        {
          Field field = fields.get(i-1);
          fieldsIndex.put(field.getName(), i);
        }
        fieldsIndexByMessage.put(message.getName(), fieldsIndex);
      }
    }
  }
  //-----------------------------------------------------------------------------
}

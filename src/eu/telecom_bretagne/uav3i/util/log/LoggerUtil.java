package eu.telecom_bretagne.uav3i.util.log;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil
{
  //-----------------------------------------------------------------------------
  public static final Logger LOG;
  static
  {
    System.setProperty("java.util.logging.config.file", "uav3i_logging.properties");
    LOG = Logger.getLogger("uav3i_logger");
//    LOG.setUseParentHandlers(false);
//    LOG.addHandler(new Handler()
//    {
//      
//      @Override
//      public void publish(LogRecord record)
//      {
//      }
//      
//      @Override
//      public void flush()
//      {
//        // TODO Auto-generated method stub
//        
//      }
//      
//      @Override
//      public void close() throws SecurityException
//      {
//        // TODO Auto-generated method stub
//        
//      }
//    });
  }
  //-----------------------------------------------------------------------------
}

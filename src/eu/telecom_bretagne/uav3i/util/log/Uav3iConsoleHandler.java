package eu.telecom_bretagne.uav3i.util.log;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Philippe TANGUY (Télécom Bretagne)
 *
 */
public class Uav3iConsoleHandler extends Handler
{
  //-----------------------------------------------------------------------------
  @Override
  public void publish(LogRecord record)
  {
    if (getFormatter() == null)
    {
      //setFormatter(new SimpleFormatter());
      setFormatter(new MyFormatter());
    }

    try
    {
      String message = getFormatter().format(record);
      if (record.getLevel().intValue() >= Level.WARNING.intValue())
      {
        System.err.write(message.getBytes());
      }
      else
      {
        System.out.write(message.getBytes());
      }
    }
    catch (Exception exception)
    {
      reportError(null, exception, ErrorManager.FORMAT_FAILURE);
      return;
    }
  }
  //-----------------------------------------------------------------------------
  @Override
  public void flush() {}
  //-----------------------------------------------------------------------------
  @Override
  public void close() throws SecurityException {}
  //-----------------------------------------------------------------------------
}

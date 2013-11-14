package eu.telecom_bretagne.uav3i.util.log;

import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * Par défaut, la classe ConsoleHandler écrit tout sur la sortie d'erreurs (en
 * rouge donc). Cette classe permet d'écrire les logs de niveau SEVERE ou WARNING
 * sur la sortie d'erreurs et les autres sur la sortie standard (en noir). La
 * différentiation est donc plus facile.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class Uav3iConsoleHandler extends Handler
{
  //-----------------------------------------------------------------------------
  @Override
  public void publish(LogRecord record)
  {
    // eu.telecom_bretagne.uav3i.util.log.Uav3iConsoleHandler.formatter = eu.telecom_bretagne.uav3i.util.log.MyFormatter
    // La ligne précédente dans uav3i_logging.properties ne semble pas fonctionner...
    // On fixe donc le formatter.
//    if (getFormatter() == null)
//    {
//      //setFormatter(new SimpleFormatter());
      setFormatter(new MyFormatter());
//    }

    if(record.getLevel().intValue() >= this.getLevel().intValue())
    {
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
  }
  //-----------------------------------------------------------------------------
  @Override
  public void flush() {}
  //-----------------------------------------------------------------------------
  @Override
  public void close() throws SecurityException {}
  //-----------------------------------------------------------------------------
}

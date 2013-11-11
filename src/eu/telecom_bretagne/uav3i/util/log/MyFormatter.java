package eu.telecom_bretagne.uav3i.util.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Classe assurant le formatage des lines de log.
 * 
 * @author Philippe TANGUY (Télécom Bretagne)
 */
public class MyFormatter extends Formatter
{
  //-----------------------------------------------------------------------------
  private static DateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM y HH:mm:ss");
  private static String     logLineFormat       = "%1$-30s | %2$-8s | %3$-70s | %4$s\n";

  //-----------------------------------------------------------------------------
  @Override
  public String format(LogRecord record)
  {
    String simpleClassName = record.getSourceClassName().substring(record.getSourceClassName().lastIndexOf('.') + 1);
    return String.format(logLineFormat, dateToString(record.getMillis()),
                                        record.getLevel(),
                                        "(" + record.getLoggerName() + ") " + simpleClassName + " -> " + record.getSourceMethodName(),
                                        record.getMessage());
  }
  //-----------------------------------------------------------------------------
  /**
   * Convertit un timestamp (entier long) en une chaîne de caractères exprimée au
   * format "EEEE dd MMMM y HH:mm:ss" (exemple mardi 12 novembre 2013 11:28:52).<br/>
   * 
   * @param eventTime la date au format <code>long</code>.
   * @return la date lisible dans une chaîne de caractères.
   */
  public static String dateToString(long eventTime)
  {
    return dateFormat.format(new Date(eventTime));
  }
  //-----------------------------------------------------------------------------
}

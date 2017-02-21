/**
 * Util class for all log related operations
 */
package utils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.xml.DOMConfigurator;

public class LogUtils
{

    public LogUtils()
    {
    }

    public static void init()
    {
        //BasicConfigurator.configure();
        String file = System.getProperty("storage-config");
        file += System.getProperty("file.separator") + "log4j.xml";
        DOMConfigurator.configure(file);
    }

    public static String stackTrace(Throwable e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String getTimestamp()
    {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return df.format(date);
    }
    
    public static String throwableToString(Throwable e)
    {
        StringBuffer sbuf = new StringBuffer("");
        String trace = stackTrace(e);
        sbuf.append((new StringBuilder()).append("Exception was generated at : ").append(getTimestamp()).append(" on thread ").append(Thread.currentThread().getName()).toString());
        sbuf.append(System.getProperty("line.separator"));
        String message = e.getMessage();
        if(message != null)
            sbuf.append(message);
        sbuf.append(System.getProperty("line.separator"));
        sbuf.append(trace);
        return sbuf.toString();
    }

    public static String getLogMessage(String message)
    {
        StringBuffer sbuf = new StringBuffer((new StringBuilder()).append("Log started at : ").append(getTimestamp()).toString());
        sbuf.append(System.getProperty("line.separator"));
        sbuf.append(message);
        return sbuf.toString();
    }
}

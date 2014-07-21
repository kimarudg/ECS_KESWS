/**
 * 
 */
package logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author kim
 *
 */
public class ECSKESWSLogFormater extends Formatter{

	@Override
	public String format(LogRecord record) {
		// TODO Auto-generated method stub
		  StringBuffer buf = new StringBuffer(1000);
		    // Bold any levels >= WARNING
		    buf.append("<tr>");
		    buf.append("<td>");

		    if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
		      buf.append("<b>");
		      buf.append(record.getLevel());
		      buf.append("</b>");
		    } else {
		      buf.append(record.getLevel());
		    }
		    buf.append("</td>");
		    buf.append("<td>");
		    buf.append(calcDate(record.getMillis()));
		    buf.append(' ');
		    buf.append(formatMessage(record));
		    buf.append('\n');
		    buf.append("<td>");
		    buf.append("</tr>\n");
		    return buf.toString();

	}
	  private String calcDate(long millisecs) {
		    SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		    Date resultdate = new Date(millisecs);
		    return date_format.format(resultdate);
		  }
	  // This method is called just after the handler using this
	  // formatter is created
	  public String getHead(Handler h) {
	    return "<HTML>\n<HEAD>\n" + (new Date()) 
	        + "\n</HEAD>\n<BODY>\n<PRE>\n"
	        + "<table width=\"100%\" border>\n  "
	        + "<tr><th>Level</th>" +
	        "<th>Time</th>" +
	        "<th>Log Message</th>" +
	        "</tr>\n";
	  }

	  // This method is called just after the handler using this
	  // formatter is closed
	  public String getTail(Handler h) {
	    return "</table>\n  </PRE></BODY>\n</HTML>\n";
	  }


}

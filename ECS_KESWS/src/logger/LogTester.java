package logger;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogTester {
	// Image here some real work

    // Now we demo the logging
	 private final static Logger LOGGER = Logger.getLogger(LogTester.class
		      .getName());
     public void doSomeThingAndLog() {
    // Image here some real work

    // Now we demo the logging

    // Set the LogLevel to Severe, only severe Messages will be written
    LOGGER.setLevel(Level.SEVERE);
    LOGGER.severe("Info Log");
    LOGGER.warning("Info Log");
    LOGGER.info("Info Log");
    LOGGER.finest("Really not important");

    // Set the LogLevel to Info, severe, warning and info will be written
    // Finest is still not written
    LOGGER.setLevel(Level.INFO);
    LOGGER.severe("Info Log");
    LOGGER.warning("Info Log");
    LOGGER.info("Info Log");
    LOGGER.finest("Really not important");
  }

  public static void main(String[] args) {
	  LogTester tester = new LogTester();
    try {
      ECSKESWSLogger.setup();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Problems with creating the log files");
    }
    tester.doSomeThingAndLog();
  }

}

package ub.edu.softwaredistribuit.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Log File utils
 */
public class LogUtils {

    private Logger logger;
    private FileHandler fileHandler;

    /**
     * Create new LogUtil with certain filename
     * @param filename Log file name
     */
    public LogUtils(String filename) {
        this.logger = Logger.getLogger(filename);
        this.loggerSetUp(filename);
    }

    private void loggerSetUp(String filename){
        this.logger.setUseParentHandlers(false);
        try {
            fileHandler = new FileHandler("./Server"+ filename + ".log");
            fileHandler.setFormatter(new LogFormat());
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Log into the file with certain Level
     * @param level Level
     * @param msg Message to log
     */
    public void log(Level level, String msg){
        this.logger.log(level, msg);
    }

    /**
     * Log an info message to the server log file
     * @param msg Message to log
     */
    public void info(String msg){
        this.logger.info(msg);
    }

    /**
     * Lon a warn message to the server log file
     * @param msg Message to log
     */
    public void warn(String msg){
        this.logger.warning(msg);
    }

    /**
     * Close the opened file handler
     */
    public void closeHandler(){
        this.logger.getHandlers()[0].close();
    }

    /**
     * Custom formatter for the Log File
     */
    private class LogFormat extends Formatter {

        // Create a DateFormat to format the logger timestamp.
        private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder(1000);
            builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");
            builder.append("[").append(record.getLevel()).append("] - ");
            builder.append(formatMessage(record));
            builder.append("\n");
            return builder.toString();
        }

        @Override
        public String getHead(Handler h) {
            return super.getHead(h);
        }

        @Override
        public String getTail(Handler h) {
            return super.getTail(h);
        }
    }
}

/*
 * Logger.java
 *
 * Copyright (C) 2022 Michael Dinolfo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package AlgoTrader;

import java.io.*;
import java.text.*;
import java.util.Calendar;

//import AlgoTrader.Configuration;

/**
 * This class is responsible for writing events to a flatfile.
 *
 */
public class Logger {

    /** Path and file name of the log file. */
    private static final String FileName = "../log/" + Configuration.getInstance().getString("LOGFILE");

    /** Singleton instance of Logger. */
	protected static volatile Logger LoggerInstance = null;

    /** Logfile stream object. */
    private FileOutputStream LogFile;

    /**
     * This is a singleton class.  The constructor is Protected so it
     * can never be instantiated from outside of the class.
     */
    protected Logger () {
    }

    /**
     * Creates a new Logger instance, verifies there is only one,
     * and attempts to open the output file for writing.
     *
     * @throws IOException If there is already a Logger instance, or
     * if the output file cannot be open
     */
    public static void initialize () throws IOException {
        if ( LoggerInstance != null ) {
            throw new IOException("Logger is already initialized.");
        }

        LoggerInstance = new Logger();

        try {
            LoggerInstance.LogFile =
                new FileOutputStream(FileName);
        } catch ( IOException e ) {
            throw e;
        }
    }

    /**
     * Accessor function for the instance.
     *
     * @return An instance of Logger, or null if it was never
     * initialized.
     */
    public static Logger getInstance () {
        return LoggerInstance;
    }

    /**
     * Writes a string to the logfile with a prepended datetime.
     * We flush after each write in case the matching engine is
     * abruptly stopped.
     *
     * @param s The string to be written
     * @throws IOException If the logfile write or flush fails
     */
    public void write ( String s ) throws IOException {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String prefix = df.format(Calendar.getInstance().getTime());

        String output = prefix + ": " + s + "\n\n";

        try {
            LogFile.write(output.getBytes());
            LogFile.flush();
        } catch (IOException e) {
            throw e;
        }
    }

}


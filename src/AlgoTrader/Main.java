/*
 * Main.java
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

/**
 * Static class that will intialize the other objects.
 *
 */
public class Main
{
    private static boolean keepRunning = true;
    
	public static void main (String args[])
    {
        try
        {
            final Thread mainThread = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook( new Thread() {
                public void run(){
                    try {
                        keepRunning = false;
                        mainThread.join();
                    } catch ( Exception e ) {
                        // do nothing
                    }
                }  
            } );

			while ( keepRunning ) {
				Thread.sleep ( 100 );
			}

		}
        catch ( Exception e )
        {
            System.err.println("Error initializing:\n" + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
        
	}
}


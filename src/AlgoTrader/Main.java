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
            // look for terminate signal to perform orderly shutdown
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
            // initialization steps go below

            Configuration.initialize();
            Logger.initialize();
            Order.initialize();

            Trader algoTrader = new Trader();

            PriceSocket ps = new PriceSocket( "PS-MAIN", algoTrader );
            OrderSocket os = new OrderSocket( "OS-MAIN", algoTrader );

            ps.start();
            os.start();

            // initialization steps go above
            Logger.getInstance().write("AlgoTrader is up.");

			while ( keepRunning ) {
				Thread.sleep ( 100 );
			}
            Logger.getInstance().write("Received termination signal.");
            // orderly shutdown steps go below
            
            /*_*/

            // orderly shutdown steps go above
            Logger.getInstance().write("AlgoTrader shutdown complete.");

		}
        catch ( Exception e )
        {
            System.err.println("Error initializing:\n" + e.getMessage() + "\n");
            e.printStackTrace();
            System.exit(1);
        }
        
	}
}


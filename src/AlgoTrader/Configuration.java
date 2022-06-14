/*
 * Configuration.java
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
import java.util.*;

/**
 * This class reads in the configuration file, defaults values, and
 * returns settings on request.  This class does not validate any
 * settings, and stores and replies to any configuration item.
 */
public class Configuration {
    
    /** Singleton instance of Configuration. */
	protected static volatile Configuration ConfigurationInstance = null;
    
    /** Configuration items and values. */
    private HashMap<String, String> ConfigValues;

    /**
     * This is a singleton class.  The constructor is Protected so it
     * can never be instantiated from outside of the class.
     */
    protected Configuration ()
    {
        ConfigValues = new HashMap<>();
    }
    
    /**
     * Creates a new Configuration instance, verifies there is only one,
     * and attempts to open the output file for writing.
     *
     * @throws IOException If there is already a Configuration instance,
     * or if the configuration file cannot be open
     */
    public static void initialize () throws IOException
    {
        if ( ConfigurationInstance != null )
        {
            throw new IOException("Configuration is already initialized.");
        }

        ConfigurationInstance = new Configuration();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader("../cfg/AlgoTrader.cfg"));
            String line;
            while ((line = br.readLine()) != null) {
                ConfigurationInstance.ProcessInput( line );
            }
            br.close();
        }
        catch ( IOException e )
        {
            throw e;
        }
        
        ConfigurationInstance.DefaultSettings();
    }
    
    /**
     * Parses a line from the configuration file, adds the key/value
     * pair to the ConfigValues object if a valid line.
     * 
     * @param line String containing the line read from the
     * configuration file
     */
     private void ProcessInput( String line )
     {
         // trim whitespace
         line = line.replaceAll("^\\s+","").replaceAll("\\s+$","");
         
         // do not process comments or unparsable short lines
         if ( line.length() < 3 || line.charAt(0) == '#' )
            return;
         
         String[] token = line.split("=");
         
         // did we find a key/value pair?
         if ( token.length == 2 )
         {
             
             // trim whitespace from left of value and right of key
             String key = token[0].replaceAll("\\s+$","");
             String value = token[1].replaceAll("^\\s+","");
             
             // remove double spaces
             value = value.replaceAll("\\s\\s"," ");
             
             // add key/value
             ConfigValues.put( key, value );
        }
     }
    
    /**
     * Defaults settings vital to operation only if they were not
     * set in the configuration file.
     */
     private void DefaultSettings ()
     {
        if ( !ConfigValues.containsKey( "ORDERIP" ) )
            ConfigValues.put( "ORDERIP" , "192.168.0.1" );

         if ( !ConfigValues.containsKey( "ORDERPORT" ) )
            ConfigValues.put( "ORDERPORT" , "3500" );

         if ( !ConfigValues.containsKey( "PRICEIP" ) )
            ConfigValues.put( "PRICEIP" , "192.168.0.1" );
            
         if ( !ConfigValues.containsKey( "PRICEPORT" ) )
            ConfigValues.put( "PRICEPORT" , "3501" );
            
         if ( !ConfigValues.containsKey( "LOGFILE" ) )
            ConfigValues.put( "LOGFILE" , "AlgoTrader.log" );

         if ( !ConfigValues.containsKey( "PINGMS" ) )
            ConfigValues.put( "PINGMS" , "5000" );

         if ( !ConfigValues.containsKey( "LOGGING" ) )
            ConfigValues.put( "LOGGING" , "STANDARD" );
         
         if ( !ConfigValues.containsKey( "STRATEGIES" ) )
            ConfigValues.put( "STRATEGIES" , "" );
     }
    
    /**
     * Returns the configuration item's value as a String
     * 
     * @return String value of the configuration item, null if
     * neither loaded nor defaulted
     * @param key String containing the parameter key.
     */
    public String getString( String key )
    {
        if ( !ConfigValues.containsKey( key ) )
            return null;
        
        return ConfigValues.get( key );
    }

    /**
     * Returns the configuration item's value as an Integer
     * 
     * @return Integer value of the configuration item, -1 if
     * neither loaded nor defaulted
     * @param key String containing the parameter key.
     */    
    public int getInt( String key )
    {
        if ( !ConfigValues.containsKey( key ) )
            return -1;
        
        return Integer.parseInt( ConfigValues.get( key ) );
    }
    
    /**
     * Returns the configuration item's value as a Double
     * 
     * @return Double value of the configuration item, -1.0 if
     * neither loaded nor defaulted
     * @param key String containing the parameter key.
     */    
    public double getDouble( String key )
    {
        if ( !ConfigValues.containsKey( key ) )
            return -1D;
        
        return Double.parseDouble( ConfigValues.get( key ) );
    }

        
    /**
     * Accessor function for the instance.
     *
     * @return An instance of Configuration, or null if it was never
     * initialized.
     */
    public static Configuration getInstance ()
    {
        return ConfigurationInstance;
    }
}

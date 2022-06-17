/*
 * PriceSocket.java
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
import java.net.*;

/**
 * This class creates a TCP socket interface to allow market data
 * subscribe/unsubscribe requests to be sent to a market data source.
 * The Trader captures snapshots of the current prices in the books.
 * This socket runs on its own thread.
 *
 * @see Trader
 */

public class PriceSocket implements Runnable {

    /** Thread object for this class. */
    private Thread T;

    /** Label of the Thread. */
    private String ThreadName;

    /** IP address to connect. */
    private String IPAddress;

    /** Port number to connect. */
    private int PortNumber;

    /** Reference to Trader object. */
    private Trader trader;

    /** Buffered output to be written to the connection. */
    private String OutputBuffer;

    /** Number of erroneous messages.  Three in a row results in a
     *  disconnect. */
    private int ErrorCount;

    /** Milliseconds between PING messages.  These messages can be
     *  ignored on the client side. */
    private int PingMS;

    /**
     * The constructor stores information for when the Thread is started,
     * but does not start it on its own.
     *
     * @param Name A string identifier for the future Thread
     * @param me A reference to the MatchingEngine this socket is
     * supposed to connect
     */
    public PriceSocket ( String Name, Trader tdr )
    {
        ThreadName = Name;
        IPAddress = Configuration.getInstance().getString("PRICEIP");
        PortNumber = Configuration.getInstance().getInt("PRICEPORT");
        PingMS = Configuration.getInstance().getInt("PINGMS");
        trader = tdr;
        T = null;
        ErrorCount = 0;
        OutputBuffer = "";
    }

    /**
     * Interface for the trader to subscribe to a symbol
     *
     * @param symbol The symbol to subscribe
     */
    public void Subscribe ( String symbol )
    {
        WriteMessage("SUB,"+symbol);
    }

    /**
     * Interface for the trader to unsubscribe to a symbol
     *
     * @param symbol The symbol to subscribe
     */
    public void Unsubscribe ( String symbol )
    {
        WriteMessage("UNSUB,"+symbol);
    }

    /**
     * Writes a message to the outbound buffer
     *
     * @param s The message to be sent out
     */
    private void WriteMessage ( String s )
    {
        if ( OutputBuffer.length() > 0 )
            OutputBuffer += "\n" + s;
        else
            OutputBuffer = s;
    }


    /**
     * Parses the input received on the connection.  Will disconnect
     * when receiving a "BYE", otherwise will attempt to parse the
     * message as a subscribe/unsubscribe request.  If all fails, returns
     * an error message and increments the ErrorCount.  Disconnects
     * if there are three consecutive errors.
     *
     * @param InputLine The message received on the connection
     * @return An error message, or blank string if there was no error
     */
    private String ProcessInput ( String InputLine )
    {
        if ( InputLine.equals("END") )
            return "BYE";

        if ( InputLine.equals("PING") || InputLine.equals("CONNECTED") )
            return "";

        if ( InputLine.equals("BYE") )
            return "ABORT";

        String[] tokens = InputLine.split(",");
        String cmd = tokens[0];

        if ( cmd.equals("SNAPSHOT") ) {
            trader.MarketDataUpdate(tokens);
            return "";
        } else if ( cmd.equals("REJECT")) {
            try {
                Logger.getInstance().write("PS REJECT:"+InputLine);
            } catch ( Exception e) {
                //
            }
            return "";
        }


        ErrorCount++;
        if ( ErrorCount >= 3 )
            return "END";
        else
            return "UNKNOWN COMMAND";
    }

    /**
     * Notifies the trader that this socket exists.  Opens the
     * PortNumber and listens for connections.  Establishes connections
     * and reads/writes data.  This function never exits, which requires
     * killing AlgoTrader to stop.
     */
    public void run ()
    {
        trader.SetPriceSocket(this);

        while ( true )
        {
            try (
                Socket clientSocket = new Socket(IPAddress,PortNumber);
                PrintWriter out =
                    new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            ) {
                String InputLine, OutputLine;

                int PingCheck = 0;
                boolean AbortSession = false;

                // messages from a previous, disconnected session?
                if ( !OutputBuffer.equals("") )
                {
                    out.println(OutputBuffer);
                    OutputBuffer = "";
                }

                while ((InputLine = in.readLine()) != null && !AbortSession)
                {
                    OutputLine = ProcessInput(InputLine);
                    if ( OutputLine.equals("ABORT") )
                        break;
                    else if ( !OutputLine.equals("") )
                        out.println(OutputLine);
                    
                    if ( OutputLine.equals("BYE") )
                        break;

                    while(!in.ready() && !AbortSession)
                    {
                        OutputLine = OutputBuffer;
                        if ( OutputLine.length() > 0 )
                        {
                            out.println(OutputLine);
                            OutputBuffer = "";
                        }

                        Thread.sleep(50);
                        PingCheck += 50;

                        if (PingCheck >= PingMS) {
                            out.println("PING");
                            PingCheck = 0;
                            AbortSession = out.checkError();
                        }

                    }

                    PingCheck = 0;
                }

            } catch (Exception e) {
                System.err.println("Exception caught when trying to connect on port "
                    + PortNumber + " at " + IPAddress);
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the Thread.  Sends a message to Logger to indicate
     * successful start and as to which PortNumber it is listening.
     *
     * @throws IOException Passthrough from Logger
     */
    public void start () throws IOException
    {
        if ( T == null )
        {
            try {
                Logger.getInstance().write("Connecting to price subscriptions on "+ IPAddress + ":" + PortNumber);
            } catch (IOException e) {
                throw e;
            }

            T = new Thread( this, ThreadName );
            T.start();
        }
    }
}


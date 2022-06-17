/*
 * Trader.java
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

 import java.util.*;

 /**
 * This class keeps track of the orders, their current state, and implements
 * strategies.
 */
 public class Trader {

    /**
     * Class for storing market data points
     */
    private class PricePoint implements Comparable<PricePoint> {
        public double Price;
        public double Quantity;

        public PricePoint(double px, double qty) {
            Price = px;
            Quantity = qty;
        }

        @Override
        public int compareTo(PricePoint pp) {
            return Double.compare(Price,pp.Price);
        }

        @Override
        public String toString(){
            return Double.toString(Price) + " " + Double.toString(Quantity) + " ";
        }
    }

    /**
     * Master list of Orders.
     */
    private Map<String, Order> OrderBlotter;

    /**
     * List of price subscriptions
     */
    private Map<String, Integer> PriceSubscriptions;

    /**
     * Market Data
     */
    private Map<String, List<PricePoint>> BidMarketData;
    private Map<String, List<PricePoint>> OfferMarketData;

    /**
     * A reference to a PriceSocket object so the trader
     * may subscribe to market data.
     *
     * @see PriceSocket
     */
    private PriceSocket PS;

    /**
     * Initializes objects
     */
    public Trader () {
        OrderBlotter = new HashMap<>();
        PriceSubscriptions = new HashMap<>();
        BidMarketData = new HashMap<>();
        OfferMarketData = new HashMap<>();

        PS = null;
    }

    /**
     * Processes market data.
     *
     * @param ps PriceSocket
     */
    public void MarketDataUpdate ( String[] tokens ) {
        String symbol = tokens[1];
        List<PricePoint> Bid = new LinkedList<>();
        List<PricePoint> Offer = new LinkedList<>();
        List<PricePoint> ListPtr = null;
        PricePoint pp;
        String val;
        Double px, qty;
        px = qty = null;

        for( int i = 2; i < tokens.length; i++ ) {
            val = tokens[i];
            if ( val.equals("BID") ) {
                ListPtr = Bid;
            } else if ( val.equals("OFFER") ) {
                ListPtr = Offer;
            } else if ( px == null ) {
                px = Double.parseDouble(val);
            } else {
                qty = Double.parseDouble(val);
                pp = new PricePoint(px, qty);
                ListPtr.add(pp);

                px = null;
            }
        }

        if ( Bid.size() > 0 )
            Collections.sort(Bid, Collections.reverseOrder());
        if ( Offer.size() > 0 )
            Collections.sort(Offer);

        BidMarketData.put(symbol, Bid);
        OfferMarketData.put(symbol, Offer);
    }

    /**
     * Adds an order to the blotter
     *
     */
    public void NewOrder ( Order o ) {
        // update market data subscriptions
        String symbol = o.getSymbol();
        if ( PriceSubscriptions.containsKey(symbol) ) {
            int count = PriceSubscriptions.get(symbol);
            count++;
            PriceSubscriptions.put(symbol, count);
        } else {
            PS.Subscribe(symbol);
            PriceSubscriptions.put(symbol, 1);
        }

        /*  TBD  */

        // add to blotter
        OrderBlotter.put(o.getOrderID(), o);
    }

    /**
     * Removes an order to the blotter
     *
     */
    public void CancelOrder ( Order o ) {
        // update market data subscriptions
        String symbol = o.getSymbol();
        int count = PriceSubscriptions.get(symbol);
        count--;
        if ( count > 0 ) {
            PriceSubscriptions.put(symbol, count);
        } else {
            PS.Unsubscribe(symbol);
            PriceSubscriptions.remove(symbol);
        }

        /*  TBD  */

        // remove from blotter
        OrderBlotter.remove(o.getOrderID());
    }

    /**
     * Sets the PriceSocket reference.
     *
     * @param ps PriceSocket
     */
    public void SetPriceSocket ( PriceSocket ps )
    {
        PS = ps;
    }
 }
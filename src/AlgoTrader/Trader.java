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
     * Master list of Orders.
     */
    private Map<String, Order> OrderBlotter;

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

        PS = null;
    }

    /**
     * Processes market data.
     *
     * @param ps PriceSocket
     */
    public void MarketDataUpdate ( String[] tokens ) {
        /*  TBD  */
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
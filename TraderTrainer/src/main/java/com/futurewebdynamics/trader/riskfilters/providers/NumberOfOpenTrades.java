package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 09/10/2017.
 */
public class NumberOfOpenTrades implements IRiskFilter {

    final static Logger logger = Logger.getLogger(TimeSinceLastBuy.class);

    private PositionsManager manager;
    private int maxOpenTrades;

    public NumberOfOpenTrades(PositionsManager manager, int maxOpenTrades) {
        this.manager = manager;
        this.maxOpenTrades = maxOpenTrades;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice, boolean isShortTrade) {
         if (manager.getOpenTradesCount() < maxOpenTrades) {
             return true;
         }
         logger.info("Blocking purchase as there are already the maximum number of trades");

        return false;
    }

    @Override
    public void setTestTimeMs(long testTimeMs) {

    }
}
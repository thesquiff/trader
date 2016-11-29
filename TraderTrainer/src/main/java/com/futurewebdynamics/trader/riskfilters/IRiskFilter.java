package com.futurewebdynamics.trader.riskfilters;

/**
 * Created by Charlie on 26/09/2016.
 */
public interface IRiskFilter {

    boolean proceedWithBuy(int buyPrice, boolean isShortTrade);

    void setTestTimeMs(long testTimeMs);
}

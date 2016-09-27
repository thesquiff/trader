package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;

/**
 * Created by Charlie on 26/09/2016.
 */
public class LowerBuyLimit implements IRiskFilter{

    private int lowerBuyLimit;

    public LowerBuyLimit(int lowerBuyLimit) {
        this.lowerBuyLimit = lowerBuyLimit;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice) {
        return (buyPrice > lowerBuyLimit);
    }
}

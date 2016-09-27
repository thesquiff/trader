package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;

/**
 * Created by Charlie on 26/09/2016.
 */
public class UpperBuyLimit implements IRiskFilter{

    private int upperBuyLimit;

    public UpperBuyLimit(int upperBuyLimit) {
        this.upperBuyLimit = upperBuyLimit;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice) {
        return (buyPrice < upperBuyLimit);
    }
}

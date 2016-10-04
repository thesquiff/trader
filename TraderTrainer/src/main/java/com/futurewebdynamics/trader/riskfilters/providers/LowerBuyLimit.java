package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 26/09/2016.
 */
public class LowerBuyLimit implements IRiskFilter{

    final static Logger logger = Logger.getLogger(LowerBuyLimit.class);

    private int lowerBuyLimit;

    public LowerBuyLimit(int lowerBuyLimit) {
        this.lowerBuyLimit = lowerBuyLimit;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice) {
        logger.debug("Lower buy limit buyPrice: " + buyPrice + " lowerBuyLimit:" + lowerBuyLimit);
        return (buyPrice > lowerBuyLimit);
    }
}

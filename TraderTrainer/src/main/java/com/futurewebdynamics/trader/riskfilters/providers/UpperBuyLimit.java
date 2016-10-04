package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 26/09/2016.
 */
public class UpperBuyLimit implements IRiskFilter{

    final static Logger logger = Logger.getLogger(UpperBuyLimit.class);

    private int upperBuyLimit;

    public UpperBuyLimit(int upperBuyLimit) {
        this.upperBuyLimit = upperBuyLimit;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice) {
        logger.debug("Lower buy limit buyPrice: " + buyPrice + " upperBuyLimit:" + upperBuyLimit);
        return (buyPrice < upperBuyLimit);
    }
}

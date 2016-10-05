package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import com.futurewebdynamics.trader.riskfilters.MatchTradeEnum;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 26/09/2016.
 */
public class UpperBuyLimit implements IRiskFilter{

    final static Logger logger = Logger.getLogger(UpperBuyLimit.class);

    private int upperBuyLimit;
    private MatchTradeEnum matchTrade;


    public UpperBuyLimit(int upperBuyLimit, MatchTradeEnum matchTrade) {
        this.upperBuyLimit = upperBuyLimit;
        this.matchTrade = matchTrade;
    }

    @Override
    public boolean proceedWithBuy(int buyPrice, boolean isShortTrade) {

        if (isShortTrade && this.matchTrade == MatchTradeEnum.LONG_ONLY) return true;

        logger.debug("Lower buy limit buyPrice: " + buyPrice + " upperBuyLimit:" + upperBuyLimit);
        return (buyPrice < upperBuyLimit);
    }
}

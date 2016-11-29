package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import com.futurewebdynamics.trader.riskfilters.MatchTradeEnum;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 26/09/2016.
 */
public class LowerBuyLimit implements IRiskFilter{

    final static Logger logger = Logger.getLogger(LowerBuyLimit.class);

    private int lowerBuyLimit;
    private MatchTradeEnum matchTrade;

    public LowerBuyLimit(int lowerBuyLimit, MatchTradeEnum matchShortTrade) {
        this.lowerBuyLimit = lowerBuyLimit;
        this.matchTrade = matchTrade;
    }

    @Override
    public void setTestTimeMs(long testTimeMs) {

    }

    @Override
    public boolean proceedWithBuy(int buyPrice, boolean isShortTrade) {

        if (isShortTrade && this.matchTrade == MatchTradeEnum.LONG_ONLY) return true;

        logger.debug("Lower buy limit buyPrice: " + buyPrice + " lowerBuyLimit:" + lowerBuyLimit);
        return (buyPrice > lowerBuyLimit);
    }
}

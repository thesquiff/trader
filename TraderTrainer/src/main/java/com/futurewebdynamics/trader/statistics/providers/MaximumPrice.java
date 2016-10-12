package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;

import java.util.List;

/**
 * Created by 52con on 15/04/2016.
 */
public class MaximumPrice extends IStatisticProvider{


    public MaximumPrice(boolean isShortTrade) {
        super();
        this.setShortTradeCondition(isShortTrade);
    }

    @Override
    public String getName() {
        return "MaximumPrice";
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public Object getResult() {
        List<NormalisedPriceInformation> data = dataWindow.getData();
        return isShortTradeCondition() ? data.stream().mapToInt(p->p.getBidPrice()).max() :  data.stream().mapToInt(p->p.getAskPrice()).max();
    }
}

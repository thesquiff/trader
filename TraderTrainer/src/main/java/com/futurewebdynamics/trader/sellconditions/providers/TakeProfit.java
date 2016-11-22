package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by 52con on 15/04/2016.
 */
public class TakeProfit extends ISellConditionProvider {

    private int increase;

    public TakeProfit (int increase, boolean isShortTrade) {
        this.increase = increase;
        super.setShortTradeCondition(isShortTrade);
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }

    public void tick(Position position, NormalisedPriceInformation tick) {
        int buyPrice = position.getActualOpenPrice();

        if (!super.isShortTradeCondition() && tick.getBidPrice() >= (buyPrice+ increase)) {
            super.sell(position, tick, false);
        }

        if (super.isShortTradeCondition() && tick.getAskPrice() <= (buyPrice - increase)) {
            super.sell(position, tick, true);
        }
    }

    public TakeProfit makeCopy() {
        TakeProfit copy = new TakeProfit(this.increase, super.isShortTradeCondition());
        return copy;
    }

}

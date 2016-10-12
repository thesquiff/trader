package com.futurewebdynamics.trader.sellconditions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;

/**
 * Created by 52con on 15/04/2016.
 */
public abstract class ISellConditionProvider  {

    private int buyPrice;
    private boolean isShortTrade;

    public void setShortTradeCondition(boolean isShortTrade)
    {
        this.isShortTrade = isShortTrade;
    }

    public abstract ISellConditionProvider makeCopy();

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }

    public abstract void tick(Position position, NormalisedPriceInformation tickData);

    public void sell(Position position, int targetSellPrice) {
        position.sell(targetSellPrice);
    }

    public boolean isShortTradeCondition() {
        return this.isShortTrade;
    }

}

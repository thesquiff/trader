package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by 52con on 15/04/2016.
 */
public class TakeProfit extends ISellConditionProvider {

    private int increase;
    private int buyPrice;

    public TakeProfit (Position position, int buyPrice, int increase) {
        this.increase = increase;
        this.buyPrice = buyPrice;
        super.setPosition(position);
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }

    public void tick(NormalisedPriceInformation tick) {
        if (tick.getPrice() >= (buyPrice + increase)) {
            super.sell(tick.getPrice());
        }
    }

}

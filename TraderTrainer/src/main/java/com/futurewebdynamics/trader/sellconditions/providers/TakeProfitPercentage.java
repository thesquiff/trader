package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by Charlie on 29/04/2016.
 */
public class TakeProfitPercentage extends ISellConditionProvider {

    public double increasePercentage;

    public TakeProfitPercentage(double increasePercentage) {
        this.increasePercentage = increasePercentage;
    }

    public void tick(Position position, NormalisedPriceInformation tick) {
        if (tick.getPrice() >= (super.getBuyPrice() * (100+increasePercentage)/100)) {
            super.sell(position, tick.getPrice());
        }
    }

}

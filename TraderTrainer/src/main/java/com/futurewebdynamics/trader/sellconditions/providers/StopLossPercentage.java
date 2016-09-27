package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by Charlie on 29/04/2016.
 */
public class StopLossPercentage extends ISellConditionProvider{

    private double decreasePercentage;

    public StopLossPercentage(double decreasePercentage) {
        this.decreasePercentage = decreasePercentage;
    }

    public void tick(Position position, NormalisedPriceInformation tick) {
        if (tick.getPrice() <= (super.getBuyPrice() * (100-decreasePercentage)/100)) {
            sell(position, tick.getPrice());
        }
    }

    public double getDecreasePercentage() {
        return this.decreasePercentage;
    }

    public void setDecreasePercentage(double value) {
        this.decreasePercentage = value;
    }


    public StopLossPercentage makeCopy() {
        StopLossPercentage copy = new StopLossPercentage(this.decreasePercentage);
        return copy;
    }
}

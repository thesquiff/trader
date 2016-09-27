package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by 52con on 15/04/2016.
 */
public class StopLoss extends ISellConditionProvider {

    private int decrease;

    public StopLoss (int decrease) {
        this.decrease = decrease;
    }

    public int getDecrease() {
        return decrease;
    }

    public void setDecrease(int increase) {
        this.decrease = decrease;
    }

    public void tick(Position position, NormalisedPriceInformation tick) {
        if (tick.getPrice() <= (super.getBuyPrice() - decrease)) {
            sell(position, tick.getPrice());
        }
    }

    public StopLoss makeCopy() {
        StopLoss copy = new StopLoss(this.decrease);
        return copy;
    }


}

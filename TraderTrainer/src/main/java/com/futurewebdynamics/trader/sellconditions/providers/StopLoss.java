package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by 52con on 15/04/2016.
 */
public class StopLoss extends ISellConditionProvider {

    private int decrease;
    private int buyPrice;

    public StopLoss (Position position, int buyPrice, int decrease) {
        this.decrease = decrease;
        this.buyPrice = buyPrice;
        super.setPosition(position);
    }

    public int getDecrease() {
        return decrease;
    }

    public void setDecrease(int increase) {
        this.decrease = decrease;
    }

    public void tick(NormalisedPriceInformation tick) {
        if (tick.getPrice() <= (buyPrice - decrease)) {
            sell(tick.getPrice());
        }
    }




}

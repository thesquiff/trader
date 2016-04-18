package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

/**
 * Created by 52con on 15/04/2016.
 */
public class StopLoss extends ISellConditionProvider {

    private int buyPrice;
    private int decrease;

    public StopLoss(int buyPrice, int decrease) {
        this.buyPrice = buyPrice;
        this.decrease = decrease;
    }




}

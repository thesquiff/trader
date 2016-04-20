package com.futurewebdynamics.trader.sellconditions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionsManager;

/**
 * Created by 52con on 15/04/2016.
 */
public abstract class ISellConditionProvider  {

    private Position position;

    public abstract void tick(NormalisedPriceInformation tickData);

    public Position position() {
        return position;
    }

    public void setManager(Position position) {
        this.position = position;
    }

    public void sell(int targetSellPrice) {
        this.position.sell(targetSellPrice);
    }


}

package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.statistics.providers.IsFalling;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 29/04/2016.
 */
public class TakeProfitPercentage extends ISellConditionProvider {


    final static Logger logger = Logger.getLogger(TakeProfitPercentage.class);

    private double increasePercentage;
    private boolean waitForFall;
    private IsFalling fallingStatistic;

    public TakeProfitPercentage(double increasePercentage, boolean waitForFall, IsFalling fallingStatistic) {
        this.increasePercentage = increasePercentage;
        this.waitForFall = waitForFall;
        this.fallingStatistic = fallingStatistic;
    }

    public void tick(Position position, NormalisedPriceInformation tick) {

        logger.debug("tickPrice:" + tick.getPrice() + " buy price:" + super.getBuyPrice() + " targetSellPrice:" + (super.getBuyPrice() * (100+increasePercentage)/100));
        if (tick.getPrice() >= (super.getBuyPrice() * (100+increasePercentage)/100)) {

            if (waitForFall) {
                if (!(Boolean)this.fallingStatistic.getResult()) {
                    logger.debug("falling specified but not falling");
                    return;
                }
            }

            logger.debug("decided to sell");
            super.sell(position, tick.getPrice());
        }
    }

    public double getIncreasePercentage() {
        return increasePercentage;
    }

    public void setIncreasePercentage(double value) {
        this.increasePercentage = value;
    }

    public TakeProfitPercentage makeCopy() {
        TakeProfitPercentage percentage = new TakeProfitPercentage(this.increasePercentage, this.waitForFall, this.fallingStatistic);
        return percentage;
    }

}

package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 29/04/2016.
 */
public class TakeProfitPercentage extends ISellConditionProvider {

    final static Logger logger = Logger.getLogger(TakeProfitPercentage.class);

    private double increasePercentage;
    private boolean waitForFall;
    private IStatisticProvider fallingOrRisingStatistic;

    public TakeProfitPercentage(double increasePercentage, boolean waitForFall, IStatisticProvider fallingOrRisingStatistic, boolean isShortTrade) {
        this.increasePercentage = increasePercentage;
        this.waitForFall = waitForFall;
        this.fallingOrRisingStatistic = fallingOrRisingStatistic;
        super.setShortTradeCondition(isShortTrade);
    }

    public void tick(Position position, NormalisedPriceInformation tick) {

        int buyPrice = position.getActualOpenPrice();
        if (!super.isShortTradeCondition()) {
            logger.debug("LONG TRADE id: " + position.getUniqueId() + "  tickPrice:" + tick.getBidPrice() + " buy price:" + buyPrice + " targetSellPrice:" + (buyPrice * (100 + increasePercentage) / 100));
            if (!super.isShortTradeCondition() && tick.getBidPrice() >= (buyPrice * (100 + increasePercentage) / 100)) {

                if (waitForFall) {
                    if (!(Boolean) this.fallingOrRisingStatistic.getResult()) {
                        logger.debug("falling specified but not falling");
                        return;
                    }
                }

                logger.debug("decided to sell");
                super.sell(position, tick.getBidPrice());
            }
        }

        if (super.isShortTradeCondition()) {

            double targetPrice =(buyPrice * (100 - increasePercentage) / 100);
            logger.debug("SHORT TRADE id: " + position.getUniqueId() + " tickPrice:" + tick.getAskPrice() + " buy price:" + buyPrice + " targetSellPrice:" + targetPrice);

            if (tick.getAskPrice() <= targetPrice) {

                if (waitForFall) {
                    if (!(Boolean) this.fallingOrRisingStatistic.getResult()) {
                        logger.debug("rising specified but not rising");
                        return;
                    }
                }

                logger.debug("decided to sell at ask price" + tick.getAskPrice());
                super.sell(position, tick.getAskPrice());
            }
        }
    }

    public double getIncreasePercentage() {
        return increasePercentage;
    }

    public void setIncreasePercentage(double value) {
        this.increasePercentage = value;
    }

    public TakeProfitPercentage makeCopy() {
        TakeProfitPercentage percentage = new TakeProfitPercentage(this.increasePercentage, this.waitForFall, this.fallingOrRisingStatistic, super.isShortTradeCondition());
        return percentage;
    }

}

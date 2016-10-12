package com.futurewebdynamics.trader.sellconditions.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import org.apache.log4j.Logger;

/**
 * Created by Charlie on 29/04/2016.
 */
public class StopLossPercentage extends ISellConditionProvider{

    final static Logger logger = Logger.getLogger(StopLossPercentage.class);

    private double decreasePercentage;

    public StopLossPercentage(double decreasePercentage, boolean isShortTrade) {
        this.decreasePercentage = decreasePercentage;
        super.setShortTradeCondition(isShortTrade);
    }

    public void tick(Position position, NormalisedPriceInformation tick) {

        if (!super.isShortTradeCondition() && tick.getBidPrice() <= (super.getBuyPrice() * (100-decreasePercentage)/100)) {
            logger.debug("STOP LOSS LONG TRADE tickPrice:" + tick.getBidPrice() + " buy price:" + super.getBuyPrice() + " targetSellPrice:" + (super.getBuyPrice() * (100 - decreasePercentage) / 100));
            sell(position, tick.getBidPrice());
        }

        if (super.isShortTradeCondition() && tick.getAskPrice() >= (super.getBuyPrice() * (100+decreasePercentage)/100)) {
            logger.debug("STOP LOSS SHORT TRADE tickPrice:" + tick.getAskPrice() + " buy price:" + super.getBuyPrice() + " targetSellPrice:" + (super.getBuyPrice() * (100 + decreasePercentage) / 100));
            sell(position, tick.getAskPrice());
        }
    }

    public double getDecreasePercentage() {
        return this.decreasePercentage;
    }

    public void setDecreasePercentage(double value) {
        this.decreasePercentage = value;
    }


    public StopLossPercentage makeCopy() {
        StopLossPercentage copy = new StopLossPercentage(this.decreasePercentage, super.isShortTradeCondition());
        return copy;
    }
}

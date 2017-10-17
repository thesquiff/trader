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

        int buyPrice = position.getActualOpenPrice();

        if (position.isShortTrade() != super.isShortTradeCondition()) {
            //quit as this sell condition is not compatible
            logger.debug("INCOMPATIBLE EVALUATION");
            return;
        }

        logger.debug("Evaluating StopLossPercentage condition");

        if (!super.isShortTradeCondition()) {

            double targetSellPrice = buyPrice * (100 - decreasePercentage) / 100;

            logger.debug("STOP LOSS LONG TRADE tickPrice:" + tick.getBidPrice() + " buy price:" + buyPrice + " targetSellPrice:" + targetSellPrice);

            double drop = ((double)(buyPrice - tick.getBidPrice()) / buyPrice) * 100.0;
            if (drop > 1.0) logger.debug("% drop is " + drop + " at tick time: " + tick.getTimestamp());

            if (tick.getBidPrice() <= targetSellPrice) {
                logger.debug("Decided to sell based on stop loss percentage");
                sell(position, tick);
            }
        }

        if (super.isShortTradeCondition() && tick.getAskPrice() >= (buyPrice * (100+decreasePercentage)/100)) {
            logger.debug("STOP LOSS SHORT TRADE id:" + position.getUniqueId() + " tickPrice:" + tick.getAskPrice() + " buy price:" + buyPrice + " targetSellPrice:" + (buyPrice * (100 + decreasePercentage) / 100));
            sell(position, tick);
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

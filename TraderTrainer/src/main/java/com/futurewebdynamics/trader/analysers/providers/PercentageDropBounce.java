package com.futurewebdynamics.trader.analysers.providers;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.statistics.providers.IsFalling;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import com.futurewebdynamics.trader.statistics.providers.PercentageDrop;
import com.futurewebdynamics.trader.statistics.providers.PercentageRise;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDropBounce extends IAnalyserProvider {

    private double triggerPercentage;

    private IStatisticProvider percentageChangeStatistic;
    private IStatisticProvider isRisingOrFallingStatistic;
    private boolean isShortTrade;

    final static Logger logger = Logger.getLogger(PercentageDropBounce.class);

    public PercentageDropBounce(DataWindow dataWindow, int dataWindowSize, PositionsManager positionManager, double triggerPercentage, int oldestWindowSize, Collection<ISellConditionProvider> sellConditions, boolean isShortTrade) {
        super(dataWindow, dataWindowSize, positionManager, sellConditions);

        if (isShortTrade) {
            percentageChangeStatistic = new PercentageRise();
            ((PercentageRise)percentageChangeStatistic).setDataWindow(dataWindow, oldestWindowSize);
        } else {
            percentageChangeStatistic = new PercentageDrop();
            ((PercentageDrop)percentageChangeStatistic).setDataWindow(dataWindow, oldestWindowSize);
        }

        isRisingOrFallingStatistic = isShortTrade ? new IsFalling(1) : new IsRising(1);
        isRisingOrFallingStatistic.setDataWindow(dataWindow);

        this.triggerPercentage = triggerPercentage;
        this.isShortTrade = isShortTrade;

    }

    @Override
    public void tick(NormalisedPriceInformation tickData) {

        if (tickData.isEmpty()) return;
        if (this.dataWindow.hasGaps()) {
            return;
        }

        Double drop = (Double)percentageChangeStatistic.getResult();

        Boolean isRisingOrFalling = (Boolean)isRisingOrFallingStatistic.getResult();

        if (isShortTrade) {
            logger.debug("% drop: " + drop + ", isFalling: " + isRisingOrFalling);
        } else {
            logger.debug("% drop: " + drop + ", isRising: " + isRisingOrFalling);
        }

        if (drop >= triggerPercentage && isRisingOrFalling) {
            logger.debug("Going to buy");
            buy(tickData, isShortTrade);
        }

    }
}

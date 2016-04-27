package com.futurewebdynamics.trader.analysers.providers;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import com.futurewebdynamics.trader.statistics.providers.PercentageDrop;
import org.apache.log4j.Logger;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDropBounce extends IAnalyserProvider {

    private double triggerPercentage;

    private PercentageDrop percentageDropStatistic;
    private IsRising isRisingStatistic;

    final static Logger logger = Logger.getLogger(PercentageDropBounce.class);


    public PercentageDropBounce(DataWindow dataWindow, int dataWindowSize, PositionsManager positionManager, double triggerPercentage, int oldestWindowSize) {
        super(dataWindow, dataWindowSize, positionManager);
        percentageDropStatistic = new PercentageDrop();
        percentageDropStatistic.setDataWindow(dataWindow, oldestWindowSize);

        isRisingStatistic = new IsRising(1);
        isRisingStatistic.setDataWindow(dataWindow);

        this.triggerPercentage = triggerPercentage;

    }

    @Override
    public void tick(NormalisedPriceInformation tickData) {
        if (this.dataWindow.hasGaps()) return;

        Double drop = (Double)percentageDropStatistic.getResult();

        Boolean isRising = (Boolean)isRisingStatistic.getResult();

        logger.debug("% drop: " + drop + ", isRising: " + isRising);

        if (drop >= triggerPercentage && isRising) {
            logger.debug("Going to buy");
            buy(tickData);
        }

    }
}

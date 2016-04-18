package com.futurewebdynamics.trader.analysers.providers;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import com.futurewebdynamics.trader.statistics.providers.PercentageDrop;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDropBounce extends IAnalyserProvider {

    private double triggerPercentage;

    private PercentageDrop percentageDropStatistic;
    private IsRising isRisingStatistic;

    public PercentageDropBounce(DataWindow dataWindow, int dataWindowSize, PositionsManager positionManager, double triggerPercentage) {
        super(dataWindow, dataWindowSize, positionManager);
        percentageDropStatistic = new PercentageDrop();
        percentageDropStatistic.setDataWindow(dataWindow);

        isRisingStatistic = new IsRising(2);
        isRisingStatistic.setDataWindow(dataWindow);

        this.triggerPercentage = triggerPercentage;

    }

    @Override
    public void tick(NormalisedPriceInformation tickData) {
        if (this.dataWindow.hasGaps()) return;

        Double drop = (Double)percentageDropStatistic.getResult();

        if (drop >= triggerPercentage && (Boolean)isRisingStatistic.getResult()) {
            buy();
        }

    }
}

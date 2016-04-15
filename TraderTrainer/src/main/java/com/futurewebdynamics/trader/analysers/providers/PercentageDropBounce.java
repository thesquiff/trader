package com.futurewebdynamics.trader.analysers.providers;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import com.futurewebdynamics.trader.statistics.providers.PercentageDrop;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDropBounce extends IAnalyserProvider {

    private double triggerPercentage;

    private PercentageDrop percentageDropStatistic;
    private IsRising isRisingStatistic;

    public PercentageDropBounce(int dataWindowSize, double triggerPercentage) {
        super(dataWindowSize);
        percentageDropStatistic = new PercentageDrop();
        percentageDropStatistic.setDataWindow(dataWindow);

        isRisingStatistic = new IsRising(2);
        isRisingStatistic.setDataWindow(dataWindow);

        this.triggerPercentage = triggerPercentage;

    }

    @Override
    public void tick() {
        if (this.dataWindow.hasGaps()) return;

        Double drop = (Double)percentageDropStatistic.getResult();

        if (drop >= triggerPercentage && (Boolean)isRisingStatistic.getResult()) {
            buy();
        }

    }
}

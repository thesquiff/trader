package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import org.apache.log4j.Logger;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageRise extends IStatisticProvider {

    final static Logger logger = Logger.getLogger(PercentageRise.class);

    private int oldestWindowSize;

    public PercentageRise(PriceType priceType) {
        this.setPriceType(priceType);
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public String getName() {
        return "PercentageDrop";
    }

    @Override
    public DataWindow getDataWindow() {
        return super.getDataWindow();
    }

    @Override
    public void setDataWindow(DataWindow dataWindow) {
        super.setDataWindow(dataWindow);
        oldestWindowSize = getDataWindow().getWindowSize();
    }

    public void setDataWindow(DataWindow dataWindow, int oldestWindowSize) {
        this.oldestWindowSize = oldestWindowSize;
        super.setDataWindow(dataWindow);
    }

    @Override
    public Object getResult() {

        double greatestRise = 0.0;
        for (int lookback = 1; lookback <= oldestWindowSize; lookback++) {
            double rise = 0.0;

            NormalisedPriceInformation oldestTick = dataWindow.get(dataWindow.getWindowSize() - 1 - lookback);
            NormalisedPriceInformation newestTick = dataWindow.get(dataWindow.getWindowSize() - 1);

            int newestValue = newestTick.getPrice(this.getPriceType());
            int oldestValue = oldestTick.getPrice(this.getPriceType());

            if (newestValue <= oldestValue) continue;

            rise = (newestValue - oldestValue) / (double) oldestValue * 100.0;
            if (logger.isTraceEnabled()) {
                logger.trace("OldestValue: " + oldestValue + ", NewestValue: " + newestValue + ", %rise: " + rise);
            }

            if (rise > greatestRise) greatestRise = rise;
        }

        return greatestRise;

    }
}

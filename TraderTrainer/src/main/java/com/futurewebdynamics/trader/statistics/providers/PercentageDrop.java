package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDrop extends IStatisticProvider {

    final static Logger logger = Logger.getLogger(PercentageDrop.class);

    private int oldestWindowSize;

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
        dataWindow.debug();
        List<NormalisedPriceInformation> data = dataWindow.getData();

        double greatestDrop = 0.0;
        for (int lookback = 1; lookback <= oldestWindowSize; lookback++) {
            double drop = 0.0;

            int oldestValue = dataWindow.getData().get(dataWindow.getWindowSize() - 1 - lookback).getPrice();
            int newestValue = dataWindow.getData().get(dataWindow.getWindowSize() - 1).getPrice();

            if (newestValue >= oldestValue) continue;

            drop = (newestValue - oldestValue) / (double) oldestValue * -100.0;
            logger.debug("OldestValue: " + oldestValue + ", NewestValue: " + newestValue + ", %drop: " + drop);

            if (drop > greatestDrop) greatestDrop = drop;
        }

        return greatestDrop;

    }
}

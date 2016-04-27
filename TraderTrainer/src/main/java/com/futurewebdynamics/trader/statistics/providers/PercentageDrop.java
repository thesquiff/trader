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

    public void setDataWindow(DataWindow dataWIndow, int oldestWindowSize) {
        this.oldestWindowSize = oldestWindowSize;
        super.setDataWindow(dataWIndow);
    }

    @Override
    public Object getResult() {
        dataWindow.debug();
        List<NormalisedPriceInformation> data = dataWindow.getData();

        int newestValue = dataWindow.getData().get(oldestWindowSize -1).getPrice();
        int oldestValue = dataWindow.getData().get(0).getPrice();

        if (newestValue >= oldestValue) return 0.0;

        double drop =  (newestValue - oldestValue) / (double)oldestValue * -100.0;
        logger.debug("OldestValue: " + oldestValue + ", NewestValue: " + newestValue + ", %drop: " + drop);
        return drop;

    }
}

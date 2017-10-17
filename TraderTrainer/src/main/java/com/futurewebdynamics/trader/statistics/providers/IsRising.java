package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import org.apache.log4j.Logger;

/**
 * Created by 52con on 15/04/2016.
 */
public class IsRising extends IStatisticProvider {

    private int lookBack;

    public IsRising(int lookBack, PriceType priceType) {
        this.lookBack = lookBack;
        this.setPriceType(priceType);
    }

    public int getLookBack() {
        return lookBack;
    }

    public void setLookBack(int lookBack) {
        this.lookBack = lookBack;
    }

    final static Logger logger = Logger.getLogger(IsRising.class);


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
        return null;
    }

    @Override
    public DataWindow getDataWindow() {
        return super.getDataWindow();
    }

    @Override
    public void setDataWindow(DataWindow dataWindow) {
        super.setDataWindow(dataWindow);
    }

    @Override
    //return false if at any time the test value has fallen or remained the same as the previous value
    public Object getResult() {
        int newestValue = dataWindow.get(dataWindow.getWindowSize()-1).getPrice(this.getPriceType());
        logger.debug("Newest value: " + newestValue);

        for (int i = 1; i <= lookBack; i++) {
            int testValue = dataWindow.get(dataWindow.getWindowSize()-1-i).getPrice(this.getPriceType());
            if (testValue >= newestValue) return false;
            newestValue = testValue;
        }

        return true;
    }
}

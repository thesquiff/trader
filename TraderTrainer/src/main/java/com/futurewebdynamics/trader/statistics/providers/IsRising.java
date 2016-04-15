package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;

import java.util.List;

/**
 * Created by 52con on 15/04/2016.
 */
public class IsRising extends IStatisticProvider {

    private int lookBack;

    public IsRising(int lookBack) {
        this.lookBack = lookBack;
    }

    public int getLookBack() {
        return lookBack;
    }

    public void setLookBack(int lookBack) {
        this.lookBack = lookBack;
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
    public Object getResult() {

        List<NormalisedPriceInformation> data = dataWindow.getData();

        int lastValue = data.get(dataWindow.getWindowSize()-1).getPrice();

        for (int i = 0; i < lookBack; i++) {
            int testValue = data.get(dataWindow.getWindowSize()-1-i-1).getPrice();
            if (testValue >= lastValue) return false;
            lastValue = testValue;
        }

        return true;
    }
}

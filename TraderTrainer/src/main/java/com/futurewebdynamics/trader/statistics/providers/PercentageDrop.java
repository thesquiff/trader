package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;

import java.util.List;

/**
 * Created by 52con on 15/04/2016.
 */
public class PercentageDrop extends IStatisticProvider {


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
    }

    @Override
    public Object getResult() {
        List<NormalisedPriceInformation> data = dataWindow.getData();


        return (dataWindow.getData().get(dataWindow.getWindowSize() -1).getPrice() - dataWindow.getData().get(0).getPrice())/dataWindow.getWindowSize() * 100.0;
    }
}

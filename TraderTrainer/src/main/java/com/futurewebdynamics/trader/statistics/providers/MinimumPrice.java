package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.statistics.IStatisticProvider;

/**
 * Created by 52con on 14/04/2016.
 */
public class MinimumPrice extends IStatisticProvider {


    public MinimumPrice() {
        super();
    }

    @Override
    public String getName() {
        return "MinimumPrice";
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
    public Object getResult() {
        ObjectStream.of
        dataWindow.getData();
        return null;
    }
}

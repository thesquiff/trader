package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;

/**
 * Created by 52con on 14/04/2016.
 */
public abstract class IStatisticProvider {

    protected DataWindow dataWindow;

    public abstract int getMajorVersion();
    public abstract int getMinorVersion();
    public abstract String getName();

    public DataWindow getDataWindow() {
        return dataWindow;
    }

    public void setDataWindow(DataWindow dataWindow) {
        this.dataWindow = dataWindow;
    }

    public abstract Object getResult();

    private boolean isShortTrade;

    public boolean isShortTradeCondition() {
        return this.isShortTrade;
    }

    public void setShortTradeCondition(boolean isShortTrade)
    {
        this.isShortTrade = isShortTrade;
    }


}

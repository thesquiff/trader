package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.PriceType;

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

    private PriceType priceType;

    public PriceType getPriceType() {
        return this.priceType;
    }

    public void setPriceType(PriceType priceType)
    {
        this.priceType = priceType;
    }


}

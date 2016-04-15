package com.futurewebdynamics.trader.analysers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

import java.util.ArrayList;

/**
 * Created by 52con on 15/04/2016.
 */
public abstract class IAnalyserProvider {

    public IAnalyserProvider(int dataWindowSize) {
        this.dataWindowSize = dataWindowSize;
    }

    private int dataWindowSize;

    private String name;

    private int majorVersion;

    private int minorVersion;

    private ISellConditionProvider sellConditionProvider;

    protected ArrayList statisticProviders;

    protected DataWindow dataWindow;

    public ISellConditionProvider getSellConditionProvider() {
        return sellConditionProvider;
    }

    public void setSellConditionProvider(ISellConditionProvider sellConditionProvider) {
        this.sellConditionProvider = sellConditionProvider;
    }

    public DataWindow getDataWindow() {
        return dataWindow;
    }

    public void setDataWindow(DataWindow dataWindow) {
        this.dataWindow = dataWindow;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public abstract void tick();

    public void buy() {

    }

}

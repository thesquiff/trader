package com.futurewebdynamics.trader.analysers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

import java.util.ArrayList;

/**
 * Created by 52con on 15/04/2016.
 */
public abstract class IAnalyserProvider {

    private int dataWindowSize;
    private String name;
    private int majorVersion;
    private int minorVersion;
    protected ArrayList statisticProviders;
    protected DataWindow dataWindow;
    private PositionsManager manager;
    private NormalisedPriceInformation tickData;

    public IAnalyserProvider(DataWindow dataWindow, int dataWindowSize, PositionsManager manager) {
        this.dataWindowSize = dataWindowSize;
        this.dataWindow = dataWindow;
        this.manager = manager;
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

    public int getRequiredDataWindowSize() {
        return this.dataWindowSize;
    }

    public abstract void tick(NormalisedPriceInformation tickData);

    public void buy() {
        this.manager.openPosition(tickData.getPrice());

    }

}

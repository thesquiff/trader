package com.futurewebdynamics.trader.analysers;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

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
    private Collection<ISellConditionProvider> sellConditions;

    final static Logger logger = Logger.getLogger(IAnalyserProvider.class);

    public IAnalyserProvider(DataWindow dataWindow, int dataWindowSize, PositionsManager manager, Collection<ISellConditionProvider> sellConditions) {
        this.dataWindowSize = dataWindowSize;
        this.dataWindow = dataWindow;
        this.manager = manager;
        this.sellConditions = sellConditions;
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

    public void buy(NormalisedPriceInformation tickData, boolean isShortTrade) {
        if (!isShortTrade) {
            logger.info("Buying LONG at " + tickData.getAskPrice());
        } else {
            logger.info("Buying SHORT at " + tickData.getBidPrice());
        }
        this.manager.openPosition(tickData, sellConditions, isShortTrade);
    }

}

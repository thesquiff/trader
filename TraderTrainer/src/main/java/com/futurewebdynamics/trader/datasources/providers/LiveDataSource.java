package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.datasources.IDataSource;

/**
 * Created by 52con on 15/04/2016.
 */
public class LiveDataSource implements IDataSource {

    @Override
    public NormalisedPriceInformation getTickData() {
        return null;
    }

    @Override
    public void init(String propertiesFile) {

    }
}

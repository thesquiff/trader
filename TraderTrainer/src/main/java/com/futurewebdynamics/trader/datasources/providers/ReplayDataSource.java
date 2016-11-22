package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.DatabaseCache;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.datasources.IDataSource;
import org.apache.log4j.Logger;

/**
 * Created by 52con on 15/04/2016.
 */
public class ReplayDataSource implements IDataSource {

    private TimeNormalisedDataCache dataCache;

    private int index = 0;

    final static Logger logger = Logger.getLogger(ReplayDataSource.class);

    private String connectionString;

    private int intervalMs;
    private long dateStartTimestampMs;
    private long dateEndTimestampMs;

    public ReplayDataSource(int intervalMs, long dateStartTimestampMs, long dateEndTimestampMs) {
        this.intervalMs = intervalMs;
        this.dateStartTimestampMs = dateStartTimestampMs;
        this.dateEndTimestampMs = dateEndTimestampMs;
    }

    public void init(String connectionString) throws Exception {

        this.connectionString = connectionString;

        DatabaseCache databaseCache = new DatabaseCache(connectionString, dateStartTimestampMs, dateEndTimestampMs);
        databaseCache.loadData();

        dataCache = new TimeNormalisedDataCache(databaseCache.getCache(), intervalMs);
    }

    public long getStartTime() {

        return dataCache.getStartTime();
    }


    public boolean hasMoreData() {
        return index < dataCache.getCacheSize();
    }

    public NormalisedPriceInformation getTickData() {
        if (index >= dataCache.getCacheSize()) return null;
        NormalisedPriceInformation price = dataCache.getIntervalPrices()[index++];

        if (price != null ) {
            logger.debug("index: " + index + ", ask price: " + price.getAskPrice() + " Bid price:" + price.getBidPrice());
        } else {
            logger.debug("index: " + index + ", price is null");
        }

        return price;
    }

    public void reset() {
        index = 0;
    }

}

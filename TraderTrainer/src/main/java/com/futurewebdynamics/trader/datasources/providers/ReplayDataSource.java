package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.DatabaseCache;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceInformation;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.datasources.IDataSource;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

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
    private DatabaseCache databaseCache;

    public ReplayDataSource(int intervalMs, long dateStartTimestampMs, long dateEndTimestampMs) {
        this.intervalMs = intervalMs;
        this.dateStartTimestampMs = dateStartTimestampMs;
        this.dateEndTimestampMs = dateEndTimestampMs;
    }

    public void init(String connectionString) throws Exception {

        this.connectionString = connectionString;

        databaseCache = new DatabaseCache(connectionString, dateStartTimestampMs, dateEndTimestampMs);
        databaseCache.loadData();

        this.dataCache = new TimeNormalisedDataCache(databaseCache.getCache(), intervalMs);
        logger.debug("data cache object set: " + this.dataCache);
    }

    public long getStartTime() {

        return this.dataCache.getStartTime();
    }


    public boolean hasMoreData() {
        return index < this.dataCache.getCacheSize();
    }

    public NormalisedPriceInformation getTickData() throws Exception {
        if (index >= dataCache.getCacheSize()) return null;
        NormalisedPriceInformation price = dataCache.getIntervalPrices()[index++];

        if (price != null && !price.isEmpty()) {
            if (price.getAskPrice()==0) {
                throw new Exception ("incorrect ask price at index " + index);
            }
            logger.debug("index: " + index + ", ask price: " + price.getAskPrice() + " Bid price:" + price.getBidPrice());
        } else {
            logger.debug("index: " + index + ", price is null");
        }

        return price;
    }

    public void reset() {
        index = 0;
    }

    public void dumpData(PrintWriter writer) {

        ArrayList<PriceInformation> data = databaseCache.getCache();

        Iterator izzy = data.iterator();
        while(izzy.hasNext()) {

            PriceInformation price = (PriceInformation)izzy.next();

            writer.println(String.format("%d,%d,%d", price.getTimestamp(), price.getAskPrice(), price.getBidPrice()));

        }

    }

}

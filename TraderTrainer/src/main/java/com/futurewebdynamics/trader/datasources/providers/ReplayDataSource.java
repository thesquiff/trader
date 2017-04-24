package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.DatabaseCache;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceInformation;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.datasources.IDataSource;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 52con on 15/04/2016.
 */
public class ReplayDataSource implements IDataSource {

    final static Logger logger = Logger.getLogger(ReplayDataSource.class);

    private TimeNormalisedDataCache dataCache;

    private int index = 0;
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

    public ReplayDataSource(ReplayDataSource replayDataSource) {
        this.intervalMs = replayDataSource.getIntervalMs();
        this.dateStartTimestampMs = replayDataSource.getDateStartTimestampMs();
        this.dateEndTimestampMs = replayDataSource.getDateEndTimestampMs();
        ReplayDataSource dataSource = new ReplayDataSource(intervalMs, dateStartTimestampMs, dateEndTimestampMs);
        this.dataCache = new TimeNormalisedDataCache(replayDataSource.getDataCache());
    }

    public void init(String connectionString) throws Exception {

        this.connectionString = connectionString;

        databaseCache = new DatabaseCache(connectionString, dateStartTimestampMs, dateEndTimestampMs);
        databaseCache.loadData();

        this.dataCache = new TimeNormalisedDataCache(databaseCache.getCache(), intervalMs);
        logger.debug("data cache object set: " + this.dataCache);
    }

    public void initFromFile(String filename) {
        BufferedReader br = null;
        String line = "";

        ArrayList<PriceInformation> cache = new ArrayList<PriceInformation>();

        try {
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] tokens = line.split(",");
                cache.add(new PriceInformation(Long.parseLong(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
            }

            this.dataCache = new TimeNormalisedDataCache(cache, intervalMs);
            logger.debug("data cache object set: " + this.dataCache);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public long getStartTime() {

        return this.dataCache.getStartTime();
    }


    public boolean hasMoreData() {
        return index < this.dataCache.getCacheSize();
    }

    public NormalisedPriceInformation getTickData() {
        if (index >= dataCache.getCacheSize()) return null;
        NormalisedPriceInformation price = dataCache.getIntervalPrices()[index++];

        if (price != null && !price.isEmpty()) {
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

    public void setDataCache(TimeNormalisedDataCache dataCache) {
        this.dataCache = dataCache;
    }

    public int getIntervalMs() {
        return this.intervalMs;
    }

    public long getDateStartTimestampMs() {
        return this.dateStartTimestampMs;
    }

    public long getDateEndTimestampMs() {
        return this.dateEndTimestampMs;
    }

    public TimeNormalisedDataCache getDataCache() {
        return this.dataCache;
    }

}

package com.futurewebdynamics.trader.common;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by 52con on 14/04/2016.
 */
public class TimeNormalisedDataCache {

    private NormalisedPriceInformation intervalPrices[];

    private long startTimeMs;

    final static Logger logger = Logger.getLogger(TimeNormalisedDataCache.class);

    private int intervalMs;


    public TimeNormalisedDataCache() {

    }

    public TimeNormalisedDataCache(TimeNormalisedDataCache timeNormalisedDataCache) {

        this.setIntervalPrices(timeNormalisedDataCache.getIntervalPrices());

    }

    public TimeNormalisedDataCache(ArrayList<PriceInformation> priceInformation, int intervalMs) throws Exception {
        this.intervalMs = intervalMs;

        init(priceInformation);

    }

    public long getStartTime() {
        return this.startTimeMs;
    }

    private long init(ArrayList<PriceInformation> priceInformation) throws Exception {

        //assume resolution of minutes for now

        logger.info("Initialising time normalised data cache");


        //get time window
        startTimeMs = priceInformation.get(0).getTimestamp();
        long endTimeMs = priceInformation.get(priceInformation.size() - 1).getTimestamp();

        logger.info("Start time is " + startTimeMs);
        logger.info("End time is " + endTimeMs);
        logger.info("Interval ms " + intervalMs);

        long numberOfElements = (endTimeMs - startTimeMs) / intervalMs + 1;

        logger.debug("Cache size is " + numberOfElements);


        if (numberOfElements > Integer.MAX_VALUE) {
            throw new Exception("Too many elements");
        }

        logger.debug("Number of elements: " + numberOfElements);

        intervalPrices = new NormalisedPriceInformation[(int)numberOfElements];

        long targetTimeInMillis = startTimeMs;

        logger.debug("First targetTime: " + targetTimeInMillis);

        int rawDataIndex = 0;
        int rawDataSize = priceInformation.size();

        for (int tick = 0; tick < numberOfElements; tick++) {

            if (tick % 1000 == 0) logger.debug("Tick=" + tick + " elements="+numberOfElements);

            boolean missingData = false;
            boolean priceFound = false;
            PriceInformation rawData = null;
            while (!priceFound && !missingData) {
                if (rawDataIndex >= rawDataSize) {
                    missingData = true;
                } else {
                    rawData = priceInformation.get(rawDataIndex);
                    if (rawData.getTimestamp() < targetTimeInMillis) {
                        //we haven't got to the time segment we want yet
                        rawDataIndex++;
                        //logger.debug("Raw data has run ahead of us");
                    } else if (rawData.getTimestamp() > targetTimeInMillis + intervalMs) {
                        missingData = true;
                    } else if (rawData.getTimestamp() >= targetTimeInMillis && rawData.getTimestamp() <= (targetTimeInMillis + intervalMs)) {
                        priceFound = true;
                        if (rawDataIndex+1 < rawDataSize) {
                            //unless there's another later sample that also matches
                            PriceInformation rawDataNext = priceInformation.get(rawDataIndex + 1);
                            if (rawDataNext.getTimestamp() >= targetTimeInMillis && rawDataNext.getTimestamp() <= (targetTimeInMillis + intervalMs)) {
                                //next sample also matches so we'll pass this one up
                                priceFound = false;
                            }
                        }
                        rawDataIndex++;

                    }
                }
            }

            if (missingData) {
                logger.debug("No data found for " + targetTimeInMillis);
                intervalPrices[tick] = new NormalisedPriceInformation(targetTimeInMillis, true);
            }

            if (priceFound) {

                intervalPrices[tick] = new NormalisedPriceInformation(rawData.getTimestamp(), rawData.getAskPrice(), rawData.getBidPrice(), targetTimeInMillis);
                logger.debug("Price for index " + tick + " is " + rawData.getAskPrice() + " raw data index: " + rawDataIndex);
            }

            targetTimeInMillis += intervalMs;
        }

        return startTimeMs;
    }


    public NormalisedPriceInformation[] getIntervalPrices() {
        return intervalPrices;
    }

    public void setIntervalPrices(NormalisedPriceInformation[] intervalPrices) {
        this.intervalPrices = intervalPrices;
    }

    public int getCacheSize() {
        return this.intervalPrices.length;
    }

}

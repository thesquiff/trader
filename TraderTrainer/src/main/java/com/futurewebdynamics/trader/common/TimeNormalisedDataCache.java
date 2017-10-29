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

    public TimeNormalisedDataCache(TimeNormalisedDataCache timeNormalisedDataCache) {
        this.setIntervalPrices(timeNormalisedDataCache.getIntervalPrices());
    }

    public TimeNormalisedDataCache(ArrayList<PriceInformation> priceInformation, int intervalMs) {
        this.intervalMs = intervalMs;

        init(priceInformation);
    }

    public long getStartTime() {
        return this.startTimeMs;
    }

    private long init(ArrayList<PriceInformation> priceInformation) {

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
            logger.error("Too many elements");
            intervalPrices = null;
            return 0;
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

    public NormalisedPriceInformation[] getDataInRange(long startTimestampMs, long endTimestampMs) {
        int i = 0;
        for (i = 0; i < intervalPrices.length; i++) {
            if (intervalPrices[i].getCorrectedTimestamp() >= startTimestampMs) break;
        }

        int j = 0;
        for (j = i; j < intervalPrices.length; j++) {
            if (intervalPrices[j].getCorrectedTimestamp() >= endTimestampMs) break;
        }

        NormalisedPriceInformation[] buffer  = new NormalisedPriceInformation[j-i + 1];
        java.lang.System.arraycopy(intervalPrices, i, buffer, 0, j-i+1);

        return buffer;

    }

    public void setIntervalPrices(NormalisedPriceInformation[] intervalPrices) {
        this.intervalPrices = intervalPrices;
    }

    public int getCacheSize() {
        return this.intervalPrices.length;
    }

    public PriceRange getPriceRange(long startTimeMs, long endTimeMs, PriceType priceType) {

        int maxPrice = Integer.MIN_VALUE;
        int minPrice = Integer.MAX_VALUE;
        for (int i = 0; i < intervalPrices.length; i++) {

            if (intervalPrices[i].isEmpty()) continue;

            if (priceType == PriceType.BID_PRICE && intervalPrices[i].getBidPrice() == 0) continue;

            if (priceType == PriceType.ASK_PRICE && intervalPrices[i].getAskPrice() == 0) continue;

            if (intervalPrices[i].getCorrectedTimestamp() > startTimeMs) {
                if (priceType == PriceType.BID_PRICE && intervalPrices[i].getBidPrice() > maxPrice) {
                    maxPrice = intervalPrices[i].getBidPrice();
                }

                if (priceType == PriceType.ASK_PRICE && intervalPrices[i].getAskPrice() > maxPrice) {
                    maxPrice = intervalPrices[i].getAskPrice();
                }

                if (priceType == PriceType.BID_PRICE && intervalPrices[i].getBidPrice() < minPrice) {
                    minPrice = intervalPrices[i].getBidPrice();
                }

                if (priceType == PriceType.ASK_PRICE && intervalPrices[i].getAskPrice() < minPrice) {
                    minPrice = intervalPrices[i].getAskPrice();
                }
            }

            if (intervalPrices[i].getCorrectedTimestamp() > endTimeMs) break;

        }

        return new PriceRange(maxPrice, minPrice);

    }
}
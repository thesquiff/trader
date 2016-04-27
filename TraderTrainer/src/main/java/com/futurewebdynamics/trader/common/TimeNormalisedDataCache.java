package com.futurewebdynamics.trader.common;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 52con on 14/04/2016.
 */
public class TimeNormalisedDataCache {

    private NormalisedPriceInformation minutePrices[];

    final static Logger logger = Logger.getLogger(TimeNormalisedDataCache.class);

    public TimeNormalisedDataCache(ArrayList<PriceInformation> priceInformation) {

        init(priceInformation);

    }

    private void init(ArrayList<PriceInformation> priceInformation) {

        //assume resolution of minutes for now

        logger.info("Initialising time normalised data cache");


        //get time window
        int startTime = priceInformation.get(0).getTimestamp();
        int endTime = priceInformation.get(priceInformation.size() - 1).getTimestamp();

        logger.info("Start time is " + startTime);
        logger.info("End time is " + endTime);

        int numberOfElements = (endTime - startTime)/60 + 1;

        logger.debug("Cache size is " + numberOfElements);

        minutePrices = new NormalisedPriceInformation[numberOfElements];

        Calendar targetTime = GregorianCalendar.getInstance();
        targetTime.setTime(new Date((long)startTime*1000));
        targetTime.set(Calendar.SECOND, 0);
        targetTime.set(Calendar.MILLISECOND, 0);

        logger.debug("First targetTime: " + targetTime.getTime());

        for (int tick = 0; tick < numberOfElements; tick++) {

            //logger.debug("target time: " + targetTime.getTime());

            long minute = targetTime.getTimeInMillis() / 1000;

            List<PriceInformation> selectedInfo = priceInformation.stream().filter(p->(p.getTimestamp() >= minute && p.getTimestamp() <= minute+60)).collect(Collectors.toList());

            if (selectedInfo.size() <= 0) {
                logger.debug("No data found for " + new Date(minute*1000));
                minutePrices[tick] = new NormalisedPriceInformation(true);
            } else {
                PriceInformation unnormalised = selectedInfo.get(0);
                minutePrices[tick] = new NormalisedPriceInformation(unnormalised.getTimestamp(), unnormalised.getPrice(), (int)minute);
            }

            targetTime.add(Calendar.MINUTE, 1);

        }
    }


    public NormalisedPriceInformation[] getMinutePrices() {
        return minutePrices;
    }

    public void setMinutePrices(NormalisedPriceInformation[] minutePrices) {
        this.minutePrices = minutePrices;
    }

    public int getCacheSize() {
        return this.minutePrices.length;
    }

}

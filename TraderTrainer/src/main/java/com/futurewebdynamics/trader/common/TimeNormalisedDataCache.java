package com.futurewebdynamics.trader.common;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceInformation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 52con on 14/04/2016.
 */
public class TimeNormalisedDataCache {




    private NormalisedPriceInformation minutePrices[];

    public TimeNormalisedDataCache(ArrayList<PriceInformation> priceInformation) {

        init(priceInformation);

    }

    private void init(ArrayList<PriceInformation> priceInformation) {

        //assume resolution of minutes for now

        //get time window
        int startTime = priceInformation.get(0).getTimestamp();
        int endTime = priceInformation.get(priceInformation.size() - 1).getTimestamp();

        int numberOfElements = (endTime - startTime)/60 + 1;

        minutePrices = new NormalisedPriceInformation[numberOfElements];

        Date beginTime = new Date((long)startTime*1000);

        Calendar beginCal = GregorianCalendar.getInstance();
        beginCal.setTime(beginTime);
        beginCal.set(Calendar.SECOND, 0);
        beginCal.set(Calendar.MILLISECOND, 0);

        Calendar targetTime = GregorianCalendar.getInstance();

        int infoIndex = 0;

        for (int tick = 0; tick < numberOfElements; tick++) {

            long minute = targetTime.getTimeInMillis() / 1000;

            List<PriceInformation> selectedInfo = priceInformation.stream().filter(p->(p.getTimestamp() >= minute && p.getTimestamp() <= minute+60)).collect(Collectors.toList());

            if (selectedInfo.size() <= 0) {
                minutePrices[tick] = null;
            } else {
                PriceInformation unnormalised = selectedInfo.get(0);

                minutePrices[tick] = new NormalisedPriceInformation(unnormalised.getTimestamp(), unnormalised.getPrice(), (int)targetTime.getTimeInMillis() / 1000);
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

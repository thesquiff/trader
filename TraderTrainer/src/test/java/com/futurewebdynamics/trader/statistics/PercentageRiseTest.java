package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.providers.PercentageRise;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Charlie on 29/12/2016.
 */
public class PercentageRiseTest {

    public void PercentageDropReturnsCorrectResults() {

        DataWindow dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,9,133,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,11,132,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,132,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,131,0));
        dataWindow.tick(new NormalisedPriceInformation(0,14,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,16,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,18,130,0)); //newest value

        PercentageRise percentageRise = new PercentageRise(PriceType.ASK_PRICE);
        percentageRise.setDataWindow(dataWindow);
        Assert.assertEquals((18-9)/18.0*100.0, (double)percentageRise.getResult(),0.00001);

        percentageRise = new PercentageRise(PriceType.BID_PRICE);
        percentageRise.setDataWindow(dataWindow);
        Assert.assertEquals(0.0, (double)percentageRise.getResult(),0.00001);

        dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,15,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,14,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,120,0));
        dataWindow.tick(new NormalisedPriceInformation(0,14,110,0));
        dataWindow.tick(new NormalisedPriceInformation(0,16,100,0)); //newest value

        percentageRise = new PercentageRise(PriceType.ASK_PRICE);
        percentageRise.setDataWindow(dataWindow);
        Assert.assertEquals(40.0, (double)percentageRise.getResult(), 0.0000001);


        dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,15,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,14,129,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,128,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,129,0));
        dataWindow.tick(new NormalisedPriceInformation(0,20,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,22,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,23,129,0)); //newest value

        percentageRise = new PercentageRise(PriceType.ASK_PRICE);
        percentageRise.setDataWindow(dataWindow);
        Assert.assertEquals((23-12)/12.0*100.0, (double)percentageRise.getResult(), 0.00001);

        percentageRise = new PercentageRise(PriceType.BID_PRICE);
        percentageRise.setDataWindow(dataWindow);
        Assert.assertEquals(0.769230, (double)percentageRise.getResult(), 0.00001);

    }

    @Test
    public void PercentageRiseDataWindowSetterWorks() {

        DataWindow dataWindow = new DataWindow(10);
        PercentageRise percentageRise = new PercentageRise(PriceType.BID_PRICE);

        percentageRise.setDataWindow(dataWindow);

        Assert.assertEquals(dataWindow, percentageRise.getDataWindow());

    }
}

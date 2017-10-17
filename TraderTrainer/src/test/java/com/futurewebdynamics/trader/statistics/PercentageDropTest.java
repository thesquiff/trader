package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.providers.PercentageDrop;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Charlie on 29/12/2016.
 */
public class PercentageDropTest {

    @Test
    public void PercentageDropReturnsCorrectResults() {

        DataWindow dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,9,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,11,131,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,131,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,131,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,132,0));
        dataWindow.tick(new NormalisedPriceInformation(0,11,132,0));
        dataWindow.tick(new NormalisedPriceInformation(0,10,133,0)); //newest value

        PercentageDrop percentageDrop = new PercentageDrop(PriceType.ASK_PRICE);
        percentageDrop.setDataWindow(dataWindow);
        Assert.assertEquals((13-10)/13.0*100.0, (double)percentageDrop.getResult(),0.00001);

        percentageDrop = new PercentageDrop(PriceType.BID_PRICE);
        percentageDrop.setDataWindow(dataWindow);
        Assert.assertEquals(0.0, (double)percentageDrop.getResult(),0.00001);

        dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,15,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,14,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,11,120,0));
        dataWindow.tick(new NormalisedPriceInformation(0,10,110,0));
        dataWindow.tick(new NormalisedPriceInformation(0,9,100,0)); //newest value

        percentageDrop = new PercentageDrop(PriceType.ASK_PRICE);
        percentageDrop.setDataWindow(dataWindow);
        Assert.assertEquals(40.0, (double)percentageDrop.getResult(), 0.0000001);


        dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,15,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,14,129,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,128,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,129,0));
        dataWindow.tick(new NormalisedPriceInformation(0,20,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,18,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,14,129,0)); //newest value

        percentageDrop = new PercentageDrop(PriceType.ASK_PRICE);
        percentageDrop.setDataWindow(dataWindow);
        Assert.assertEquals(30.0, (double)percentageDrop.getResult(), 0.00001);

        percentageDrop = new PercentageDrop(PriceType.BID_PRICE);
        percentageDrop.setDataWindow(dataWindow);
        Assert.assertEquals(0.769230, (double)percentageDrop.getResult(), 0.00001);

    }

    @Test
    public void PercentageDropDataWindowSetterWorks() {

        DataWindow dataWindow = new DataWindow(10);
        PercentageDrop percentageDrop = new PercentageDrop(PriceType.BID_PRICE);

        percentageDrop.setDataWindow(dataWindow);

        Assert.assertEquals(dataWindow, percentageDrop.getDataWindow());

    }
}

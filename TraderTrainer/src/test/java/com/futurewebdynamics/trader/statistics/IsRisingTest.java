package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Charlie on 29/12/2016.
 */
public class IsRisingTest {

    @Test
    public void IsRisingReturnsCorrectResults() {

        DataWindow dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,13,150,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,12,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,11,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,14,150,0));
        dataWindow.tick(new NormalisedPriceInformation(0,15,151,0)); //newest value

        IsRising isRising = new IsRising(1, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(2, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(3, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(4, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(5, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isRising.getResult());

        isRising = new IsRising(6, PriceType.ASK_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isRising.getResult());


        isRising = new IsRising(1, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(2, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(3, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isRising.getResult());

        isRising = new IsRising(4, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isRising.getResult());

        isRising = new IsRising(5, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isRising.getResult());

        isRising = new IsRising(6, PriceType.BID_PRICE);
        isRising.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isRising.getResult());

    }

    @Test
    public void IsRisingLookbackSetterWorks() {

        IsRising isRising = new IsRising(0,PriceType.BID_PRICE);

        isRising.setLookBack(10);

        Assert.assertEquals(10, isRising.getLookBack());

    }

    @Test
    public void IsRisingDataWindowSetterWorks() {

        DataWindow dataWindow = new DataWindow(10);
        IsRising isRising = new IsRising(0,PriceType.BID_PRICE);

        isRising.setDataWindow(dataWindow);

        Assert.assertEquals(dataWindow, isRising.getDataWindow());

    }
}

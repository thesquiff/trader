package com.futurewebdynamics.trader.statistics;

import com.futurewebdynamics.trader.common.DataWindow;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.statistics.providers.IsFalling;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Charlie on 29/12/2016.
 */
public class IsFallingTest {

    @Test
    public void IsFallingReturnsCorrectResults() {

        DataWindow dataWindow = new DataWindow(7);
        dataWindow.tick(new NormalisedPriceInformation(0,9,130,0)); //oldest value
        dataWindow.tick(new NormalisedPriceInformation(0,11,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,140,0));
        dataWindow.tick(new NormalisedPriceInformation(0,13,130,0));
        dataWindow.tick(new NormalisedPriceInformation(0,12,120,0));
        dataWindow.tick(new NormalisedPriceInformation(0,11,110,0));
        dataWindow.tick(new NormalisedPriceInformation(0,10,100,0)); //newest value

        IsFalling isFalling = new IsFalling(1, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(2, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(3, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(4, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isFalling.getResult());

        isFalling = new IsFalling(5, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isFalling.getResult());

        isFalling = new IsFalling(6, PriceType.ASK_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isFalling.getResult());


        isFalling = new IsFalling(1, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(2, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(3, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(4, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertTrue((Boolean)isFalling.getResult());

        isFalling = new IsFalling(5, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isFalling.getResult());

        isFalling = new IsFalling(6, PriceType.BID_PRICE);
        isFalling.setDataWindow(dataWindow);
        Assert.assertFalse((Boolean)isFalling.getResult());

    }

    @Test
    public void IsFallingLookbackSetterWorks() {

        IsFalling isFalling = new IsFalling(0,PriceType.BID_PRICE);

        isFalling.setLookBack(10);

        Assert.assertEquals(10, isFalling.getLookBack());

    }

    @Test
    public void IsFallingDataWindowSetterWorks() {

        DataWindow dataWindow = new DataWindow(10);
        IsFalling isFalling = new IsFalling(0,PriceType.BID_PRICE);

        isFalling.setDataWindow(dataWindow);

        Assert.assertEquals(dataWindow, isFalling.getDataWindow());

    }
}

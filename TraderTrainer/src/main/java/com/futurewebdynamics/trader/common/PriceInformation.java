package com.futurewebdynamics.trader.common;

/**
 * Created by 52con on 14/04/2016.
 */
public class PriceInformation {

    private long timestamp;
    private int askPrice;
    private int bidPrice;

    public PriceInformation() {
    }

    public PriceInformation(long timestamp, int askPrice, int bidPrice) {
        this.timestamp = timestamp;
        this.askPrice = askPrice;
        this.bidPrice = bidPrice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getAskPrice() {
        return askPrice;
    }

    public int getBidPrice() {
        return bidPrice;
    }

    public void setAskPrice(int price) {
        this.askPrice = price;
    }
    public void setBidPrice(int price) {
        this.bidPrice = price;
    }

}
package com.futurewebdynamics.trader.trainer;

/**
 * Created by 52con on 14/04/2016.
 */
public class PriceInformation {

    private int timestamp;
    private int price;

    public PriceInformation() {
    }

    public PriceInformation(int timestamp, int price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }



}

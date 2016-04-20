package com.futurewebdynamics.trader.common;

/**
 * Created by 52con on 14/04/2016.
 */
public class NormalisedPriceInformation extends PriceInformation {

    private int correctedTimestamp;


    public NormalisedPriceInformation(int timestamp, int price, int correctedTimestamp) {
        super(timestamp, price);
        this.correctedTimestamp = correctedTimestamp;
    }

    public int getCorrectedTimestamp() {
        return correctedTimestamp;
    }

    public void setCorrectedTimestamp(int correctedTimestamp) {
        this.correctedTimestamp = correctedTimestamp;
    }



}

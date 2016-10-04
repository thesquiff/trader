package com.futurewebdynamics.trader.common;

/**
 * Created by 52con on 14/04/2016.
 */
public class NormalisedPriceInformation extends PriceInformation {

    private int correctedTimestamp;

    private boolean isEmpty = false;

    public NormalisedPriceInformation(int timestamp, int askPrice, int bidPrice, int correctedTimestamp) {
        super(timestamp, askPrice, bidPrice);
        this.correctedTimestamp = correctedTimestamp;
    }

    public NormalisedPriceInformation(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public int getCorrectedTimestamp() {
        return correctedTimestamp;
    }

    public void setCorrectedTimestamp(int correctedTimestamp) {
        this.correctedTimestamp = correctedTimestamp;
    }



}

package com.futurewebdynamics.trader.common;

/**
 * Created by 52con on 14/04/2016.
 */
public class NormalisedPriceInformation extends PriceInformation {

    private long correctedTimestamp;

    private boolean isEmpty = false;

    public NormalisedPriceInformation(long timestamp, int askPrice, int bidPrice, long correctedTimestamp) {
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

    public long getCorrectedTimestamp() {
        return correctedTimestamp;
    }

    public void setCorrectedTimestamp(long correctedTimestamp) {
        this.correctedTimestamp = correctedTimestamp;
    }



}

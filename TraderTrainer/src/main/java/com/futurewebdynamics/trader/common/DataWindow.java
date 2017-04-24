package com.futurewebdynamics.trader.common;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.Logger;

import java.util.stream.Collectors;

/**
 * Created by 52con on 14/04/2016.
 */
public class DataWindow {

    private int windowSize;

    private CircularFifoQueue<NormalisedPriceInformation> window;

    private long mostRecentGapMs = 0;

    private boolean hasGaps = false;

    final static Logger logger = Logger.getLogger(DataWindow.class);

    public DataWindow(int windowSize) {
        this.windowSize = windowSize;
        window = new CircularFifoQueue(windowSize);
        for (int i = 0; i < this.windowSize; i++) {
            window.add(new NormalisedPriceInformation(0, true));
        }
    }

    /*
    Inserts data from the data source into the window. Newest value is at end of the window
     */
    public void tick(NormalisedPriceInformation tickData) {

        if (mostRecentGapMs > 0) {
            //check if gap has expired
            if (mostRecentGapMs < window.get(0).getCorrectedTimestamp()) {
                //gap is no longer in the window
                hasGaps = false;
            }
        }

        if (tickData == null || tickData.isEmpty()) {
            mostRecentGapMs = tickData.getCorrectedTimestamp();
            hasGaps = true;
        }

        window.add(tickData);
    }

    public NormalisedPriceInformation get(int index) {
        return window.get(index);
    }

    public int getWindowSize() {
        return windowSize;
    }

    public boolean hasGaps() {

       return hasGaps;
    }

    public void debug() {
        String prices = window.stream().map(p->p.getAskPrice()).map(p->p.toString()).collect(Collectors.joining("],["));
        logger.trace("ask: old [" + prices + "] new");

        prices = window.stream().map(p->p.getBidPrice()).map(p->p.toString()).collect(Collectors.joining("],["));
        logger.trace("bid: old [" + prices + "] new");
    }
}
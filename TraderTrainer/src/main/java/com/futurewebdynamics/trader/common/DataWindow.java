package com.futurewebdynamics.trader.common;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 52con on 14/04/2016.
 */
public class DataWindow {

    private int windowSize;

    //private ArrayList<NormalisedPriceInformation> buffer;
    //private int bufferSize;

    private CircularFifoQueue<NormalisedPriceInformation> window;

    final static Logger logger = Logger.getLogger(TimeNormalisedDataCache.class);

    //private int bufferPointer = 0;

    public DataWindow(int windowSize) {
        this.windowSize = windowSize;
        window = new CircularFifoQueue(windowSize);
        for (int i = 0; i < this.windowSize; i++) {
            window.add(new NormalisedPriceInformation(true));

        }
    }

    public void tick(NormalisedPriceInformation tickData) {
        window.add(tickData);
    }

    public List<NormalisedPriceInformation> getData() {
        Object[] objs = window.toArray();

        List<NormalisedPriceInformation> list = Arrays.asList(Arrays.copyOf(objs, objs.length, NormalisedPriceInformation[].class));

        return list;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public boolean hasGaps() {
        List<NormalisedPriceInformation> data = this.getData();
        return (data.stream().filter(p->p.isEmpty()).count() > 0);
    }

    public void debug() {
        String prices = window.stream().map(p->p.getPrice()).map(p->p.toString()).collect(Collectors.joining("],["));

        logger.debug("old [" + prices + "] new");
    }

}

package com.futurewebdynamics.trader.common;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by 52con on 14/04/2016.
 */
public class DataWindow {

    private int windowSize;

    private ArrayList<NormalisedPriceInformation> buffer;
    private int bufferSize;

    private CircularFifoQueue<NormalisedPriceInformation> window;

    private int bufferPointer = 0;

    public DataWindow(int windowSize) {
        this.windowSize = windowSize;
        window = new CircularFifoQueue(windowSize);
        for (int i = 0; i < this.windowSize; i++) {
            window.add(new NormalisedPriceInformation(true));

        }
    }

    /*public boolean primeWindow() {

        ListIterator izzy = buffer.listIterator();

        for (int i = 0; i < this.windowSize; i++) {

            if (!izzy.hasNext()) {
                //there is not enough data for a window of this size
                return false;
            }

            window.add(izzy.next());

            bufferPointer++;
        }

        return true;
    }*/

    public void tick(NormalisedPriceInformation tickData) {
        window.add(tickData);
        /*if (bufferPointer < buffer.size() - 1 ) {
            window.add(buffer.get(bufferPointer++));
            return true;
        }

        return false;*/
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

}

package com.futurewebdynamics.trader.common;

import com.futurewebdynamics.trader.trainer.NormalisedPriceInformation;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

/**
 * Created by 52con on 14/04/2016.
 */
public class DataWindow {


    private final static int WINDOW_SIZE = 24*60;

    private ArrayList<NormalisedPriceInformation> buffer;
    private int bufferSize;

    private CircularFifoQueue window;

    private int bufferPointer = 0;

    public DataWindow(ArrayList<NormalisedPriceInformation> buffer, int windowSize) {

        this.buffer = buffer;
        this.bufferSize = buffer.size();

        window = new CircularFifoQueue(windowSize);
    }

    public boolean primeWindow() {

        ListIterator izzy = buffer.listIterator();

        for (int i = 0; i < WINDOW_SIZE; i++) {

            if (!izzy.hasNext()) {
                //there is not enough data for a window of this size
                return false;
            }

            window.add(izzy.next());

            bufferPointer++;
        }

        return true;
    }

    public boolean tick() {
        if (bufferPointer < buffer.size() - 1 ) {
            window.add(buffer.get(bufferPointer++));
            return true;
        }

        return false;
    }

    public List<NormalisedPriceInformation> getData() {
        return Arrays.asList(window.toArray());
    }

}

package com.futurewebdynamics.trader.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 52con on 15/04/2016.
 */
public class DataWindowRegistry {

    private HashMap<Integer, DataWindow> dataWindows;

    public DataWindowRegistry() {
        dataWindows = new HashMap<Integer, DataWindow>();
    }

    public DataWindow getWindowOfLength(int length) {
        return dataWindows.get(new Integer(length));
    }

    public DataWindow createWindowOfLength(int length) {
        DataWindow existing = dataWindows.get(new Integer(length));
        if (existing != null) return existing;

        DataWindow newDataWindow = new DataWindow(length);

        dataWindows.put(length, newDataWindow);

        return newDataWindow;
    }

    public void tick(NormalisedPriceInformation tickData) {
        Iterator izzy = this.dataWindows.entrySet().iterator();

        while(izzy.hasNext()) {

            Map.Entry pair = (Map.Entry)izzy.next();

            ((DataWindow)pair.getValue()).tick(tickData);
        }


    }

}

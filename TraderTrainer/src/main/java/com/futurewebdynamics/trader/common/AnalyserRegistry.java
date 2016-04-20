package com.futurewebdynamics.trader.common;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by 52con on 14/04/2016.
 */
public class AnalyserRegistry {

    private ArrayList<IAnalyserProvider> analysers;

    public AnalyserRegistry() {
    }

    public ArrayList<IAnalyserProvider> getAnalysers() {
        return analysers;
    }

    public void setAnalysers(ArrayList<IAnalyserProvider> analysers) {
        this.analysers = analysers;
    }

    public void addAnalyser(IAnalyserProvider analyser) {
        this.analysers.add(analyser);
    }

    public void tick(NormalisedPriceInformation tickData) {
        ListIterator<IAnalyserProvider> izzy = analysers.listIterator();

        while(izzy.hasNext()) {
            izzy.next().tick(tickData);
        }
    }

}

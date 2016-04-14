package com.futurewebdynamics.trader.trainer;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by 52con on 14/04/2016.
 */
public class AnalyserRegistry {

    private ArrayList<IAnalyser> analysers;

    public AnalyserRegistry() {
    }

    public ArrayList<IAnalyser> getAnalysers() {
        return analysers;
    }

    public void setAnalysers(ArrayList<IAnalyser> analysers) {
        this.analysers = analysers;
    }

    public void addAnalyser(IAnalyser analyser) {
        this.analysers.add(analyser);
    }

    public void tick() {
        ListIterator<IAnalyser> izzy = analysers.listIterator();

        while(izzy.hasNext()) {
            izzy.next().tick();
        }
    }

}

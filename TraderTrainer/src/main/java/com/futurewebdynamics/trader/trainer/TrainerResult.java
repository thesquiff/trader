package com.futurewebdynamics.trader.trainer;

/**
 * Created by Charlie on 09/04/2017.
 */
public class TrainerResult {

    private int totalGains;
    private int iterationCounter;
    private boolean isComplete;

    public int getTotalGains() {
        return totalGains;
    }

    public void setTotalGains(int totalGains) {
        this.totalGains = totalGains;
    }

    public int getIterationCounter() {
        return iterationCounter;
    }

    public void setIterationCounter(int iterationCounter) {
        this.iterationCounter = iterationCounter;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}

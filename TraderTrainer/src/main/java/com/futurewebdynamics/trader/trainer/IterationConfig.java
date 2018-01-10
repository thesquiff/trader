package com.futurewebdynamics.trader.trainer;

/**
 * Created by Charlie on 07/04/2017.
 */
public class IterationConfig {


    private long timeSinceLastBuyLimit;
    private String takeProfit;
    private double stopLoss;
    private int bounceLookback;
    private double bounceTrigger;
    private String takeProfitDelays;


    public long getTimeSinceLastBuyLimit() {
        return timeSinceLastBuyLimit;
    }

    public void setTimeSinceLastBuyLimit(long timeSinceLastBuyLimit) {
        this.timeSinceLastBuyLimit = timeSinceLastBuyLimit;
    }

    public String getTakeProfit() {
        return takeProfit;
    }

    public void setTakeProfit(String takeProfit) {
        this.takeProfit = takeProfit;
    }

    public double getStopLoss() {
        return stopLoss;
    }

    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    public int getBounceLookback() {
        return bounceLookback;
    }

    public void setBounceLookback(int bounceLookback) {
        this.bounceLookback = bounceLookback;
    }

    public double getBounceTrigger() {
        return bounceTrigger;
    }

    public void setBounceTrigger(double bounceTrigger) {
        this.bounceTrigger = bounceTrigger;
    }

    public String getTakeProfitDelays() {
        return takeProfitDelays;
    }

    public void setTakeProfitDelays(String takeProfitDelays) {
        this.takeProfitDelays = takeProfitDelays;
    }
}

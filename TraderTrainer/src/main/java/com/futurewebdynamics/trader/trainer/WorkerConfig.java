package com.futurewebdynamics.trader.trainer;

/**
 * Created by Charlie on 06/04/2017.
 */
public class WorkerConfig {

    private int analysisIntervalMs;
    private int tickSleepMs;
    private double bounceTriggerStart;
    private double bounceTriggerEnd;
    private int bounceLookbackStart;
    private int bounceLookbackEnd;
    private int bounceLookbackStep;
    private double takeProfitStart;
    private double takeProfitEnd;
    private double takeProfitShort;
    private double stopLossStart;
    private double stopLossEnd;
    private double stopLossShort;
    private int upperBuyLimit;
    private int lowerBuyLimit;
    private long timeSinceLastBuyLimitStart;
    private long timeSinceLastBuyLimitEnd;
    private long timeSinceLastBuyLimitStep;
    private int windowSize;
    private boolean enableShortTrade;
    private boolean enableLongTrade;
    private String masterOutputFolder;
    private boolean createTickerFile;

    public int getAnalysisIntervalMs() {
        return analysisIntervalMs;
    }

    public void setAnalysisIntervalMs(int analysisIntervalMs) {
        this.analysisIntervalMs = analysisIntervalMs;
    }

    public int getTickSleepMs() {
        return tickSleepMs;
    }

    public void setTickSleepMs(int tickSleepMs) {
        this.tickSleepMs = tickSleepMs;
    }

    public double getBounceTriggerStart() {
        return bounceTriggerStart;
    }

    public void setBounceTriggerStart(double bounceTriggerStart) {
        this.bounceTriggerStart = bounceTriggerStart;
    }

    public double getBounceTriggerEnd() {
        return bounceTriggerEnd;
    }

    public void setBounceTriggerEnd(double bounceTriggerEnd) {
        this.bounceTriggerEnd = bounceTriggerEnd;
    }

    public int getBounceLookbackStart() {
        return bounceLookbackStart;
    }

    public void setBounceLookbackStart(int bounceLookbackStart) {
        this.bounceLookbackStart = bounceLookbackStart;
    }

    public int getBounceLookbackEnd() {
        return bounceLookbackEnd;
    }

    public void setBounceLookbackEnd(int bounceLookbackEnd) {
        this.bounceLookbackEnd = bounceLookbackEnd;
    }

    public int getBounceLookbackStep() {
        return bounceLookbackStep;
    }

    public void setBounceLookbackStep(int bounceLookbackStep) {
        this.bounceLookbackStep = bounceLookbackStep;
    }

    public double getTakeProfitStart() {
        return takeProfitStart;
    }

    public void setTakeProfitStart(double takeProfitStart) {
        this.takeProfitStart = takeProfitStart;
    }

    public double getTakeProfitEnd() {
        return takeProfitEnd;
    }

    public void setTakeProfitEnd(double takeProfitEnd) {
        this.takeProfitEnd = takeProfitEnd;
    }

    public double getTakeProfitShort() {
        return takeProfitShort;
    }

    public void setTakeProfitShort(double takeProfitShort) {
        this.takeProfitShort = takeProfitShort;
    }

    public double getStopLossStart() {
        return stopLossStart;
    }

    public void setStopLossStart(double stopLossStart) {
        this.stopLossStart = stopLossStart;
    }

    public double getStopLossEnd() {
        return stopLossEnd;
    }

    public void setStopLossEnd(double stopLossEnd) {
        this.stopLossEnd = stopLossEnd;
    }

    public double getStopLossShort() {
        return stopLossShort;
    }

    public void setStopLossShort(double stopLossShort) {
        this.stopLossShort = stopLossShort;
    }

    public int getUpperBuyLimit() {
        return upperBuyLimit;
    }

    public void setUpperBuyLimit(int upperBuyLimit) {
        this.upperBuyLimit = upperBuyLimit;
    }

    public int getLowerBuyLimit() {
        return lowerBuyLimit;
    }

    public void setLowerBuyLimit(int lowerBuyLimit) {
        this.lowerBuyLimit = lowerBuyLimit;
    }

    public long getTimeSinceLastBuyLimitStart() {
        return timeSinceLastBuyLimitStart;
    }

    public void setTimeSinceLastBuyLimitStart(long timeSinceLastBuyLimitStart) {
        this.timeSinceLastBuyLimitStart = timeSinceLastBuyLimitStart;
    }

    public long getTimeSinceLastBuyLimitEnd() {
        return timeSinceLastBuyLimitEnd;
    }

    public void setTimeSinceLastBuyLimitEnd(long timeSinceLastBuyLimitEnd) {
        this.timeSinceLastBuyLimitEnd = timeSinceLastBuyLimitEnd;
    }

    public long getTimeSinceLastBuyLimitStep() {
        return timeSinceLastBuyLimitStep;
    }

    public void setTimeSinceLastBuyLimitStep(long timeSinceLastBuyLimitStep) {
        this.timeSinceLastBuyLimitStep = timeSinceLastBuyLimitStep;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public boolean isEnableShortTrade() {
        return enableShortTrade;
    }

    public void setEnableShortTrade(boolean enableShortTrade) {
        this.enableShortTrade = enableShortTrade;
    }

    public boolean isEnableLongTrade() {
        return enableLongTrade;
    }

    public void setEnableLongTrade(boolean enableLongTrade) {
        this.enableLongTrade = enableLongTrade;
    }

    public String getMasterOutputFolder() {
        return masterOutputFolder;
    }

    public void setMasterOutputFolder(String masterOutputFolder) {
        this.masterOutputFolder = masterOutputFolder;
    }

    public boolean isCreateTickerFile() {
        return createTickerFile;
    }

    public void setCreateTickerFile(boolean createTickerFile) {
        this.createTickerFile = createTickerFile;
    }
}

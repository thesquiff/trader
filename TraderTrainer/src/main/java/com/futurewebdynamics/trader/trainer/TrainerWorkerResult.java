package com.futurewebdynamics.trader.trainer;

/**
 * Created by Charlie on 18/10/2017.
 */
public class TrainerWorkerResult {

    public double averageLengthOfTrade;
    public double averageLengthOfTradeShort;
    public double averageLengthOfTradeLong;
    public int numberOfClosedTrades;
    public int numberOfClosedTradesShort;
    public int numberOfClosedTradesLong;
    public int totalNumberOfOpenedTrades;
    public int numberOfUnclosedTrades;
    public int realisedProfitLoss;
    public int realisedProfitLossLong;
    public int realisedProfitLossShort;
    public double balanceOfOpenTrades;
    public double averageProfitShort;
    public double averageProfitLong;
    public double averageLossShort;
    public double averageLossLong;

    public int iteration;
    public boolean isComplete;
}
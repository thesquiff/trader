package com.futurewebdynamics.trader.trader.providers;

import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by 52con on 15/04/2016.
 */
public class PseudoTrader implements ITrader {

    private Connection connection;

    private String connectionString;

    final static Logger logger = Logger.getLogger(PseudoTrader.class);

    private int uniqueIds = 0;

    private int leverage;

    private int balance;

    private int units;

    private ArrayList<Position> positions;

    public PseudoTrader(int leverage, int intialBalance, int units) {
        this.leverage = leverage;
        this.balance = balance;
        this.units = units;
    }

    public void init(String propertiesFile) {

    }

    public int getCurrentBalanceOfCompletedTrades() {
        return this.balance;
    }

    @Override
    public int getStandardUnits() {
        return this.units;
    }

    @Override
    public int getStandardLeverage() {
        return this.leverage;
    }

    @Override
    public boolean openPosition(Position position) {

        if (position.isShortTrade()) {

        } else {
            if (position.getTargetOpenPrice()*units > balance) return false;
        }

        position.setUniqueId(uniqueIds++);
        position.setStatus(PositionStatus.OPEN);
        position.setActualOpenPrice(position.getTargetOpenPrice());
        logger.debug("Setting actual open price on poisition to: " + position.getTargetOpenPrice());

        return true;
    }

    @Override
    public boolean checkPosition(Position position) {
        return false;
    }

    @Override
    public boolean closePosition(Position position, long replayTimestampMs) {

        position.setActualSellPrice(position.getTargetSellPrice());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(replayTimestampMs);

        position.setTimeClosed(calendar);

        position.setStatus(PositionStatus.CLOSED);

        if (position.isShortTrade()) {
            balance += (position.getActualSellPrice() - position.getActualOpenPrice()) * leverage * units;
        } else {
            balance += (position.getActualSellPrice() - position.getActualOpenPrice()) * leverage * units;
        }
        return true;


    }

    @Override
    public void getPositions(PositionsManager manager, Collection<ISellConditionProvider> defaultSellCondtions) {

    }
}
package com.futurewebdynamics.trader.trader;

import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;

import java.util.Collection;

/**
 * Created by 52con on 15/04/2016.
 */
public interface ITrader {

    boolean openPosition(Position position);
    boolean checkPosition(Position position);
    boolean closePosition(Position position, long replayTimestamp);

    int getStandardUnits();
    int getStandardLeverage();

    void getPositions(PositionsManager manager, Collection<ISellConditionProvider> defaultSellCondtions);

}

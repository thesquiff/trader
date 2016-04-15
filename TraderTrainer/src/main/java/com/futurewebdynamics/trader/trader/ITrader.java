package com.futurewebdynamics.trader.trader;

import com.futurewebdynamics.trader.positions.Position;

import java.util.ArrayList;

/**
 * Created by 52con on 15/04/2016.
 */
public interface ITrader {

    boolean openPosition(Position position);
    boolean checkPosition();
    boolean closePosition();

    ArrayList<Position> getPositions();

}

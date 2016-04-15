package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.trader.ITrader;

import java.util.ArrayList;

/**
 * Created by 52con on 15/04/2016.
 */
public class PositionsManager {

    public ArrayList<Position> positions;

    private ITrader trader;

    public PositionsManager() {
        positions = new ArrayList<Position>();
    }

    public ITrader getTrader() {
        return trader;
    }

    public void setTrader(ITrader trader) {
        this.trader = trader;
    }

    public void init() {
        positions = trader.getPositions();
    }


}

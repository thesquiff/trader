package com.futurewebdynamics.trader.riskfilters;

/**
 * Created by Charlie on 04/10/2016.
 */
public enum MatchTradeEnum {
    LONG_AND_SHORT (0),
    LONG_ONLY (1),
    SHORT_ONLY (2);

    private final int value;

    private MatchTradeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}


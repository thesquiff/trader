package com.futurewebdynamics.trader.positions;

/**
 * Created by 52con on 15/04/2016.
 */
public enum PositionStatus {
    BUYING (0),
    OPEN (1),
    SELLING (2),
    CLOSED (3);

    private final int value;

    private PositionStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.trader.ITrader;

import java.util.Calendar;

/**
 * Created by 52con on 15/04/2016.
 */
public class Position {

    private PositionStatus status;
    private int quantity;
    private int targetOpenPrice;
    private int actualOpenPrice;
    private int targetSellPrice;
    private int actualSellPrice;
    private Calendar timeOpened;
    private Calendar timeClosed;
    private int leverage;

    public Position() {
    }

    public PositionStatus getStatus() {
        return status;
    }

    public void setStatus(PositionStatus status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTargetOpenPrice() {
        return targetOpenPrice;
    }

    public void setTargetOpenPrice(int targetOpenPrice) {
        this.targetOpenPrice = targetOpenPrice;
    }

    public int getActualOpenPrice() {
        return actualOpenPrice;
    }

    public void setActualOpenPrice(int actualOpenPrice) {
        this.actualOpenPrice = actualOpenPrice;
    }

    public int getTargetSellPrice() {
        return targetSellPrice;
    }

    public void setTargetSellPrice(int targettSellPrice) {
        this.targetSellPrice = targettSellPrice;
    }

    public int getActualSellPrice() {
        return actualSellPrice;
    }

    public void setActualSellPrice(int actualSellPrice) {
        this.actualSellPrice = actualSellPrice;
    }

    public Calendar getTimeOpened() {
        return timeOpened;
    }

    public void setTimeOpened(Calendar timeOpened) {
        this.timeOpened = timeOpened;
    }

    public Calendar getTimeClosed() {
        return timeClosed;
    }

    public void setTimeClosed(Calendar timeClosed) {
        this.timeClosed = timeClosed;
    }

    public int getLeverage() {
        return leverage;
    }

    public void setLeverage(int leverage) {
        this.leverage = leverage;
    }



}

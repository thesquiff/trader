package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.trader.ITrader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

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
    private PositionsManager positionsManager;

    private long uniqueId;

    public ArrayList<ISellConditionProvider> sellConditions;

    public Position() {
    }

    public PositionStatus getStatus() {
        return status;
    }

    public ArrayList<ISellConditionProvider> getSellConditions() {
        return sellConditions;
    }

    public void addSellCondition(ISellConditionProvider sellCondition) {
        this.sellConditions.add(sellCondition);
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

    public PositionsManager getPositionsManager() {
        return positionsManager;
    }

    public void setPositionsManager(PositionsManager positionsManager) {
        this.positionsManager = positionsManager;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void tick(NormalisedPriceInformation tickData) {
        Iterator izzy = sellConditions.iterator();
        while(izzy.hasNext()) {
            ((ISellConditionProvider)izzy.next()).tick(tickData);
        }
    }

    public void sell(int targetSellPrice) {
        this.positionsManager.sellPosition(this, targetSellPrice);
    }
}

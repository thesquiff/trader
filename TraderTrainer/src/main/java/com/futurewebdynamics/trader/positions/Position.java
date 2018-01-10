package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import org.apache.log4j.Logger;

import java.util.*;

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
    private PositionsManager positionsManager;
    private boolean isShortTrade;
    private int units;
    private int leverage;

    private long uniqueId;

    public ArrayList<ISellConditionProvider> sellConditions;

    final static Logger logger = Logger.getLogger(Position.class);

    public Position(int units, int leverage) {
        sellConditions = new ArrayList<ISellConditionProvider>();
        this.units = units;
        this.leverage = leverage;
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

    public void addSellConditions(Collection sellConditions) {
        this.sellConditions.addAll(sellConditions);
    }

    public void setShortTrade(boolean isShortTrade) {
        this.isShortTrade = isShortTrade;
    }

    public int getUnits() {
        return this.units;
    }

    public boolean isShortTrade() {
        return this.isShortTrade;
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

    public void tick(NormalisedPriceInformation tickData, long testTime) {
        if (tickData.isEmpty()) return;
        //logger.debug("Evaluating sell conditions for " + this.uniqueId);
        Iterator izzy = sellConditions.iterator();
        while(izzy.hasNext()) {
            ((ISellConditionProvider)izzy.next()).tick(this, tickData, testTime);
        }
    }

    public void tick(NormalisedPriceInformation tickData) {
        if (tickData.isEmpty()) return;
        //logger.debug("Evaluating sell conditions for " + this.uniqueId);
        Iterator izzy = sellConditions.iterator();
        while(izzy.hasNext()) {
            ((ISellConditionProvider)izzy.next()).tick(this, tickData);
        }
    }

    public void sell(NormalisedPriceInformation tickData) {
        this.positionsManager.sellPosition(this, tickData);
    }

    public ISellConditionProvider getSellConditionOfType(Class classType) {
        ListIterator izzy = sellConditions.listIterator();
        while(izzy.hasNext()) {

            ISellConditionProvider sellCondition = (ISellConditionProvider) izzy.next();

            if (sellCondition.getClass() == classType) {
                return sellCondition;
            }
        }
        return null;
    }
}

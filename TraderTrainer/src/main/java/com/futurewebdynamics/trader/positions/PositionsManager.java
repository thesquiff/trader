package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by 52con on 15/04/2016.
 */
public class PositionsManager {

    public ArrayList<Position> positions;

    private ITrader trader;

    final static Logger logger = Logger.getLogger(PositionsManager.class);

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

    public void tick(NormalisedPriceInformation tickData) {
        if (tickData.isEmpty()) return;

        for (int i = 0; i < this.positions.size(); i++)
        {
            Position position = this.positions.get(i);
            if (position.getStatus() == PositionStatus.OPEN) {
                position.tick(tickData);
            }
        }
    }

    public void openPosition(int price, Collection<ISellConditionProvider> sellConditions) {
        Position position = new Position();
        position.setPositionsManager(this);

        this.positions.add(position);
        position.setStatus(PositionStatus.BUYING);
        position.setTargetOpenPrice(price);

        Calendar cal = GregorianCalendar.getInstance();
        position.setTimeOpened(cal);
        position.setActualOpenPrice(price);
        position.setTargetOpenPrice(price);

        Iterator izzy = sellConditions.iterator();
        while(izzy.hasNext()) {
            ISellConditionProvider sellCondition = (ISellConditionProvider)izzy.next();
            sellCondition.setBuyPrice(price);
        }

        position.addSellConditions(sellConditions);

        this.trader.openPosition(position);
        logger.debug("Position Status: " + position.getStatus().toString());

    }

    public void sellPosition(Position position, int targetPrice) {
        logger.info("Selling position " + position.getUniqueId() + " opened at " + position.getTargetOpenPrice() + " for " + targetPrice);
        /*try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Calendar cal = GregorianCalendar.getInstance();
        position.setTimeClosed(cal);

        position.setStatus(PositionStatus.SELLING);
        position.setTargetSellPrice(targetPrice);
        position.setActualSellPrice(targetPrice);
        this.trader.closePosition(position);
    }

    public void printStats() {

        System.out.format("%-7s%-14s%-7s%-7s%-14s%-7s%-7s%-7s\n", new String[]{"ID", "TO", "TOP", "AOP", "TC", "TCP", "ACP", "Q"});

        Iterator izzy = positions.iterator();
        while(izzy.hasNext()) {
            Position position = (Position)izzy.next();

            System.out.format("%-7d%-14d%-7d%-7d%-14d%-7d%-7d%-7d\n", position.getUniqueId(), position.getTimeOpened().getTimeInMillis()/1000, position.getTargetOpenPrice(), position.getActualOpenPrice(), (position.getTimeClosed() !=null) ? position.getTimeClosed().getTimeInMillis()/1000 : 0, position.getTargetSellPrice(), position.getActualSellPrice(), position.getQuantity());

        }

        int totalTrades = (int)positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).count();

        System.out.println("Total closed trades: " + totalTrades);
        System.out.println("% closed at profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalTrades * 100);

        System.out.println("Average Profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        System.out.println("Average Loss: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());

        System.out.println("Total Gains: " + positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum());

    }

    public int getTotalGains() {
        return positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum();
    }
}

package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.providers.StopLoss;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfit;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

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
        Iterator izzy = this.positions.iterator();
        while (izzy.hasNext()) {
            Position pos = ((Position)izzy.next());
            if (pos.getStatus() == PositionStatus.OPEN) pos.tick(tickData);
        }
    }

    public void openPosition(int price) {
        Position position = new Position();
        position.setPositionsManager(this);

        this.positions.add(position);
        position.setStatus(PositionStatus.BUYING);
        position.setTargetOpenPrice(price);

        Calendar cal = GregorianCalendar.getInstance();
        position.setTimeOpened(cal);

        TakeProfit tp = new TakeProfit(position, price, (int)Math.round(price * .05));
        StopLoss sl = new StopLoss(position, price, (int)Math.round(price * .1));

        position.addSellCondition(tp);
        position.addSellCondition(sl);

        this.trader.openPosition(position);
        logger.debug("Position Status: " + position.getStatus().toString());
    }

    public void sellPosition(Position position, int targetPrice) {
        logger.info("Selling position " + position.getUniqueId() + " opened at " + position.getTargetOpenPrice() + " for " + targetPrice);
        try {
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Calendar cal = GregorianCalendar.getInstance();
        position.setTimeClosed(cal);

        position.setStatus(PositionStatus.SELLING);
        position.setTargetSellPrice(targetPrice);
        this.trader.closePosition(position);
    }
}

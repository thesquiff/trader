package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.sellconditions.providers.StopLoss;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfit;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.helpers.OptionConverter;

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
        Iterator izzy = this.positions.iterator();
        while (izzy.hasNext()) {
            ((Position)izzy.next()).tick(tickData);
        }
    }

    public void openPosition(int price) {
        Position position = new Position();

        this.positions.add(position);
        position.setStatus(PositionStatus.BUYING);
        position.setTargetOpenPrice(price);

        Calendar cal = GregorianCalendar.getInstance();
        position.setTargetOpenPrice((int)(cal.getTimeInMillis() / 1000));

        this.trader.openPosition(position);

        TakeProfit tp = new TakeProfit((int)Math.round(price * .02));
        StopLoss sl = new StopLoss((int)Math.round(price * .1));



    }
}

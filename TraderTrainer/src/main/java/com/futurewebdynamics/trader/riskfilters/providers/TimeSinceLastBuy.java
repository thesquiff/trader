package com.futurewebdynamics.trader.riskfilters.providers;

import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import org.apache.log4j.Logger;

import java.util.OptionalLong;

/**
 * Created by Charlie on 26/09/2016.
 */
public class TimeSinceLastBuy implements IRiskFilter {

    final static Logger logger = Logger.getLogger(TimeSinceLastBuy.class);

    private PositionsManager manager;
    private long thresholdInMillis;
    private long testTimeMs;

    public TimeSinceLastBuy(PositionsManager manager, long thresholdInMillis) {
        this.manager = manager;
        this.thresholdInMillis = thresholdInMillis;
    }

    public boolean proceedWithBuy(int buyPrice, boolean isShortTrade) {

        OptionalLong lastBuyTime = manager.positions.stream().filter(p->p.getStatus()== PositionStatus.OPEN || p.getStatus() == PositionStatus.BUYING).mapToLong(p->p.getTimeOpened().getTimeInMillis()).max();
        if (!lastBuyTime.isPresent()) return true;


        long currentTimeMs = System.currentTimeMillis();

        if (testTimeMs > 0) {
            currentTimeMs = testTimeMs;
            testTimeMs = 0;
        }

        if ((currentTimeMs-lastBuyTime.getAsLong()) < this.thresholdInMillis) {
            logger.info("Blocking purchase as only " + (currentTimeMs-lastBuyTime.getAsLong()) + " has passed since last buy");
            return false;
        }

        return true;
    }

    @Override
    public void setTestTimeMs(long testTimeMs) {
        this.testTimeMs = testTimeMs;

    }


}

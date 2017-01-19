package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 52con on 15/04/2016.
 */
public class PositionsManager {

    public List<Position> positions;
    public List<IRiskFilter> riskFilters;

    private ITrader trader;
    private ExecutorService executor;
    private boolean isReplayMode;

    private int balanceOfOpenTrades;

    final static Logger logger = Logger.getLogger(PositionsManager.class);

    public PositionsManager(boolean isReplayMode) {
        positions = Collections.synchronizedList(new ArrayList<Position>());
        riskFilters = Collections.synchronizedList(new ArrayList<IRiskFilter>());
        executor = Executors.newCachedThreadPool();
        this.isReplayMode = isReplayMode;
    }

    public ITrader getTrader() {
        return trader;
    }

    public void setTrader(ITrader trader) {
        this.trader = trader;
    }

    public void init() {

    }

    public void tick(NormalisedPriceInformation tickData) {
        logger.trace("Calling tick() on " + this.positions.size() + " positions");
        if (tickData.isEmpty()) {
            logger.trace("tick data is empty");
            return;
        }

        int liveBalance = 0;

        for (int i = 0; i < this.positions.size(); i++)
        {
            Position position = this.positions.get(i);
            if (position.getStatus() == PositionStatus.OPEN) {
                this.executor.execute(new Runnable() {
                    public void run() {
                        position.tick(tickData);
                    }
                });

                if (position.isShortTrade()) {
                    liveBalance += (position.getActualOpenPrice() - tickData.getAskPrice()) * position.getUnits() * position.getLeverage();
                } else {
                    liveBalance += (tickData.getBidPrice() - position.getActualOpenPrice()) * position.getUnits() * position.getLeverage();
                }
            }
        }

        this.balanceOfOpenTrades = liveBalance;
    }

    public int getBalanceOfOpenTrades() {
        return balanceOfOpenTrades;
    }

    public void openPosition(NormalisedPriceInformation tickData, Collection<ISellConditionProvider> templateSellConditions, boolean isShortTrade) {
        int price = isShortTrade ? tickData.getBidPrice() : tickData.getAskPrice();

        logger.debug("Assessing " + this.riskFilters.size() + " risk filters");
        for (IRiskFilter riskFilter : this.riskFilters) {
            riskFilter.setTestTimeMs(tickData.getTimestamp());
            if (!riskFilter.proceedWithBuy(price, isShortTrade)) {
                logger.debug("Cancelling proposed buy due to risk filters");
                return;
            }
        }

        Position position = new Position(trader.getStandardUnits(), trader.getStandardLeverage());
        position.setPositionsManager(this);
        position.setShortTrade(isShortTrade);

        position.setStatus(PositionStatus.BUYING);
        position.setTargetOpenPrice(price);

        Calendar cal = GregorianCalendar.getInstance();
        if (isReplayMode) {
            cal.setTimeInMillis(tickData.getCorrectedTimestamp());
        }
        position.setTimeOpened(cal);


        for (ISellConditionProvider sellPosition : templateSellConditions) {
            if (sellPosition.isShortTradeCondition() != isShortTrade) continue;

            ISellConditionProvider copiedSellCondition = sellPosition.makeCopy();
            position.addSellCondition(copiedSellCondition);
        }

        if (this.trader.openPosition(position)) {
            this.positions.add(position);
            logger.debug("Position Status: " + position.getStatus().toString());
        } else {
            //position was refused by the trader
            position = null;
            logger.info("Purchase blocked due to lack of funds");
        }

    }


    public void sellPosition(Position position, NormalisedPriceInformation tickData) {

        int targetPrice = position.isShortTrade() ? tickData.getAskPrice() : tickData.getBidPrice();

        logger.info("Selling " + (position.isShortTrade() ? "short" : "long") + " position " + position.getUniqueId() + " opened at " + position.getActualOpenPrice() + " for " + targetPrice);

        Calendar cal = GregorianCalendar.getInstance();
        if (isReplayMode) {
            cal.setTimeInMillis(tickData.getCorrectedTimestamp());
        }
        position.setTimeClosed(cal);

        position.setStatus(PositionStatus.SELLING);
        position.setTargetSellPrice(targetPrice);
        this.trader.closePosition(position, tickData.getCorrectedTimestamp(), targetPrice);
    }

    public void printStats() {

        logger.debug(String.format("%-7s%-14s%-7s%-7s%-14s%-7s%-7s%-7s\n", new String[]{"ID", "TO", "TOP", "AOP", "TC", "TCP", "ACP", "Q"}));

        Iterator izzy = positions.iterator();
        while(izzy.hasNext()) {
            Position position = (Position)izzy.next();
            logger.debug(String.format("%-7d%-14d%-7d%-7d%-14d%-7d%-7d%-7d\n",position.getUniqueId(), position.getTimeOpened().getTimeInMillis()/1000, position.getTargetOpenPrice(), position.getActualOpenPrice(), (position.getTimeClosed() !=null) ? position.getTimeClosed().getTimeInMillis()/1000 : 0, position.getTargetSellPrice(), position.getActualSellPrice(), position.getQuantity()));
        }

        int totalLongTrades = (int)positions.stream().filter(p -> !p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).count();
        int totalShortTrades = (int)positions.stream().filter(p -> p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).count();

        logger.info("Total LONG closed trades: " + totalLongTrades);
        logger.info("% LONG closed at profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalLongTrades * 100);

        logger.info("Average Long Profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        logger.info("Average Long Loss: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());

        double totalLongGains = positions.stream().filter(p -> !p.isShortTrade() && p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum();
        logger.info("Total LONG Gains: " + totalLongGains);

        OptionalDouble averageMsOpenLong = positions.stream().filter(p->!p.isShortTrade() && p.getTimeClosed() != null).mapToLong(p->p.getTimeClosed().getTimeInMillis() - p.getTimeOpened().getTimeInMillis()).average();
        if (averageMsOpenLong.isPresent()) logger.info("Average length of long trade " + averageMsOpenLong.getAsDouble()/1000 + "s");

        logger.info("Total SHORT closed trades: " + totalShortTrades);
        logger.info("% SHORT closed at profit: " + positions.stream().filter(p->p.isShortTrade() &&  p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).count() / (double)totalShortTrades * 100);
        logger.info("Average Short Profit: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());
        logger.info("Average Short Loss: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());

        double totalShortGains = positions.stream().filter(p -> p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).mapToInt(p->(p.getActualOpenPrice() - p.getActualSellPrice())).sum();
        logger.info("Total SHORT Gains: " + totalShortGains);

        OptionalDouble averageMsOpenShort = positions.stream().filter(p->p.isShortTrade() && p.getTimeClosed() != null).mapToLong(p->p.getTimeClosed().getTimeInMillis() - p.getTimeOpened().getTimeInMillis()).average();
        if (averageMsOpenShort.isPresent()) logger.info("Average length of short trade " + averageMsOpenShort + "s");

        logger.info("TOTAL GAINS: " + (totalLongGains + totalShortGains));
    }

    public void dumpToCsv(String filename) {
        try {
            List<String> lines = new ArrayList<String>();
            lines.add("id, type, timeOpened, openPricePence, timeClosed, closePricePence, units, leverage, profitPence");
            for (Position p: this.positions) {

                int profit = p.getStatus() == PositionStatus.CLOSED ?  p.isShortTrade() ? (p.getActualOpenPrice() - p.getActualSellPrice()) * p.getUnits() * p.getLeverage() : (p.getActualSellPrice() - p.getActualOpenPrice()) * p.getUnits() * p.getLeverage() : 0;

                lines.add(String.format("%d,%s,%d,%d,%d,%d,%d,%d,%d", p.getUniqueId(), p.isShortTrade() ? "SHORT" : "LONG", p.getTimeOpened().getTimeInMillis(), p.getActualOpenPrice(), p.getTimeClosed() == null ? 0 : p.getTimeClosed().getTimeInMillis(), p.getActualSellPrice(), p.getUnits(), p.getLeverage(), profit));
            }

            Path file = Paths.get(filename);
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void addExistingPosition(Position p) {
        p.setPositionsManager(this);
        this.positions.add(p);
    }
}

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
import java.util.stream.Collectors;

/**
 * Created by 52con on 15/04/2016.
 */
public class PositionsManager {

    public List<Position> positions;
    public List<IRiskFilter> riskFilters;

    private ITrader trader;
    private ExecutorService executor;
    private boolean isReplayMode;

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

        for (int i = 0; i < this.positions.size(); i++)
        {
            Position position = this.positions.get(i);
            if (position.getStatus() == PositionStatus.OPEN) {
                this.executor.execute(new Runnable() {
                    public void run() {
                        position.tick(tickData);
                    }
                });
            }
        }
    }

    public void openPosition(NormalisedPriceInformation tickData, Collection<ISellConditionProvider> templateSellConditions, boolean isShortTrade) {
        int price = isShortTrade ? tickData.getBidPrice() : tickData.getBidPrice();

        logger.debug("Assessing " + this.riskFilters.size() + " risk filters");
        for (IRiskFilter riskFilter : this.riskFilters) {
            if (!riskFilter.proceedWithBuy(price, isShortTrade)) {
                logger.debug("Cancelling proposed buy due to risk filters");
                return;
            }
        }

        Position position = new Position();
        position.setPositionsManager(this);

        this.positions.add(position);
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

        this.trader.openPosition(position, isShortTrade);
        logger.debug("Position Status: " + position.getStatus().toString());

    }


    public void sellPosition(Position position, NormalisedPriceInformation tickData, boolean isShortTradeCondition) {

        int targetPrice = isShortTradeCondition ? tickData.getAskPrice() : tickData.getBidPrice();

        logger.info("Selling position " + position.getUniqueId() + " opened at " + position.getActualOpenPrice() + " for " + targetPrice);


        Calendar cal = GregorianCalendar.getInstance();
        if (isReplayMode) {
            cal.setTimeInMillis(tickData.getCorrectedTimestamp());
        }
        position.setTimeClosed(cal);

        position.setStatus(PositionStatus.SELLING);
        position.setTargetSellPrice(targetPrice);
        position.setActualSellPrice(targetPrice);
        this.trader.closePosition(position, tickData.getCorrectedTimestamp());
    }

    public void printStats() {

        logger.debug(String.format("%-7s%-14s%-7s%-7s%-14s%-7s%-7s%-7s\n", new String[]{"ID", "TO", "TOP", "AOP", "TC", "TCP", "ACP", "Q"}));
        System.out.format("%-7s%-14s%-7s%-7s%-14s%-7s%-7s%-7s\n", new String[]{"ID", "TO", "TOP", "AOP", "TC", "TCP", "ACP", "Q"});

        Iterator izzy = positions.iterator();
        while(izzy.hasNext()) {
            Position position = (Position)izzy.next();
            logger.debug(String.format("%-7d%-14d%-7d%-7d%-14d%-7d%-7d%-7d\n",position.getUniqueId(), position.getTimeOpened().getTimeInMillis()/1000, position.getTargetOpenPrice(), position.getActualOpenPrice(), (position.getTimeClosed() !=null) ? position.getTimeClosed().getTimeInMillis()/1000 : 0, position.getTargetSellPrice(), position.getActualSellPrice(), position.getQuantity()));
            System.out.format("%-7d%-14d%-7d%-7d%-14d%-7d%-7d%-7d\n", position.getUniqueId(), position.getTimeOpened().getTimeInMillis()/1000, position.getTargetOpenPrice(), position.getActualOpenPrice(), (position.getTimeClosed() !=null) ? position.getTimeClosed().getTimeInMillis()/1000 : 0, position.getTargetSellPrice(), position.getActualSellPrice(), position.getQuantity());

        }

        int totalTrades = (int)positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).count();

        logger.info("Total closed trades: " + totalTrades);
        System.out.println("Total closed trades: " + totalTrades);
        logger.info("% closed at profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalTrades * 100);
        System.out.println("% closed at profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalTrades * 100);

        logger.info("Average Profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        System.out.println("Average Profit: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        logger.info("Average Loss: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());
        System.out.println("Average Loss: " + positions.stream().filter(p->p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());

        logger.info("Total Gains: " + positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum());
        System.out.println("Total Gains: " + positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum());
    }

    public void dumpToCsv(String filename) {
        try {
            List<String> lines = this.positions.stream().map(p -> {
                return String.format("%s,%s,%s,%s,%s", p.getUniqueId(), p.getTimeOpened(), p.getActualOpenPrice(), p.getTimeClosed(), p.getActualSellPrice());
            }).collect(Collectors.toList());
            Path file = Paths.get(filename);
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.debug(ex.getMessage(), ex);
        }
    }

    public int getTotalGains() {
        return positions.stream().filter(p -> p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum();
    }

    public void addExistingPosition(Position p) {
        p.setPositionsManager(this);
        this.positions.add(p);
    }
}

package com.futurewebdynamics.trader.positions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.postanalysers.IPostAnalyser;
import com.futurewebdynamics.trader.riskfilters.IRiskFilter;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
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
    private IPostAnalyser postAnalyser;
    private NormalisedPriceInformation currentTickData;

    final static Logger logger = Logger.getLogger(PositionsManager.class);

    public PositionsManager(boolean isReplayMode, IPostAnalyser analyser) {
        positions = Collections.synchronizedList(new ArrayList<Position>());
        riskFilters = Collections.synchronizedList(new ArrayList<IRiskFilter>());
        executor = Executors.newCachedThreadPool();
        this.isReplayMode = isReplayMode;
        this.postAnalyser = analyser;
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
        currentTickData = tickData;
        if (tickData.isEmpty()) {
            logger.trace("tick data is empty");
            return;
        }

        int liveBalance = 0;

        for (int i = 0; i < this.positions.size(); i++)
        {
            Position position = this.positions.get(i);
            if (position.getStatus() == PositionStatus.OPEN) {
                //this.executor.execute(new Runnable() {
                //    public void run() {

                //    }
                //});

                position.tick(tickData);

                if (position.isShortTrade()) {
                    liveBalance += (position.getActualOpenPrice() - tickData.getAskPrice()) * position.getUnits() * position.getLeverage();
                } else {
                    liveBalance += (tickData.getBidPrice() - position.getActualOpenPrice()) * position.getUnits() * position.getLeverage();
                }
            }
        }

    }

    public int getBalanceOfOpenTrades() {
        int balanceOfOpenTradesLong  = (int)positions.stream().filter(p->!p.isShortTrade()).mapToInt(p->currentTickData.getBidPrice() - p.getActualOpenPrice()).sum();
        int balanceOfOpenTradesShort  = (int)positions.stream().filter(p->p.isShortTrade()).mapToInt(p->p.getActualOpenPrice() - currentTickData.getAskPrice()).sum();

        return balanceOfOpenTradesLong + balanceOfOpenTradesShort;
    }

    public void openPosition(NormalisedPriceInformation tickData, Collection<ISellConditionProvider> templateSellConditions, boolean isShortTrade) {
        int price = isShortTrade ? tickData.getBidPrice() : tickData.getAskPrice();

        logger.debug("Assessing " + this.riskFilters.size() + " risk filters");
        for (IRiskFilter riskFilter : this.riskFilters) {
            if (isReplayMode) riskFilter.setTestTimeMs(tickData.getTimestamp());
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
            logger.info("Purchase was not successful");
        }
    }


    public void sellPosition(Position position, NormalisedPriceInformation tickData) {

        position.setStatus(PositionStatus.SELLING);

        int targetPrice = position.isShortTrade() ? tickData.getAskPrice() : tickData.getBidPrice();

        logger.info("Selling " + (position.isShortTrade() ? "short" : "long") + " position " + position.getUniqueId() + " opened at " + position.getActualOpenPrice() + " for " + targetPrice);

        Calendar cal = GregorianCalendar.getInstance();
        if (isReplayMode) {
            cal.setTimeInMillis(tickData.getCorrectedTimestamp());
        }
        position.setTimeClosed(cal);

        position.setTargetSellPrice(targetPrice);
        if (!this.trader.closePosition(position, tickData.getCorrectedTimestamp(), targetPrice)) {
            //closing the trade failed
            position.setStatus(PositionStatus.OPEN);
            position.setTargetSellPrice(0);
        } else {
            if (this.postAnalyser != null) {
                this.postAnalyser.AnalysePosition(position);
            }
        }
    }

    public void printStats(String resultsFilename)
    {
        logger.debug(String.format("%-7s%-14s%-7s%-7s%-14s%-7s%-7s%-7s\n", new String[]{"ID", "TO", "TOP", "AOP", "TC", "TCP", "ACP", "Q"}));

        Iterator izzy = positions.iterator();
        while(izzy.hasNext()) {
            Position position = (Position)izzy.next();
            logger.debug(String.format("%-7d%-14d%-7d%-7d%-14d%-7d%-7d%-7d\n",position.getUniqueId(), position.getTimeOpened().getTimeInMillis()/1000, position.getTargetOpenPrice(), position.getActualOpenPrice(), (position.getTimeClosed() !=null) ? position.getTimeClosed().getTimeInMillis()/1000 : 0, position.getTargetSellPrice(), position.getActualSellPrice(), position.getQuantity()));
        }

        List<String> lines = new ArrayList<String>();

        int totalLongTrades = (int)positions.stream().filter(p -> !p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).count();
        int totalShortTrades = (int)positions.stream().filter(p -> p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).count();

        logger.info("Total LONG closed trades: " + totalLongTrades);
        lines.add("Total LONG closed trades: " + totalLongTrades);
        logger.info("% LONG closed at profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalLongTrades * 100);
        lines.add("% LONG closed at profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).count() / (double)totalLongTrades * 100);

        logger.info("Average Long Profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        lines.add("Average Long Profit: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        logger.info("Average Long Loss: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());
        lines.add("Average Long Loss: " + positions.stream().filter(p->!p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());

        double totalLongGains = positions.stream().filter(p -> !p.isShortTrade() && p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum();
        logger.info("Total LONG Gains: " + totalLongGains);
        lines.add("Total LONG Gains: " + totalLongGains);

        OptionalDouble averageMsOpenLong = positions.stream().filter(p->!p.isShortTrade() && p.getTimeClosed() != null).mapToLong(p->p.getTimeClosed().getTimeInMillis() - p.getTimeOpened().getTimeInMillis()).average();
        if (averageMsOpenLong.isPresent()) logger.info("Average length of long trade " + averageMsOpenLong.getAsDouble()/1000 + "s");

        logger.info("Total SHORT closed trades: " + totalShortTrades);
        lines.add("Total SHORT closed trades: " + totalShortTrades);
        logger.info("% SHORT closed at profit: " + positions.stream().filter(p->p.isShortTrade() &&  p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).count() / (double)totalShortTrades * 100);
        lines.add("% SHORT closed at profit: " + positions.stream().filter(p->p.isShortTrade() &&  p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).count() / (double)totalShortTrades * 100);
        logger.info("Average Short Profit: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());
        lines.add("Average Short Profit: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() < p.getActualOpenPrice()).mapToInt(p->p.getActualOpenPrice() - p.getActualSellPrice()).average());
        logger.info("Average Short Loss: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());
        lines.add("Average Short Loss: " + positions.stream().filter(p->p.isShortTrade() && p.getStatus()==PositionStatus.CLOSED && p.getActualSellPrice() > p.getActualOpenPrice()).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).average());

        double totalShortGains = positions.stream().filter(p -> p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).mapToInt(p->(p.getActualOpenPrice() - p.getActualSellPrice())).sum();
        logger.info("Total SHORT Gains: " + totalShortGains);
        lines.add("Total SHORT Gains: " + totalShortGains);

        OptionalDouble averageMsOpenShort = positions.stream().filter(p->p.isShortTrade() && p.getTimeClosed() != null).mapToLong(p->p.getTimeClosed().getTimeInMillis() - p.getTimeOpened().getTimeInMillis()).average();
        if (averageMsOpenShort.isPresent()) {
            logger.info("Average length of short trade " + averageMsOpenShort + "s");
            lines.add("Average length of short trade " + averageMsOpenShort + "s");
        }

        logger.info("TOTAL GAINS: " + (totalLongGains + totalShortGains));
        lines.add("TOTAL GAINS: " + (totalLongGains + totalShortGains));

        if (resultsFilename != null) {
            Path file = Paths.get(resultsFilename);
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("An error occurred writing out results.", e);
            }
        }
    }

    public void dumpToCsv(String filename) {

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            List<String> lines = new ArrayList<String>();
            lines.add("id, type, timeOpened, timeOpened, openPricePence, timeClosed, timeClosed, closePricePence, units, leverage, profitUsCents");
            for (Position p: this.positions) {

                int profit = p.getStatus() == PositionStatus.CLOSED ?  p.isShortTrade() ? (p.getActualOpenPrice() - p.getActualSellPrice()) * p.getUnits() * p.getLeverage() : (p.getActualSellPrice() - p.getActualOpenPrice()) * p.getUnits() * p.getLeverage() : 0;

                String opened = p.getTimeOpened() !=null ? format1.format(p.getTimeOpened().getTime()) : "";
                String closed = p.getTimeClosed() != null ? format1.format(p.getTimeClosed().getTime()) : "";

                lines.add(String.format("%d,%s,%d,%s,%d,%d,%s,%d,%d,%d,%d", p.getUniqueId(), p.isShortTrade() ? "SHORT" : "LONG", p.getTimeOpened().getTimeInMillis(), opened, p.getActualOpenPrice(), p.getTimeClosed() == null ? 0 : p.getTimeClosed().getTimeInMillis(), closed, p.getActualSellPrice(), p.getUnits(), p.getLeverage(), profit));
            }

            Path file = Paths.get(filename);
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public int getTotalGains() {
        int totalShortGains = positions.stream().filter(p -> p.isShortTrade() &&  p.getStatus() == PositionStatus.CLOSED).mapToInt(p->(p.getActualOpenPrice() - p.getActualSellPrice())).sum();

        int totalLongGains = positions.stream().filter(p -> !p.isShortTrade() && p.getStatus() == PositionStatus.CLOSED).mapToInt(p->p.getActualSellPrice() - p.getActualOpenPrice()).sum();

        return totalLongGains + totalShortGains;
    }

    public void addExistingPosition(Position p) {
        p.setPositionsManager(this);
        this.positions.add(p);
    }

    public void runPostAnalysersForOpenTrades() {
        for (Position p: this.positions) {
            if (p.getStatus() == PositionStatus.OPEN) {
                postAnalyser.AnalysePosition(p);
            }
        }
    }

    public long getOpenTradesCount() {
        return positions.stream().filter(p -> p.getStatus() == PositionStatus.OPEN).count();
    }

    public List<Position> getPositions() {
        return this.positions;
    }
}

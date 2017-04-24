package com.futurewebdynamics.trader.runner;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.AnalyserRegistry;
import com.futurewebdynamics.trader.common.DataWindowRegistry;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.OandaLiveDataSource;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.riskfilters.providers.TimeSinceLastBuy;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.sellconditions.providers.StopLossPercentage;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfitPercentage;
import com.futurewebdynamics.trader.statistics.providers.IsFalling;
import com.futurewebdynamics.trader.trader.providers.OandaTrader;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by 52con on 14/04/2016.
 */
public class Trader {

    final static Logger logger = Logger.getLogger(Trader.class);

    public static void main(String args[]) {

        Properties prop = new Properties();

        try {
            InputStream input = null;

            input = new FileInputStream(args[0]);

            prop.load(input);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        OandaTrader trader = new OandaTrader(100,100);

        try {


            boolean enableShortTrade = Integer.parseInt(prop.getProperty("enableShortTrade")) == 1;
            boolean enableLongTrade = Integer.parseInt(prop.getProperty("enableLongTrade")) == 1;

            trader.setToken(prop.getProperty("token"));
            trader.init();

            int pollIntervalMs = Integer.parseInt(prop.getProperty("pollintervalms"));
            IDataSource dataSource = new OandaLiveDataSource(trader.getAccountId(), trader.getToken(), pollIntervalMs);
            dataSource.init(args[0]);

            DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

            PositionsManager positionsManager = new PositionsManager(false);
            positionsManager.riskFilters.add(new TimeSinceLastBuy(positionsManager,Long.parseLong(prop.getProperty("timesincelastbuyms"))));
            //positionsManager.riskFilters.add(new LowerBuyLimit(Integer.parseInt(prop.getProperty("lowerbuylimit")), MatchTradeEnum.LONG_AND_SHORT));
            //positionsManager.riskFilters.add(new UpperBuyLimit(Integer.parseInt(prop.getProperty("upperbuylimit")), MatchTradeEnum.LONG_AND_SHORT));

            LinkedList<ISellConditionProvider> sellConditions = new LinkedList<ISellConditionProvider>();
            AnalyserRegistry analysers = new AnalyserRegistry();
            int windowSize = Integer.parseInt(prop.getProperty("windowsize"));

            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                int requiredSize = analyser.getRequiredDataWindowSize();
                dataWindowRegistry.getWindowOfLength(requiredSize);
            }

            if (enableShortTrade) {
                sellConditions.add(new StopLossPercentage(Double.parseDouble(prop.getProperty("stoplossshort")),true));
                sellConditions.add(new TakeProfitPercentage(Double.parseDouble(prop.getProperty("takeprofitshort")), false, null, true));
                analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSize), windowSize, positionsManager, Double.parseDouble(prop.getProperty("bouncetriggershort")), Integer.parseInt(prop.getProperty("bouncelookback")), sellConditions, true));
            }

            if (enableLongTrade) {
                IsFalling fallingStatistic = new IsFalling(1, PriceType.BID_PRICE);
                fallingStatistic.setDataWindow(dataWindowRegistry.getWindowOfLength(2));

                sellConditions.add(new StopLossPercentage(Double.parseDouble(prop.getProperty("stoploss")),false));
                sellConditions.add(new TakeProfitPercentage(Double.parseDouble(prop.getProperty("takeprofit")), false, fallingStatistic, false));
                analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSize), windowSize, positionsManager, Double.parseDouble(prop.getProperty("bouncetrigger")), Integer.parseInt(prop.getProperty("bouncelookback")), sellConditions, false));
            }

            trader.getPositions(positionsManager, sellConditions);
            positionsManager.printStats(null);
            positionsManager.setTrader(trader);

            int tickSleepMs = Integer.parseInt(prop.getProperty("tickintervalms"));
            int analysisIntervalMs = Integer.parseInt(prop.getProperty("analysisintervalms"));

            (new Thread() {
                public void run() {
                    while (true) {
                        try {
                            NormalisedPriceInformation tickData = dataSource.getTickData();

                            if (tickData == null) {
                                logger.info("Tick data is null");
                                continue;
                            } else {
                                logger.info("Time: " + System.currentTimeMillis() + " Sample Ask Price: " + tickData.getAskPrice() + " Sample Bid Price: " + tickData.getBidPrice());
                            }

                            dataWindowRegistry.tick(tickData);

                            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                                analyser.tick(tickData);
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            System.exit(1);
                        }

                        try {
                            Thread.currentThread().sleep(analysisIntervalMs);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


            (new Thread() {
                public void run() {

                    while (true) {

                        try {
                            NormalisedPriceInformation tickData = dataSource.getTickData();

                            positionsManager.tick(tickData);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                            System.exit(1);
                        }

                        try {
                            Thread.currentThread().sleep(tickSleepMs);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();

        } catch (Exception ex) {

            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
        }
    }
}

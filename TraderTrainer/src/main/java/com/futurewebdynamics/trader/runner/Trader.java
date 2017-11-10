package com.futurewebdynamics.trader.runner;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.AnalyserRegistry;
import com.futurewebdynamics.trader.common.DataWindowRegistry;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.OandaLiveDataSource;
import com.futurewebdynamics.trader.notifications.EmailNotifier;
import com.futurewebdynamics.trader.notifications.Notification;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.riskfilters.providers.NumberOfOpenTrades;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by 52con on 14/04/2016.
 */
public class Trader {

    final static Logger logger = Logger.getLogger(Trader.class);

    public static void main(String args[]) {

        Properties prop = new Properties();


        String smtpServer = prop.getProperty("smtpServer");
        int smtpPort = Integer.parseInt(prop.getProperty("smtpPort"));
        String smtpUsername = prop.getProperty("smtpUsername");
        String smtpPassword = prop.getProperty("smtpPassword");

        EmailNotifier emailNotifier = new EmailNotifier(smtpServer, smtpPort, smtpUsername, smtpPassword);

        emailNotifier.setFromEmailAddress("charlie@asqcomputing.co.uk");
        emailNotifier.setToEmailAddress("charlie@asqcomputing.co.uk");


        try {
            InputStream input = null;

            input = new FileInputStream(args[0]);

            prop.load(input);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }

        boolean isProd = Boolean.parseBoolean(prop.getProperty("production"));

        BlockingQueue<Notification> notificationsQueue = new LinkedBlockingDeque<>() ;

        (new Thread() {
            public void run() {
                while (true) {
                    for ( Notification n : notificationsQueue) {

                    }
                }
            }
        }).start();

        OandaTrader trader = new OandaTrader(100,100, isProd, notificationsQueue);

        try {

            String instrument = prop.getProperty("instrument");

            String takeProfitOptions = prop.getProperty("takeprofit");
            String takeProfitOptionsDelays = prop.getProperty("takeprofitdelays");
            String takeProfitOptionsShort = prop.getProperty("takeprofitshort");
            String takeProfitOptionsShortDelays = prop.getProperty("takeprofitshortdelays");


            String[] takeProfitTokens = takeProfitOptions.split(",");
            String[] takeProfitTokensDelays = takeProfitOptionsDelays.split(",");

            String[] takeProfitTokensShort = takeProfitOptionsShort.split(",");
            String[] takeProfitTokensShortDelays = takeProfitOptionsShortDelays.split(",");

            boolean enableShortTrade = Integer.parseInt(prop.getProperty("enableShortTrade")) == 1;
            boolean enableLongTrade = Integer.parseInt(prop.getProperty("enableLongTrade")) == 1;

            trader.setToken(prop.getProperty("token"));
            trader.init();

            int pollIntervalMs = Integer.parseInt(prop.getProperty("pollintervalms"));
            IDataSource dataSource = new OandaLiveDataSource(trader.getAccountId(), trader.getToken(), pollIntervalMs, isProd, instrument);
            dataSource.init(args[0]);

            DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

            PositionsManager positionsManager = new PositionsManager(false, null);
            positionsManager.riskFilters.add(new TimeSinceLastBuy(positionsManager,Long.parseLong(prop.getProperty("timesincelastbuyms"))));
            positionsManager.riskFilters.add(new NumberOfOpenTrades(positionsManager,Integer.parseInt(prop.getProperty("maxopentrades"))));
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

                for (int i = 0; i < takeProfitTokensShortDelays.length; i++)
                {
                    sellConditions.add(new TakeProfitPercentage(Double.parseDouble(takeProfitTokensShort[i]), Integer.parseInt(takeProfitTokensShortDelays[i]), false, null, true));
                }
                analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSize), windowSize, positionsManager, Double.parseDouble(prop.getProperty("bouncetriggershort")), Integer.parseInt(prop.getProperty("bouncelookback")), sellConditions, true));
            }

            if (enableLongTrade) {
                IsFalling fallingStatistic = new IsFalling(1, PriceType.BID_PRICE);
                fallingStatistic.setDataWindow(dataWindowRegistry.getWindowOfLength(2));

                sellConditions.add(new StopLossPercentage(Double.parseDouble(prop.getProperty("stoploss")),false));

                for (int i = 0; i < takeProfitTokensDelays.length; i++)
                {
                    logger.info("Take profit @ " + takeProfitTokens[i] + " after delay of " + takeProfitTokensDelays[i]);
                    sellConditions.add(new TakeProfitPercentage(Double.parseDouble(takeProfitTokens[i]), Integer.parseInt(takeProfitTokensDelays[i]), false, fallingStatistic, true));
                }
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

                            if (tickData !=null) {
                                positionsManager.tick(tickData);
                            }

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

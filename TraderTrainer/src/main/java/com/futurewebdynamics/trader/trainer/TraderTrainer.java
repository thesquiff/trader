package com.futurewebdynamics.trader.trainer;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.AnalyserRegistry;
import com.futurewebdynamics.trader.common.DataWindowRegistry;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.ReplayDataSource;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.sellconditions.providers.StopLossPercentage;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfitPercentage;
import com.futurewebdynamics.trader.trader.providers.PseudoTrader;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    final static Logger logger = Logger.getLogger(TraderTrainer.class);

    public static void main(String args[]) {


        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream(args[0]);
            prop.load(input);
        } catch (FileNotFoundException e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(1);
        }


        long dateStartTimestampMs = Long.parseLong(prop.getProperty("starttimestampms"));
        long dateEndTimestampMs = Long.parseLong(prop.getProperty("endtimestampms"));

        String dbDriver = prop.getProperty("dbdriver");
        String connectionString = prop.getProperty("dbconnectionstring");

        logger.info("loading db driver");
        try {
            Class.forName(dbDriver).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        int leverage = 100;
        PseudoTrader trader = new PseudoTrader(leverage);
        IDataSource dataSource = new ReplayDataSource(500, dateStartTimestampMs, dateEndTimestampMs);
        try {
            dataSource.init(connectionString);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        int analysisIntervalMs = 500;
        int tickSleepMs = 500;
        double bounceTrigger = 0.08;
        int bounceLookback = 80;
        double takeProfit = 0.1;
        double takeProfitShort = 0.1;
        double stopLoss = 10.0;
        double stopLossShort = 10.0;
        int upperBuyLimit = 0;
        int lowerBuyLimit = 0;
        long timeSinceLastBuyLimit = 0;
        int windowSize = 81;

        try {
            //for (int windowSizeSweep = 4; windowSizeSweep < 10; windowSizeSweep++) {

            //for (double triggerPercentage = 0.1; triggerPercentage < 3.0; triggerPercentage+=0.1) {

            ((ReplayDataSource) dataSource).reset();

            DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

            PositionsManager positionsManager = new PositionsManager(true);
            //positionsManager.riskFilters.add(new TimeSinceLastBuy(positionsManager,timeSinceLastBuyLimit));
            //positionsManager.riskFilters.add(new LowerBuyLimit(lowerBuyLimit, MatchTradeEnum.LONG_AND_SHORT));
            //positionsManager.riskFilters.add(new UpperBuyLimit(upperBuyLimit, MatchTradeEnum.LONG_AND_SHORT));

            LinkedList<ISellConditionProvider> sellConditions = new LinkedList<ISellConditionProvider>();
            sellConditions.add(new StopLossPercentage(stopLossShort, true));
            sellConditions.add(new StopLossPercentage(stopLoss, false));

            //IsFalling fallingStatistic = new IsFalling(1);
            //fallingStatistic.setDataWindow(dataWindowRegistry.getWindowOfLength(2));

            sellConditions.add(new TakeProfitPercentage(takeProfitShort, false, null, true));
            sellConditions.add(new TakeProfitPercentage(takeProfit, false, null, false));

            trader.getPositions(positionsManager, sellConditions);
            positionsManager.printStats();
            positionsManager.setTrader(trader);

            AnalyserRegistry analysers = new AnalyserRegistry();

            analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSize), windowSize, positionsManager, bounceTrigger, bounceLookback, sellConditions, true));
            analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSize), windowSize, positionsManager, bounceTrigger, bounceLookback, sellConditions, false));

            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                int requiredSize = analyser.getRequiredDataWindowSize();
                dataWindowRegistry.getWindowOfLength(requiredSize);
            }

            positionsManager.setTrader(trader);

            NormalisedPriceInformation tickData = null;


            while (((ReplayDataSource) dataSource).hasMoreData()) {
                //read data and evaulate current positions
                for (int adv = 0; adv < analysisIntervalMs / tickSleepMs; adv++) {
                    tickData = dataSource.getTickData();

                    if (tickData == null) {
                        logger.info("Tick data is null");
                        continue;
                    } else {
                        logger.info("Time: " + tickData.getCorrectedTimestamp() + " Sample Ask Price: " + tickData.getAskPrice() + " Sample Bid Price: " + tickData.getBidPrice());
                    }

                    positionsManager.tick(tickData);
                }

                //buy decisions
                dataWindowRegistry.tick(tickData);

                for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                    analyser.tick(tickData);
                }
            }

            positionsManager.printStats();
            positionsManager.dumpToCsv(prop.getProperty("csvfilefolder") + "\\" + System.currentTimeMillis() + ".csv");

            //}

            //}
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }
}

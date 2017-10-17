package com.futurewebdynamics.trader.trainer;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.AnalyserRegistry;
import com.futurewebdynamics.trader.common.DataWindowRegistry;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceType;
import com.futurewebdynamics.trader.datasources.providers.ReplayDataSource;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.postanalysers.providers.PositionLifetimeChart;
import com.futurewebdynamics.trader.riskfilters.providers.NumberOfOpenTrades;
import com.futurewebdynamics.trader.riskfilters.providers.TimeSinceLastBuy;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.sellconditions.providers.StopLossPercentage;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfitPercentage;
import com.futurewebdynamics.trader.statistics.providers.IsFalling;
import com.futurewebdynamics.trader.statistics.providers.IsRising;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.Position;
import com.futurewebdynamics.trader.trader.providers.PseudoTrader;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by Charlie on 06/04/2017.
 */
public class TrainerWorker  implements Callable<TrainerResult>{

    private Logger logger;

    private int iterationNumber;
    private WorkerConfig workerConfig;
    private IterationConfig iterationConfig;
    private ReplayDataSource dataSource;

    private boolean isComplete;
    private int totalGains;
    private List<Position> finalPositions;

    public TrainerWorker (int iterationNumber, WorkerConfig workerConfig, IterationConfig iterationConfig, ReplayDataSource dataSource) {

        this.iterationNumber = iterationNumber;
        this.workerConfig = workerConfig;
        this.iterationConfig = iterationConfig;
        this.dataSource = dataSource;

        logger = Logger.getLogger("Thread" + Thread.currentThread().getName());
        Properties props=new Properties();
        props.setProperty("log4j.appender.file","org.apache.log4j.RollingFileAppender");
        props.setProperty("log4j.appender.file.maxFileSize","100MB");
        props.setProperty("log4j.appender.file.maxBackupIndex","100");
        props.setProperty("log4j.appender.file.File","log4j-TraderTrainer.log"+Thread.currentThread().getName()+".log");
        props.setProperty("log4j.appender.file.threshold","info");
        props.setProperty("log4j.appender.file.layout","org.apache.log4j.PatternLayout");
        //props.setProperty("log4j.appender.file.layout.ConversionPattern","%d [%t] %-5p [%-35F : %-25M : %-6L] %-C -%m%n");
        props.setProperty("log4j.appender.file.layout.ConversionPattern","%d{yyyy-MM-dd HH:mm:ss} [%t] %-5p %c{1}:%L - %m%n");
        props.setProperty("log4j.appender.stdout","org.apache.log4j.ConsoleAppender");
        props.setProperty("log4j.logger."+"Thread" + Thread.currentThread().getName(),"INFO, file");

        PropertyConfigurator.configure(props);
        logger.info("thread started :"+Thread.currentThread().getName());
        logger.debug("run method :"+Thread.currentThread().getName());
    }

    @Override
    public TrainerResult call() {
        isComplete = false;
        System.out.println(Thread.currentThread().getName()+" Start. iterationNumber = " + iterationNumber);
        logger.info(Thread.currentThread().getName()+" Start. iterationNumber = " + iterationNumber);
        executeTrainingRun();
        logger.info(Thread.currentThread().getName()+" End. iterationNumber = " + iterationNumber);

        TrainerResult result = new TrainerResult();
        result.setTotalGains(this.totalGains);
        result.setIterationCounter(this.iterationNumber);
        result.setComplete(isComplete);

        return result;
    }

    public void executeTrainingRun() {

        String outputFolder = workerConfig.getMasterOutputFolder() + File.separator + String.valueOf(iterationNumber);

        new File(outputFolder).mkdir();

        boolean createTickerFile = false;
        if (workerConfig.isCreateTickerFile()) {
            //redirect ticker to file
            try {
                System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFolder + File.separator + "ticker.csv"))));
                createTickerFile = true;
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }

        try {
            PrintWriter writer = new PrintWriter(outputFolder + File.separator + "metadata.txt", "UTF-8");
            writer.println("analysisIntervalMs=" + workerConfig.getAnalysisIntervalMs());
            writer.println("tickSleepMs=" + workerConfig.getTickSleepMs());
            writer.println("bounceTriggerStart=" + workerConfig.getBounceLookbackStart());
            writer.println("bounceTriggerEnd=" + workerConfig.getBounceTriggerEnd());
            writer.println("bounceTrigger=" + iterationConfig.getBounceTrigger());
            writer.println("bounceLookbackStart=" + workerConfig.getBounceLookbackStart());
            writer.println("bounceLookbackEnd=" + workerConfig.getBounceLookbackEnd());
            writer.println("bounceLookback=" + iterationConfig.getBounceLookback());
            writer.println("bounceLookbackStep=" + workerConfig.getBounceLookbackStep());
            writer.println("takeProfitStart=" + workerConfig.getTakeProfitStart());
            writer.println("takeProfitEnd=" + workerConfig.getTakeProfitEnd());
            writer.println("takeProfit=" + iterationConfig.getTakeProfit());
            //writer.println("takeProfitShort=" + takeProfitShort);
            writer.println("stopLossStart=" + workerConfig.getStopLossStart());
            writer.println("stopLossEnd=" + workerConfig.getStopLossEnd());
            writer.println("stopLoss=" + iterationConfig.getStopLoss());
            //writer.println("stopLossShort=" + stopLossShort);
            writer.println("upperBuyLimit=" + workerConfig.getUpperBuyLimit());
            writer.println("lowerBuyLimit=" + workerConfig.getLowerBuyLimit());
            writer.println("timeSinceLastBuyLimitStart=" + workerConfig.getTimeSinceLastBuyLimitStart());
            writer.println("timeSinceLastBuyLimitEnd=" + workerConfig.getTimeSinceLastBuyLimitEnd());
            writer.println("timeSinceLastBuyLimitStep=" + workerConfig.getTimeSinceLastBuyLimitStep());
            writer.println("timeSinceLastBuyLimit=" + iterationConfig.getTimeSinceLastBuyLimit());
            writer.println("windowSize=" + workerConfig.getWindowSize());
            writer.println("enableLongTrade=" + workerConfig.isEnableLongTrade());
            writer.println("enableShortTrade=" + workerConfig.isEnableShortTrade());
            writer.println("maxopentrades=" + workerConfig.getMaxOpenTrades());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        ((ReplayDataSource) dataSource).reset();

        int leverage = 100;
        PseudoTrader trader = new PseudoTrader(leverage, 50000, 100);

        DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

        PositionsManager positionsManager = new PositionsManager(true, new PositionLifetimeChart(dataSource.getDataCache(), 20*60, 500, 0.1, outputFolder, iterationConfig.getTakeProfit(), iterationConfig.getStopLoss()));
        positionsManager.riskFilters.add(new TimeSinceLastBuy(positionsManager, iterationConfig.getTimeSinceLastBuyLimit()));
        positionsManager.riskFilters.add(new NumberOfOpenTrades(positionsManager,workerConfig.getMaxOpenTrades()));

        LinkedList<ISellConditionProvider> sellConditions = new LinkedList<ISellConditionProvider>();
        AnalyserRegistry analysers = new AnalyserRegistry();

        if (workerConfig.isEnableShortTrade()) {
            sellConditions.add(new StopLossPercentage(iterationConfig.getStopLoss(), true));

            IsRising risingStatistic = new IsRising(1, PriceType.ASK_PRICE);
            risingStatistic.setDataWindow(dataWindowRegistry.getWindowOfLength(2));

            sellConditions.add(new TakeProfitPercentage(iterationConfig.getTakeProfit(), 0, false, null, true));

            analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(workerConfig.getWindowSize()), workerConfig.getWindowSize(), positionsManager, iterationConfig.getBounceTrigger(), iterationConfig.getBounceLookback(), sellConditions, true));
        }

        if (workerConfig.isEnableLongTrade()) {
            sellConditions.add(new StopLossPercentage(iterationConfig.getStopLoss(), false));

            IsFalling fallingStatistic = new IsFalling(1, PriceType.BID_PRICE);
            fallingStatistic.setDataWindow(dataWindowRegistry.getWindowOfLength(2));

            sellConditions.add(new TakeProfitPercentage(iterationConfig.getTakeProfit(), 0, false, null, false));

            analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(workerConfig.getWindowSize()), workerConfig.getWindowSize(), positionsManager, iterationConfig.getBounceTrigger(), iterationConfig.getBounceLookback(), sellConditions, false));
        }

        trader.getPositions(positionsManager, sellConditions);
        positionsManager.setTrader(trader);

        for (IAnalyserProvider analyser : analysers.getAnalysers()) {
            int requiredSize = analyser.getRequiredDataWindowSize();
            dataWindowRegistry.getWindowOfLength(requiredSize);
        }

        positionsManager.setTrader(trader);

        NormalisedPriceInformation tickData = null;

        while (((ReplayDataSource) dataSource).hasMoreData()) {
            //read data and evaulate current positions
            for (int adv = 0; adv < workerConfig.getAnalysisIntervalMs() / workerConfig.getTickSleepMs(); adv++) {
                tickData = dataSource.getTickData();

                positionsManager.tick(tickData);

                //output to csv file
                if (createTickerFile)
                    System.out.println(String.join(",", new String[]{String.valueOf(tickData.getCorrectedTimestamp()), String.valueOf(tickData.getAskPrice()), String.valueOf(tickData.getBidPrice()), String.valueOf(trader.getCurrentBalanceOfCompletedTrades()), String.valueOf(positionsManager.getBalanceOfOpenTrades())}));

                if (tickData == null) {
                    logger.debug("Tick data is null");
                    continue;
                } else {
                    logger.debug("Time: " + tickData.getCorrectedTimestamp() + " Sample Ask Price: " + tickData.getAskPrice() + " Sample Bid Price: " + tickData.getBidPrice());
                }
            }

            //buy decisions
            dataWindowRegistry.tick(tickData);

            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                analyser.tick(tickData);
            }
        }

        positionsManager.runPostAnalysersForOpenTrades();
        positionsManager.printStats(outputFolder + File.separator + "results.csv");
        positionsManager.dumpToCsv(outputFolder + File.separator + "activity.csv");


        isComplete = true;
        totalGains = positionsManager.getTotalGains();
        finalPositions = position.getFinalPositions();
    }

    public double getResult() {
        return totalGains;
    }

    public boolean isComplete() {
        return isComplete;
    }


}

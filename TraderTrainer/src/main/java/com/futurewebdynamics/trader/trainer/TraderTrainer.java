package com.futurewebdynamics.trader.trainer;

import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.ReplayDataSource;
import com.futurewebdynamics.trader.notifications.EmailNotifier;
import com.futurewebdynamics.trader.notifications.INotifier;
import com.futurewebdynamics.trader.notifications.Notification;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    final static Logger logger = Logger.getLogger(TraderTrainer.class);

    public static void main(String args[]) {

        Properties prop = new Properties();
        InputStream input = null;

        try {

            System.out.println("props file: " + args[0]);

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

        String smtpServer = prop.getProperty("smtpServer");
        int smtpPort = Integer.parseInt(prop.getProperty("smtpPort"));
        String smtpUsername = prop.getProperty("smtpUsername");
        String smtpPassword = prop.getProperty("smtpPassword");

        INotifier emailNotifier = new EmailNotifier(smtpServer, smtpPort, smtpUsername, smtpPassword);

        long dateStartTimestampMs = Long.parseLong(prop.getProperty("starttimestampms"));
        long dateEndTimestampMs = Long.parseLong(prop.getProperty("endtimestampms"));


        IDataSource dataSource = new ReplayDataSource(500, dateStartTimestampMs, dateEndTimestampMs);
        try {
            if (Integer.valueOf(prop.getProperty("importdata")) == 0) {

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

                dataSource.init(connectionString);
            } else {
                ((ReplayDataSource) dataSource).initFromFile(prop.getProperty("datafile"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        logger.debug("Going to read properties...");

        WorkerConfig workerConfig = new WorkerConfig();

        workerConfig.setAnalysisIntervalMs(Integer.parseInt(prop.getProperty("analysisIntervalMs")));
        workerConfig.setTickSleepMs(Integer.parseInt(prop.getProperty("tickSleepMs")));
        workerConfig.setBounceTriggerStart(Double.parseDouble(prop.getProperty("bounceTriggerStart")));
        workerConfig.setBounceTriggerEnd(Double.parseDouble(prop.getProperty("bounceTriggerEnd")));
        workerConfig.setBounceLookbackStart(Integer.parseInt(prop.getProperty("bounceLookbackStart")));
        workerConfig.setBounceLookbackEnd(Integer.parseInt(prop.getProperty("bounceLookbackEnd")));
        workerConfig.setBounceLookbackStep(Integer.parseInt(prop.getProperty("bounceLookbackStep")));
        workerConfig.setTakeProfitStart(Double.parseDouble(prop.getProperty("takeProfitStart")));
        workerConfig.setTakeProfitEnd(Double.parseDouble(prop.getProperty("takeProfitEnd")));
        //workerConfig.setTakeProfitShort(Double.parseDouble(prop.getProperty("takeProfitShort")));
        workerConfig.setStopLossStart(Double.parseDouble(prop.getProperty("stopLossStart")));
        workerConfig.setStopLossEnd(Double.parseDouble(prop.getProperty("stopLossEnd")));
        //workerConfig.setStopLossShort(Double.parseDouble(prop.getProperty("stopLossShort")));
        workerConfig.setUpperBuyLimit(Integer.parseInt(prop.getProperty("upperBuyLimit")));
        workerConfig.setLowerBuyLimit(Integer.parseInt(prop.getProperty("lowerBuyLimit")));
        workerConfig.setTimeSinceLastBuyLimitStart(Long.parseLong(prop.getProperty("timeSinceLastBuyLimitStart")));
        workerConfig.setTimeSinceLastBuyLimitEnd(Long.parseLong(prop.getProperty("timeSinceLastBuyLimitEnd")));
        workerConfig.setTimeSinceLastBuyLimitStep(Long.parseLong(prop.getProperty("timeSinceLastBuyLimitStep")));
        workerConfig.setWindowSize(Integer.parseInt(prop.getProperty("windowSize")));
        workerConfig.setEnableShortTrade(Integer.parseInt(prop.getProperty("enableShortTrade")) == 1);
        workerConfig.setEnableLongTrade(Integer.parseInt(prop.getProperty("enableLongTrade")) == 1);
        workerConfig.setMaxOpenTrades(Integer.parseInt(prop.getProperty("maxOpenTrades")));
        workerConfig.setTakeProfitDelays(prop.getProperty("takeProfitDelays"));

        boolean createTickerFile = false;

        int iterationCounter = 0;

        logger.debug("Going to create master output folder...");

        String masterOutputFolder = prop.getProperty("csvfilefolder") + File.separator + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        new File(masterOutputFolder).mkdir();
        workerConfig.setMasterOutputFolder(masterOutputFolder);

        workerConfig.setCreateTickerFile(Integer.valueOf(prop.getProperty("createtickerfile")) == 1);

        //if required, export data set to csv file
        if (Integer.valueOf(prop.getProperty("exportdata")) == 1) {

            logger.debug("Going to export data");

            try {
                PrintWriter writer = new PrintWriter(masterOutputFolder + File.separator + "data.csv", "UTF-8");

                ((ReplayDataSource)dataSource).dumpData(writer);
                writer.flush();
                writer.close();

            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        try {

            logger.debug("Starting sweep...");

            Collection<Future<TrainerWorkerResult>> results = new ArrayList<Future<TrainerWorkerResult>>();
            ExecutorService workers = Executors.newFixedThreadPool(16);


            for (long timeSinceLastBuyLimit = workerConfig.getTimeSinceLastBuyLimitStart(); timeSinceLastBuyLimit <= workerConfig.getTimeSinceLastBuyLimitEnd(); timeSinceLastBuyLimit+=workerConfig.getTimeSinceLastBuyLimitStep()) {

                logger.info("timeSinceLastBuyLimit=" + timeSinceLastBuyLimit);

                for (double takeProfit = workerConfig.getTakeProfitStart(); takeProfit <= workerConfig.getTakeProfitEnd(); takeProfit += 0.1) {

                    logger.info("takeProfit=" + takeProfit);

                    for (double stopLoss = workerConfig.getStopLossStart(); stopLoss <= workerConfig.getStopLossEnd(); stopLoss += 0.1) {

                        logger.info("stopLoss=" + stopLoss);

                        for (int bounceLookback = workerConfig.getBounceLookbackStart(); bounceLookback <= workerConfig.getBounceLookbackEnd(); bounceLookback += workerConfig.getBounceLookbackStep()) {

                            logger.info("bounceLookback=" + bounceLookback);

                            for (double bounceTrigger = workerConfig.getBounceTriggerStart(); bounceTrigger <= workerConfig.getBounceTriggerEnd(); bounceTrigger += 0.05) {

                                logger.info("bounceTrigger=" + bounceTrigger);

                                logger.info("Iteration counter is " + iterationCounter);

                                IterationConfig iterationConfig = new IterationConfig();
                                iterationConfig.setTimeSinceLastBuyLimit(timeSinceLastBuyLimit);
                                iterationConfig.setTakeProfit(takeProfit + "," + takeProfit*0.5 + "," + "0.01");
                                iterationConfig.setTakeProfitDelays(workerConfig.getTakeProfitDelays());
                                iterationConfig.setStopLoss(stopLoss);
                                iterationConfig.setBounceLookback(bounceLookback);
                                iterationConfig.setBounceTrigger(bounceTrigger);

                                Callable<TrainerWorkerResult> worker = new TrainerWorker(iterationCounter, workerConfig, iterationConfig, new ReplayDataSource((ReplayDataSource)dataSource));

                                Future<TrainerWorkerResult> result = workers.submit(worker);

                                results.add(result);

                                iterationCounter++;
                            }
                        }
                    }
                }
            }

            int bestTotalGains = 0;
            int bestIterationNumber = 0;

            logger.info("Size of results set " + results.size());

            List<String> lines = new ArrayList<String>();
            lines.add("i,openedTrades,closedTrades,closedTradesLong,closedTradesShort,unclosedTrades,profitLoss,profitLossLong,profitLossShort,balanceOfOpenTrades,avProfitLong,avProfitShort,avLossLong,avLossShort,avLength,avLengthLong,avLengthShort");

            for (Future<TrainerWorkerResult> f : results) {
                logger.info("Going to get result for iteration");
                TrainerWorkerResult result = f.get();

                int gains = result.realisedProfitLoss;

                logger.info("Result for iteration " + result.iteration + " is " + gains + ". Complete? " + String.valueOf(result.isComplete));
                if (gains > bestTotalGains) {
                    bestTotalGains = gains;
                    bestIterationNumber = result.iteration;
                }

                lines.add(Integer.toString(result.iteration) + "," +
                        Integer.toString(result.totalNumberOfOpenedTrades) + "," +
                        Integer.toString(result.numberOfClosedTrades) + "," +
                        Integer.toString(result.numberOfClosedTradesLong) + "," +
                        Integer.toString(result.numberOfClosedTradesShort) + "," +
                        Integer.toString(result.numberOfUnclosedTrades) + "," +
                        Integer.toString(result.realisedProfitLoss) + "," +
                        Integer.toString(result.realisedProfitLossLong) + "," +
                        Integer.toString(result.realisedProfitLossShort) + "," +
                        Double.toString(result.balanceOfOpenTrades) + "," +
                        Double.toString(result.averageProfitLong) + "," +
                        Double.toString(result.averageProfitShort) + "," +
                        Double.toString(result.averageLossLong) + "," +
                        Double.toString(result.averageLossShort) + "," +
                        Double.toString(result.averageLengthOfTrade) + "," +
                        Double.toString(result.averageLengthOfTradeLong) + "," +
                        Double.toString(result.averageLengthOfTradeShort));
            }

            logger.info("Best total gains: " + bestTotalGains);
            logger.info("Path to best total gains: " + bestIterationNumber);

            logger.info("Writing iteration file");

            Path file = Paths.get(masterOutputFolder + File.separator + "iterations.csv");
            try {
                Files.write(file, lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("An error occurred writing out results.", e);
            }

            ((EmailNotifier)emailNotifier).setFromEmailAddress("charlie@asqcomputing.co.uk");
            ((EmailNotifier)emailNotifier).setToEmailAddress("charlie@asqcomputing.co.uk");

            emailNotifier.SendNotification(new Notification("Training Run Complete", "Best gains " + bestTotalGains + " @ iteration " + bestIterationNumber));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        System.exit(0);

    }
}

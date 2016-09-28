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

import java.util.LinkedList;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    final static Logger logger = Logger.getLogger(TraderTrainer.class);

    public static void main(String args[]) {

        //EToroTrader trader = new EToroTrader(args[0]);
        //trader.login();


        PseudoTrader trader = new PseudoTrader();
        IDataSource dataSource = new ReplayDataSource();
        dataSource.init(args[0]);

        int largestGain =0;
        int bestWindowSize = 0;
        double bestTrigger = 0.0;

        for (int windowSizeSweep = 4; windowSizeSweep < 10; windowSizeSweep++) {

            for (double triggerPercentage = 0.1; triggerPercentage < 3.0; triggerPercentage+=0.1) {

                ((ReplayDataSource)dataSource).reset();

                DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

                PositionsManager positionsManager = new PositionsManager();

                positionsManager.setTrader(trader);

                AnalyserRegistry analysers = new AnalyserRegistry();

                LinkedList<ISellConditionProvider> sellConditions = new LinkedList<ISellConditionProvider>();
                sellConditions.add(new StopLossPercentage(10.0));
                sellConditions.add(new TakeProfitPercentage(3.3,false, null));

                analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(windowSizeSweep), windowSizeSweep, positionsManager, triggerPercentage,2,sellConditions));

                for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                    int requiredSize = analyser.getRequiredDataWindowSize();
                    dataWindowRegistry.getWindowOfLength(requiredSize);
                }

                while(true) {
                    NormalisedPriceInformation tickData = dataSource.getTickData();

                    if (tickData == null) break;

                    dataWindowRegistry.tick(tickData);

                    for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                        analyser.tick(tickData);
                    }

                    positionsManager.tick(tickData);
                }

                //gather some stats
                logger.info("Window Size: " + windowSizeSweep);
                logger.info("Trigger % " + triggerPercentage);
                positionsManager.printStats();

                int gain = positionsManager.getTotalGains();
                if (gain > largestGain) {
                    largestGain = gain;
                    bestWindowSize = windowSizeSweep;
                    bestTrigger = triggerPercentage;
                }


            }


        }

        logger.info("Largest gain: " + largestGain + " (window size: " + bestWindowSize + ", trigger: " + bestTrigger + ")");




    }
}

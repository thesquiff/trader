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

import java.util.LinkedList;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    public static void main(String args[]) {
        IDataSource dataSource = new ReplayDataSource();
        dataSource.init(args[0]);

        DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

        PositionsManager positionsManager = new PositionsManager();

        PseudoTrader trader = new PseudoTrader();
        trader.init(args[0]);

        positionsManager.setTrader(trader);

        AnalyserRegistry analysers = new AnalyserRegistry();

        LinkedList<ISellConditionProvider> sellConditions = new LinkedList<ISellConditionProvider>();
        sellConditions.add(new StopLossPercentage(10.0));
        sellConditions.add(new TakeProfitPercentage(3.0));

        analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(4), 4, positionsManager, 0.1,2,sellConditions));

        for (IAnalyserProvider analyser : analysers.getAnalysers()) {
            int requiredSize = analyser.getRequiredDataWindowSize();
            dataWindowRegistry.getWindowOfLength(requiredSize);
        }

        while(true) {

            /*try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            NormalisedPriceInformation tickData = dataSource.getTickData();

            if (tickData == null) break;

            dataWindowRegistry.tick(tickData);

            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                analyser.tick(tickData);
            }

            positionsManager.tick(tickData);


        }

        //gather some stats

        positionsManager.printStats();

    }
}

package com.futurewebdynamics.trader.trainer;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.*;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.ReplayDataSource;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.trader.providers.PseudoTrader;

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
        analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(4), 4, positionsManager, 0.1,2));

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

    }
}

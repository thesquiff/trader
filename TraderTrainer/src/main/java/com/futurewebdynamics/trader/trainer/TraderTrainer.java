package com.futurewebdynamics.trader.trainer;

import com.futurewebdynamics.trader.analysers.IAnalyserProvider;
import com.futurewebdynamics.trader.analysers.providers.PercentageDropBounce;
import com.futurewebdynamics.trader.common.*;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.datasources.providers.ReplayDataSource;
import com.futurewebdynamics.trader.positions.PositionsManager;
import com.futurewebdynamics.trader.sellconditions.ISellConditionProvider;
import com.futurewebdynamics.trader.sellconditions.providers.TakeProfit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by 52con on 14/04/2016.
 */
public class TraderTrainer {

    public static void main(String args[]) {
        IDataSource dataSource = new ReplayDataSource();
        dataSource.init(args[0]);

        DataWindowRegistry dataWindowRegistry = new DataWindowRegistry();

        AnalyserRegistry analysers = new AnalyserRegistry();
        analysers.addAnalyser(new PercentageDropBounce(dataWindowRegistry.createWindowOfLength(3), 3, 2.0));

        for (IAnalyserProvider analyser : analysers.getAnalysers()) {
            int requiredSize = analyser.getRequiredDataWindowSize();
            dataWindowRegistry.getWindowOfLength(requiredSize);


        }

        PositionsManager positionsManager = new PositionsManager();

        while(true) {

            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NormalisedPriceInformation tickData = dataSource.getTickData();

            dataWindowRegistry.tick(tickData);

            for (IAnalyserProvider analyser : analysers.getAnalysers()) {
                analyser.tick();
            }

            positionsManager.tick(tickData);


        }

    }
}

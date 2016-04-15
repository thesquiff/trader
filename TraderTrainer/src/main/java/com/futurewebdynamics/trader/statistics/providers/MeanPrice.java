package com.futurewebdynamics.trader.statistics.providers;

import com.futurewebdynamics.trader.statistics.IStatisticProvider;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;

import java.util.List;

/**
 * Created by 52con on 14/04/2016.
 */
public class MeanPrice extends IStatisticProvider {

    public MeanPrice() {
        super();
    }

    @Override
    public String getName() {
        return "MinimumPrice";
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public Object getResult() {
        List<NormalisedPriceInformation> data = dataWindow.getData();
        return (int)Math.round(data.stream().mapToInt(p->p.getPrice()).average().getAsDouble());
    }
}

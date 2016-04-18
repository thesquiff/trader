package com.futurewebdynamics.trader.sellconditions;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;

/**
 * Created by 52con on 15/04/2016.
 */
public abstract class ISellConditionProvider  {

    public abstract void tick(NormalisedPriceInformation tickData);


}

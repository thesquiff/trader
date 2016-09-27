package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Charlie on 13/09/2016.
 */
public class Position {

    public String instrument;

    @JsonProperty("long")
    public OandaPosition longPosition;

    @JsonProperty("short")
    public OandaPosition shortPosition;

    public double unrealizedPL;
    public double pl;
    public double resettablePL;

}

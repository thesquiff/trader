
package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class UnitsAvailable {

    @JsonProperty("default")
    public Default _default;
    public OpenOnly openOnly;
    public ReduceFirst reduceFirst;
    public ReduceOnly reduceOnly;

}

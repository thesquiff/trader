package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Charlie on 16/09/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trades {

    public String lastTransactionID;
    public List<Trade> trades;

}

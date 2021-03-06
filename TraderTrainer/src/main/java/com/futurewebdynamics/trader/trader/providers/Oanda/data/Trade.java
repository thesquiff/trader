package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Charlie on 16/09/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    public int currentUnits;
    public double financing;
    public int initialUnits;
    public String instrument;
    public String openTime;
    public double price;
    public double realizedPL;
    public String state;
    public double unrealizedPL;
    public String id;
    public String[] closingTransactionIDs;
    public String closeTime;
    public String averageClosePrice;
}

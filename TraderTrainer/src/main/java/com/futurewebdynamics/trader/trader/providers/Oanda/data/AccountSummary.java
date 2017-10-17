package com.futurewebdynamics.trader.trader.providers.Oanda.data;

/**
 * Created by Charlie on 09/10/2017.
 */
public class AccountSummary {
    public String id;
    public String alias;
    public String currency;
    public String balance;
    public int createdByUserId;
    public String createdTime;
    public String pl;
    public String resettablePL;
    public String resettablePLTime;
    public String commission;
    public String marginRate;
    public String marginCallEnterTime;
    public int marginCallExtensionTime;
    public int marginCallExtensionCount;
    public String lastMarginCallExtensionTime;
    public int openTradeCount;
    public int openPositionCount;
    public int pendingOrderCount;
    public boolean hedgingEnabled;
    public String unrealizedPL;
    public String NAV;
    public String marginUsed;
    public String marginAvailable;
    public String positionValue;
    public String marginCloseoutUnrealizedPL;
    public String marginCloseoutNAV;
    public String marginCloseoutMarginUsed;
    public String marginCloseoutPercent;
    public String marginCloseoutPositionValue;
    public String marginCallMarginUsed;
    public String marginCallPercent;
    public String lastTransactionId;
}
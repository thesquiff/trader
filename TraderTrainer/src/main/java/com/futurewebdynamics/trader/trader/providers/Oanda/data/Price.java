
package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class Price {

    public List<Ask> asks = new ArrayList<Ask>();
    public List<Bid> bids = new ArrayList<Bid>();
    public String closeoutAsk;
    public String closeoutBid;
    public String instrument;
    public QuoteHomeConversionFactors quoteHomeConversionFactors;
    public String status;
    public String time;
    public UnitsAvailable unitsAvailable;
    public String type;

}

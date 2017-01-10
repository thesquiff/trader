
package com.futurewebdynamics.trader.trader.providers.Oanda.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Prices {

    public List<Price> prices = new ArrayList<Price>();

}

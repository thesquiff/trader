package com.futurewebdynamics.trader.datasources.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.RestHelper;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.HistoricalData;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Charlie on 26/09/2016.
 */
public class OandaHistoricalDataSource implements IDataSource {

    final static Logger logger = Logger.getLogger(OandaHistoricalDataSource.class);

    private HistoricalData data;
    private String startDate;
    private String endDate;
    private String granularity;

    private String currentTickDate;

    public OandaHistoricalDataSource(String startDate, String endDate, String granularity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.granularity = granularity;
        this.currentTickDate = startDate;
    }

    @Override
    public NormalisedPriceInformation getTickData() {

        //return data for currentTickDate and the increment
        return null;


    }

    @Override
    public void init(String propertiesFile) {

        try {
            String jsonResult = RestHelper.GetJson("https://api-fxpractice.oanda.com/v1/candles?instrument=BCO_USD&candleFormat=midpoint&granularity=" + granularity + "&start=" + startDate + "&end=" + endDate, "");
            ObjectMapper jsonDeseserialiser = new ObjectMapper();

            this.data = jsonDeseserialiser.readValue(jsonResult, HistoricalData.class);
        } catch (MalformedURLException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
    }


}

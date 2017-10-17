package com.futurewebdynamics.trader.datasources.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceInformation;
import com.futurewebdynamics.trader.common.RestHelper;
import com.futurewebdynamics.trader.datasources.IDataSource;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.Prices;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by Charlie on 12/09/2016.
 */
public class OandaLiveDataSource implements IDataSource {

    final static Logger logger = Logger.getLogger(OandaLiveDataSource.class);

    private String accountId;
    private String token;
    private GetLiveData runner;

    private final static String practiceUrl = "https://api-fxpractice.oanda.com";

    private final static String prodUrl = "https://api-fxtrade.oanda.com";

    public OandaLiveDataSource(String accountId, String token, int pollIntervalMs, boolean production) {
        this.accountId = accountId;
        this.token = token;
        this.runner = new GetLiveData(accountId, token, pollIntervalMs, production ? prodUrl : practiceUrl);
    }

    @Override
    public NormalisedPriceInformation getTickData() {

        PriceInformation latestPrice = runner.getLatestPrice();

        if (latestPrice == null || (System.currentTimeMillis()/1000 - latestPrice.getTimestamp()) > 1000)
            return null;

        NormalisedPriceInformation priceInformation = new NormalisedPriceInformation(latestPrice.getTimestamp(), latestPrice.getAskPrice(),latestPrice.getBidPrice(),latestPrice.getTimestamp());        priceInformation.setCorrectedTimestamp(latestPrice.getTimestamp());

        return priceInformation;

    }

    @Override
    public void init(String propertiesFile) {
        System.out.println("initiasing live data source");
        Thread t = new Thread(this.runner);
        t.start();
    }

    private class GetLiveData implements Runnable {

        private PriceInformation latestPrice;

        private String accountId;
        private String token;
        private int pollIntervalMs;

        private String apiUrl;

        public GetLiveData(String accountId, String token, int pollIntervalMs, String apiUrl) {
            logger.info("accountid: " + accountId);
            logger.info("token: " + token);
            this.accountId = accountId;
            this.token = token;
            this.pollIntervalMs = pollIntervalMs;

            this.apiUrl = apiUrl;
        }

        public PriceInformation getLatestPrice() {
            return this.latestPrice;
        }

        public void run() {
            logger.debug("Starting OandaLiveDataSource");
            while(true) {

                try {

                    Prices prices = null;

                    String jsonResult = RestHelper.GetJson(this.apiUrl + "/v3/accounts/" + this.accountId + "/pricing?instruments=BCO_USD", token);
                    ObjectMapper jsonDeseserialiser = new ObjectMapper();

                    prices = jsonDeseserialiser.readValue(jsonResult, Prices.class);

                    if (prices.prices.size() <=0 || prices.prices.get(0).asks.size() <= 0|| prices.prices.get(0).bids.size() <= 0) {
                        logger.debug("Time: " + Long.toString(System.currentTimeMillis()) + " Price: NULL");
                        latestPrice = null;
                    } else {

                        latestPrice = new PriceInformation((int) (System.currentTimeMillis()/1000), (int)(Double.parseDouble(prices.prices.get(0).asks.get(0).price)*100),(int)(Double.parseDouble(prices.prices.get(0).bids.get(0).price)*100));
                        logger.debug("Time: " + Long.toString(System.currentTimeMillis()/1000) + " AskPrice: " + Integer.toString(latestPrice.getAskPrice()) + " BidPrice:" + Integer.toString(latestPrice.getBidPrice()));
                    }

                    Thread.currentThread().sleep(pollIntervalMs);

                } catch (InterruptedException e) {
                    logger.debug(e.getMessage());
                } catch (MalformedURLException e) {
                    logger.debug(e.getMessage());
                } catch (IOException e) {
                    logger.debug(e.getMessage());
                }
            }
        }
    }
}

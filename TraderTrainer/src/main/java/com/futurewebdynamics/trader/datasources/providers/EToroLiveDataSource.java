package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.PriceInformation;
import com.futurewebdynamics.trader.datasources.IDataSource;
import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by 52con on 15/04/2016.
 */
public class EToroLiveDataSource implements IDataSource {

    final static Logger logger = Logger.getLogger(EToroLiveDataSource.class);

    private PriceInformation latestPrice;


    @Override
    public NormalisedPriceInformation getTickData() {
        if (latestPrice == null || (System.currentTimeMillis() - latestPrice.getTimestamp()) > 60)
            return null;

        NormalisedPriceInformation priceInformation = (NormalisedPriceInformation)latestPrice;
        priceInformation.setCorrectedTimestamp(latestPrice.getTimestamp());

        return priceInformation;
    }

    @Override
    public void init(String propertiesFile) {
        Thread t = new Thread(new GetLiveData());
    }

    private class GetLiveData implements Runnable {

        private final static String CLOSE_PATTERN = "\"Close\":(?<price>[0-9\\.]*)\\}";

        public void run() {

            Pattern p = Pattern.compile(CLOSE_PATTERN);

            while(true) {

                try {
                    URL url = new URL("https://candle.etoro.com/candles/desc.json/OneMinute/2/17?1459765157852");


                    InputStream inputStream = url.openStream();         // throws an IOException

                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

                    String s = dataInputStream.readLine();

                    Matcher matcher = p.matcher(s);

                    if (matcher.find()) {
                        String priceStr = matcher.group("price");

                        latestPrice = new PriceInformation((int)System.currentTimeMillis(), Integer.parseInt(priceStr),Integer.parseInt(priceStr));
                    }

                    Thread.currentThread().sleep(10000);

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


package com.futurewebdynamics.trader;

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
 * Created by 52con on 05/04/2016.
 */
public class DataPoller implements Runnable{

    private StatementBuffer _buffer;
    private final static String CLOSE_PATTERN = "\"Close\":(?<price>[0-9\\.]*)\\}";

    final static Logger _logger = Logger.getLogger(DataPoller.class);

    public DataPoller(StatementBuffer buffer) {
        _buffer = buffer;
    }

    public void run() {

        _logger.info("DataPoller running");
        Pattern p = Pattern.compile(CLOSE_PATTERN);

        //record data indefinitely
        while(true) {

            try {
                URL url = new URL("https://candle.etoro.com/candles/desc.json/OneMinute/2/17?1459765157852");

                InputStream inputStream = url.openStream();         // throws an IOException

                DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));

                String s = dataInputStream.readLine();

                Matcher matcher = p.matcher(s);

                if (matcher.find()) {
                    String priceStr = matcher.group("price");

                    _buffer.AddToBuffer("INSERT INTO price (timestamp, price) VALUES(FROM_UNIXTIME(" + System.currentTimeMillis() / 1000L + "), " + priceStr.replace(".", "") + ")");
                }

                Thread.currentThread().sleep(60000);

            } catch (InterruptedException e) {
                _logger.debug(e.getMessage());
            } catch (MalformedURLException e) {
                _logger.debug(e.getMessage());
            } catch (IOException e) {
                _logger.debug(e.getMessage());
            }
        }
    }



}

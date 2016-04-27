package com.futurewebdynamics.trader.datasources.providers;

import com.futurewebdynamics.trader.common.DatabaseCache;
import com.futurewebdynamics.trader.common.NormalisedPriceInformation;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.datasources.IDataSource;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by 52con on 15/04/2016.
 */
public class ReplayDataSource implements IDataSource {

    private TimeNormalisedDataCache dataCache;

    private int index = 0;

    final static Logger logger = Logger.getLogger(ReplayDataSource.class);

    public void init(String propertiesFile) {

        Properties prop = new Properties();
        InputStream input = null;
        String dbHost = "";
        String dbUsername = "";
        String dbPassword = "";

        try {
            input = new FileInputStream(propertiesFile);

            prop.load(input);

            dbHost = prop.getProperty("dbhost");
            dbUsername = prop.getProperty("dbusername");
            dbPassword = prop.getProperty("dbpassword");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String connectionString = "jdbc:mysql://" + dbHost + "/trader?user=" + dbUsername + "&password=" + dbPassword;

        DatabaseCache databaseCache = new DatabaseCache(connectionString);
        databaseCache.loadData();

        dataCache = new TimeNormalisedDataCache(databaseCache.getCache());
    }


    public NormalisedPriceInformation getTickData() {

        NormalisedPriceInformation price = dataCache.getMinutePrices()[index++];

        logger.debug("index: " + index + ", price: " + price.getPrice());

        if (index >= dataCache.getCacheSize()) return null;

        return price;

    }

}

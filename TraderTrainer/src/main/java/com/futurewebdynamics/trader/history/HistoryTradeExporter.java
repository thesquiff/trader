package com.futurewebdynamics.trader.history;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurewebdynamics.trader.common.RestHelper;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.Trade;
import com.futurewebdynamics.trader.trader.providers.Oanda.data.Trades;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charlie on 24/04/2017.
 */
public class HistoryTradeExporter {

    public static void main(String[] args) {

        String token = args[0];
        String accountId = args[1];


        Trades trades = null;
        try {
            String tradesJson = RestHelper.GetJson("https://api-fxpractice.oanda.com/v3/accounts/" + accountId + "/trades?state=CLOSED&count=500", token);

            ObjectMapper jsonDeseserialiser = new ObjectMapper();
            trades = jsonDeseserialiser.readValue(tradesJson, Trades.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        //export to csv file

        List<String> lines = new ArrayList<String>();
        lines.add("openTime, units, openPrice, closeTime, closePrice, financing, realisedProfitLoss, durationSeconds");

        for (Trade trade : trades.trades) {

            long openDate = javax.xml.bind.DatatypeConverter.parseDateTime(trade.openTime).getTimeInMillis();
            long closeDate = javax.xml.bind.DatatypeConverter.parseDateTime(trade.closeTime).getTimeInMillis();

            long duration = (closeDate - openDate)/1000;

            lines.add(String.format("%s,%d,%f,%s,%s,%f,%f,%d", trade.openTime, trade.initialUnits, trade.price, trade.closeTime, trade.averageClosePrice, trade.financing, trade.realizedPL, duration));
        }

        try {
            Path file = Paths.get("history.csv");
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }


    }

}

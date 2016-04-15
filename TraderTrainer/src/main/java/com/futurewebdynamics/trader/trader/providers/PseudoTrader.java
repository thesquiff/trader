package com.futurewebdynamics.trader.trader.providers;

import com.futurewebdynamics.trader.common.DatabaseCache;
import com.futurewebdynamics.trader.common.DatabaseUtils;
import com.futurewebdynamics.trader.common.TimeNormalisedDataCache;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.trader.ITrader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Created by 52con on 15/04/2016.
 */
public class PseudoTrader implements ITrader {

    private Connection connection;

    private String connectionString;

    public PseudoTrader() {
    }

    public void init(String propertiesFile) {
        Properties prop = new Properties();
        InputStream input = null;
        String dbHost = "";
        String dbUsername = "";
        String dbPassword = "";

        try {
            input = new FileInputStream(propertiesFile);

            prop.load(input);

            dbHost = prop.getProperty("pseudodbhost");
            dbUsername = prop.getProperty("pseudodbusername");
            dbPassword = prop.getProperty("pseudodbpassword");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String connectionString = "jdbc:mysql://" + dbHost + "/trader?user=" + dbUsername + "&password=" + dbPassword;


    }

    @Override
    public boolean openPosition(Position position) {
        return false;
    }

    @Override
    public boolean checkPosition() {
        return false;
    }

    @Override
    public boolean closePosition() {
        return false;
    }

    @Override
    public ArrayList<Position> getPositions() {
        //we're going to store out positions in a mysql db

        DatabaseUtils.refreshConnection(connection, connectionString);

        ArrayList<Position> positions = new ArrayList<Position>();

        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM psuedopositions");

            while (rs.next()) {
                Position position = new Position();
                position.setTargetOpenPrice(rs.getInt("targetpriceopened"));
                position.setTargetSellPrice(rs.getInt("targetpriceclosed"));
                position.setActualOpenPrice(rs.getInt("actualpriceopened"));
                position.setActualSellPrice(rs.getInt("actualpriceclosed"));
                position.setQuantity(rs.getInt("quantity"));
                position.setLeverage(rs.getInt("leverage"));
                position.setLeverage(rs.getInt("leverage"));

                Calendar timeOpened = GregorianCalendar.getInstance();
                timeOpened.setTimeInMillis(rs.getInt("timeopened")*1000);
                position.setTimeOpened(timeOpened);

                Calendar timeClosed = GregorianCalendar.getInstance();
                timeOpened.setTimeInMillis(rs.getInt("timeclosed")*1000);
                position.setTimeClosed(timeClosed);

                position.setStatus(PositionStatus.valueOf(rs.getString("status")));
                positions.add(position);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return positions;

    }
}

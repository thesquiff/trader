package com.futurewebdynamics.trader.trader.providers;

import com.futurewebdynamics.trader.common.DatabaseUtils;
import com.futurewebdynamics.trader.positions.Position;
import com.futurewebdynamics.trader.positions.PositionStatus;
import com.futurewebdynamics.trader.trader.ITrader;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    final static Logger logger = Logger.getLogger(PseudoTrader.class);

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

        this.connectionString = "jdbc:mysql://" + dbHost + "/trader?user=" + dbUsername + "&password=" + dbPassword;
        logger.debug("Connection string: " + this.connectionString);

    }

    @Override
    public boolean openPosition(Position position) {

        connection = DatabaseUtils.refreshConnection(connection, this.connectionString);

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO psuedopositions (status, timeopened, targetpriceopened, actualpriceopened, leverage, quantity) VALUES(" + position.getStatus().getValue() +", FROM_UNIXTIME(" + position.getTimeOpened().getTimeInMillis()/1000 + "), " + position.getTargetOpenPrice() + ", " + position.getTargetOpenPrice() + ", 1, 100)", Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = statement.getGeneratedKeys();
            keys.next();
            position.setUniqueId(keys.getLong(1));
            position.setStatus(PositionStatus.OPEN);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public boolean checkPosition(Position position) {
        return false;
    }

    @Override
    public boolean closePosition(Position position) {
        try {
            logger.debug("UPDATE psuedopositions SET status=" + PositionStatus.CLOSED.getValue() + ", timeclosed=FROM_UNIXTIME(" + position.getTimeClosed().getTimeInMillis()/1000 + "), targetpriceclosed="+position.getTargetSellPrice() + ", actualpriceclosed=" + position.getTargetSellPrice() + " WHERE psuedopositions.index='" + position.getUniqueId() + "';");
            connection.createStatement().executeUpdate("UPDATE psuedopositions SET status=" + PositionStatus.CLOSED.getValue() + ", timeclosed=FROM_UNIXTIME(" + position.getTimeClosed().getTimeInMillis()/1000 + "), targetpriceclosed="+position.getTargetSellPrice() + ", actualpriceclosed=" + position.getTargetSellPrice() + " WHERE psuedopositions.index='" + position.getUniqueId() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        position.setStatus(PositionStatus.CLOSED);
        return true;
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

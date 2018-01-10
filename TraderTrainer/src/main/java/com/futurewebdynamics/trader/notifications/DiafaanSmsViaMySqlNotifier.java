package com.futurewebdynamics.trader.notifications;

import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Charlie on 13/11/2017.
 */
public class DiafaanSmsViaMySqlNotifier implements INotifier {

    final static Logger logger = Logger.getLogger(DiafaanSmsViaMySqlNotifier.class);

    private String mysqlHost;
    private String mysqlUsername;
    private String mysqlPassword;
    private String mysqlDatabase;
    private String smsTo;

    private Connection connection;

    public DiafaanSmsViaMySqlNotifier(String mysqlHost, String mysqlUsername, String mysqlPassword, String mysqlDatabase, String smsTo)
    {
        this.mysqlHost = mysqlHost;
        this.mysqlPassword = mysqlPassword;
        this.mysqlUsername = mysqlUsername;
        this.mysqlDatabase = mysqlDatabase;
        this.smsTo = smsTo;
    }

    private void Connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + mysqlHost + "/" + mysqlDatabase + "?useLegacyDatetimeCode=false&serverTimezone=UTC", mysqlUsername, mysqlPassword);
        } catch (SQLException ex) {
            logger.error("An error occured connecting to mysql", ex);
        } catch (ClassNotFoundException ex) {
            logger.error("Error loading mysql driver", ex);
        }
    }

    @Override
    public boolean SendNotification(Notification n) {
        try{
            Connect();

            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO MessageOut (MessageTo, MessageType, MessageText) VALUES('" + smsTo + "','sms.Text','" + n.getSubject() + ": " + n.getMessage() + "')");
        } catch(Exception ex)
        {
            logger.error("An error occurred notifying by SMS.", ex);
            return false;
        }
        return true;
    }
}

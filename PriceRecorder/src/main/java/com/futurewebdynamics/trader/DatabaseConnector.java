package com.futurewebdynamics.trader;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Queue;

/**
 * Created by 52con on 05/04/2016.
 */
public class DatabaseConnector implements Runnable
{
    private String _connectionString;

    private Connection _connection = null;

    final static Logger _logger = Logger.getLogger(DatabaseConnector.class);

    private StatementBuffer _statementBuffer;

    public DatabaseConnector(String connectionString, StatementBuffer statementBuffer) {
        _connectionString = connectionString;
        _statementBuffer = statementBuffer;
    }

    private void flushToDatabase(Queue<String> buffer) {
        int bufferSize = buffer.size();

        if (bufferSize <=0) return;

        _logger.info("Flushing buffer of length " + bufferSize + " to database");

        refreshConnection();

        Iterator izzy = buffer.iterator();

        while(izzy.hasNext()) {
            String statement = (String)izzy.next();
            if (!tryStatement(statement)) {
                _statementBuffer.AddToBuffer(statement);
                _logger.debug("Failed to execute statement: " + statement);
            }
            izzy.remove();
        }
    }

    private boolean tryStatement(String statement) {
        //retry 10 times

        for (int i = 1; i <= 10; i++) {
            try {
                _connection.createStatement().executeUpdate(statement);
                return true;
            } catch (SQLException e) {
                _logger.debug("Statement failed (" + i + "): " + e.getMessage());
                refreshConnection();
            }
        }

        return false;

    }

    private void refreshConnection() {
        try {
            while (_connection == null || _connection.isClosed() || !_connection.isValid(2)) {
                _logger.info("Database connection lost - retrying");
                try {
                    _connection = DriverManager.getConnection(_connectionString);
                } catch (SQLException e) {
                    _logger.debug(e.getMessage());
                }
            }
        } catch (SQLException e) {
            _logger.error(e.getMessage());
        }
    }

    public void run() {
        _logger.info("DatabaseConnector running");
        while(true) {
            flushToDatabase(_statementBuffer.getBuffer());
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                _logger.debug(e.getMessage());
            }
        }
    }
}

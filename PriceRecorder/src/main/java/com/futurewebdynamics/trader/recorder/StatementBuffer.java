package com.futurewebdynamics.trader;

import org.apache.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by 52con on 05/04/2016.
 */
public class StatementBuffer {

    private final ConcurrentLinkedQueue<String> _buffer;

    final static Logger _logger = Logger.getLogger(StatementBuffer.class);

    public StatementBuffer() {
        _buffer = new ConcurrentLinkedQueue <String>();
    }

    public void AddToBuffer(String statement) {
        _buffer.add(statement);
    }

    public Queue<String> getBuffer() {
        return _buffer;
    }
}

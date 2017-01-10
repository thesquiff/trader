package com.futurewebdynamics.trader.trainer.viewer;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Charlie on 06/01/2017.
 */
public class DataPlot extends JFrame
{
    private int msPerPixel;
    private int pencePerPixel;

    private int minPence;
    private int maxPence;

    private long minMs;

    private LinkedList<LongPoint> data;

    public DataPlot(int msPerPixel, int pencePerPixel, int minPence, int maxPence, long minMs) {
        this.msPerPixel = msPerPixel;
        this.pencePerPixel = pencePerPixel;
        this.minPence = minPence;
        this.maxPence = maxPence;
        this.minMs = minMs;
    }

    public void paint(Graphics g, Dimension size, int borderWidthPx) {

        ListIterator<LongPoint> izzy = data.listIterator();

        while(izzy.hasNext()) {

            LongPoint p = izzy.next();
            int xCoordinate = (int)Math.round((p.x - minMs)/(double)msPerPixel) + borderWidthPx;
            int yCoordinate = (int)Math.round(size.height - (p.y - minPence) / (double)pencePerPixel) + borderWidthPx;

            g.drawLine(xCoordinate, yCoordinate, xCoordinate, yCoordinate);
        }

    }

    public void setData(LinkedList<LongPoint> data) {
        this.data = data;
    }
}

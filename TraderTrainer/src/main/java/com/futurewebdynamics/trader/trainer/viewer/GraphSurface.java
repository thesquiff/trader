package com.futurewebdynamics.trader.trainer.viewer;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * Created by Charlie on 06/01/2017.
 */
public class GraphSurface extends JPanel {

    private int minPence;
    private int maxPence;
    private long minMs;

    private int borderWidthPx = 50;

    private int msPerPixel = 250;
    private int pencePerPixel = 10;

    private LinkedList<LongPoint> askPriceData;
    private LinkedList<LongPoint> bidPriceData;
    private LinkedList<LongPoint> balanceOfCompletedTradesData;
    private LinkedList<LongPoint> balanceOfOpenTradesData;

    @Override
    public void paintComponent(Graphics g) {

        if (askPriceData == null || bidPriceData == null || balanceOfCompletedTradesData == null || balanceOfOpenTradesData == null) return;

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Dimension panelSize = this.getSize();

        //draw some axes
        g2d.drawLine(borderWidthPx, panelSize.height - borderWidthPx, panelSize.width - 2*borderWidthPx, panelSize.height - borderWidthPx);
        g2d.drawLine(borderWidthPx, panelSize.height - borderWidthPx, borderWidthPx, borderWidthPx);

        Dimension dataPlotSize = new Dimension(panelSize.width - 2*borderWidthPx, panelSize.height - 2*borderWidthPx);

        DataPlot askPricePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs);
        askPricePlot.setData(askPriceData);
        askPricePlot.paint(g2d, dataPlotSize, borderWidthPx);

        DataPlot bidPricePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs);
        bidPricePlot.setData(bidPriceData);
        bidPricePlot.paint(g2d, dataPlotSize, borderWidthPx);

        DataPlot balanceOfCompletedTradesPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs);
        balanceOfCompletedTradesPlot.setData(balanceOfCompletedTradesData);
        balanceOfCompletedTradesPlot.paint(g2d, dataPlotSize, borderWidthPx);

        DataPlot balanceOfOpenTradesPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs);
        balanceOfOpenTradesPlot.setData(balanceOfOpenTradesData);
        balanceOfOpenTradesPlot.paint(g2d, dataPlotSize, borderWidthPx);

    }

    public void setMinPence(int minPence) {
        this.minPence = minPence;
    }

    public void setMaxPence(int maxPence) {
        this.maxPence = maxPence;
    }

    public void setMinMs(long minMs) {
        this.minMs = minMs;
    }

    public void setAskPriceData(LinkedList<LongPoint> askPriceData) {
        this.askPriceData = askPriceData;
    }

    public void setBidPriceData(LinkedList<LongPoint> bidPriceData) {
        this.bidPriceData = bidPriceData;
    }

    public void setBalanceOfCompletedTradesData(LinkedList<LongPoint> balanceOfCompletedTradesData) {
        this.balanceOfCompletedTradesData = balanceOfCompletedTradesData;
    }

    public void setBalanceOfOpenTradesData(LinkedList<LongPoint> balanceOfOpenTradesData) {
        this.balanceOfOpenTradesData = balanceOfOpenTradesData;
    }

    public void setPencePerPixel(int pencePerPixel) {
        this.pencePerPixel = pencePerPixel;
    }

    public void setMsPerPixel(int msPerPixel) {
        this.msPerPixel = msPerPixel;
    }
}

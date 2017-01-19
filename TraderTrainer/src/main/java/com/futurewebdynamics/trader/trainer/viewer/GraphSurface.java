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
    private long maxMs;

    private int borderWidthPx = 50;

    private int msPerPixel = 250;
    private double pencePerPixel = 10;

    private int majorPenceTick = 50;

    private LinkedList<LongPoint> askPriceData;
    private LinkedList<LongPoint> bidPriceData;
    private LinkedList<LongPoint> balanceOfCompletedTradesData;
    private LinkedList<LongPoint> balanceOfOpenTradesData;
    private LinkedList<LongPoint> shortOpenData;
    private LinkedList<LongPoint> shortCloseData;
    private LinkedList<LongPoint> longOpenData;
    private LinkedList<LongPoint> longCloseData;

    private DataPlot askPricePlot;
    private DataPlot bidPricePlot;
    private DataPlot balanceOfOpenTradesPlot;
    private DataPlot balanceOfCompletedTradesPlot;
    private DataPlot shortOpenPlot;
    private DataPlot shortClosePlot;
    private DataPlot longOpenPlot;
    private DataPlot longClosePlot;

    public GraphSurface(int msPerPixel, double pencePerPixel, int minPence, int maxPence, long minMs) {
        this.msPerPixel = msPerPixel;
        this.pencePerPixel = pencePerPixel;
        this.minPence = minPence;
        this.maxPence = maxPence;
        this.minMs = minMs;
        askPricePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.DATA);
        bidPricePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.DATA);
        balanceOfCompletedTradesPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.BALANCE);
        balanceOfOpenTradesPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.BALANCE);
        shortOpenPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.ACTIVITY);
        shortClosePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.ACTIVITY);
        longOpenPlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.ACTIVITY);
        longClosePlot = new DataPlot(msPerPixel,pencePerPixel,minPence, maxPence, minMs, PlotType.ACTIVITY);
    }

    @Override
    public void paintComponent(Graphics g) {


        Rectangle bounds = g.getClipBounds();

        this.setBackground(Color.WHITE);

        if (askPriceData == null || bidPriceData == null || balanceOfCompletedTradesData == null || balanceOfOpenTradesData == null) return;

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Dimension panelSize = this.getSize();

        this.setPreferredSize(new Dimension(borderWidthPx*2 + (int)((maxMs - minMs) / msPerPixel), borderWidthPx + (int)((maxPence - minPence) / pencePerPixel)));
        this.setSize(this.getPreferredSize());

        //draw some axes
        g2d.drawLine(borderWidthPx, panelSize.height - borderWidthPx, panelSize.width - 2*borderWidthPx, panelSize.height - borderWidthPx);
        g2d.drawLine(borderWidthPx, panelSize.height - borderWidthPx, borderWidthPx, borderWidthPx);

        Dimension dataPlotSize = new Dimension(panelSize.width - 2*borderWidthPx, panelSize.height - 2*borderWidthPx);

        askPricePlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.green);
        bidPricePlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.red);
        //balanceOfCompletedTradesPlot.paint(g2d, dataPlotSize, borderWidthPx, bounds);
        //balanceOfOpenTradesPlot.paint(g2d, dataPlotSize, borderWidthPx, bounds);
        shortOpenPlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.black);
        shortClosePlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.BLACK);
        longOpenPlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.black);
        longClosePlot.paint(g2d, dataPlotSize, borderWidthPx, bounds, Color.BLACK);

        Stroke defaultStroke = g2d.getStroke();

        //label y axis

        for (int y = minPence; y < maxPence; y += majorPenceTick) {

            int yCoordinate = (int)Math.round(panelSize.getHeight() - (y-minPence) / (double)pencePerPixel) - borderWidthPx;

            //draw the tick
            g2d.setStroke(defaultStroke);
            g2d.setColor(Color.BLACK);
            g2d.drawLine(40, yCoordinate, 50, yCoordinate);

            g2d.drawString(String.valueOf(y), 9, yCoordinate+4);

            //dotted lines
            //set the stroke of the copy, not the original
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2d.setColor(Color.lightGray);
            g2d.setStroke(dashed);

            int xCoordinate = this.getPreferredSize().width;

            g2d.drawLine(50, yCoordinate, xCoordinate, yCoordinate);
        }



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
        askPricePlot.setData(this.askPriceData);
    }

    public void setBidPriceData(LinkedList<LongPoint> bidPriceData) {
        this.bidPriceData = bidPriceData;
        bidPricePlot.setData(bidPriceData);
    }

    public void setBalanceOfCompletedTradesData(LinkedList<LongPoint> balanceOfCompletedTradesData) {
        this.balanceOfCompletedTradesData = balanceOfCompletedTradesData;
        balanceOfCompletedTradesPlot.setData(balanceOfCompletedTradesData);
    }

    public void setBalanceOfOpenTradesData(LinkedList<LongPoint> balanceOfOpenTradesData) {
        this.balanceOfOpenTradesData = balanceOfOpenTradesData;
        balanceOfOpenTradesPlot.setData(balanceOfOpenTradesData);
    }

    public void setPencePerPixel(int pencePerPixel) {
        this.pencePerPixel = pencePerPixel;
    }

    public void setMsPerPixel(int msPerPixel) {
        this.msPerPixel = msPerPixel;
    }

    public void setMaxMs(long maxMs) {
        this.maxMs = maxMs;
    }

    public void setShortOpenData(LinkedList<LongPoint> shortOpenData) {
        this.shortOpenData = shortOpenData;
        this.shortOpenPlot.setData(this.shortOpenData);
    }

    public void setShortCloseData(LinkedList<LongPoint> shortCloseData) {
        this.shortCloseData = shortCloseData;
        this.shortClosePlot.setData(this.shortCloseData);
    }

    public void setLongCloseData(LinkedList<LongPoint> longCloseData) {
        this.longCloseData = longCloseData;
        this.longClosePlot.setData(longCloseData);
    }

    public void setLongOpenData(LinkedList<LongPoint> longOpenData) {
        this.longOpenData = longOpenData;
        this.longOpenPlot.setData(longOpenData);
    }
}

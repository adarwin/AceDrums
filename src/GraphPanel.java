/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;

class GraphPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    static int maximumY = 127;
    private ArrayList<StrokeDatum> strokeData;
    private long totalStrokeDataTime;
    private int numberOfGraphStrokes;
    long lastAdditionTime;
    long firstAdditionTime;


    public GraphPanel() {
        super();
        strokeData = new ArrayList<StrokeDatum>();
        setLayout(null);
    }


    private synchronized void resetStrokeData() {
        strokeData.clear();
        totalStrokeDataTime = 0;
        numberOfGraphStrokes = 0;
    }

    protected synchronized void addStrokeDatum(int type, int newDatum,
                                               int timeSinceLastDatum) {
        long additionTime = System.nanoTime()/1000; // Convert to microseconds
        if (firstAdditionTime == 0) {
            firstAdditionTime = additionTime;
        }
        if (additionTime-lastAdditionTime > 50000) {//300000) {
            resetStrokeData();
            firstAdditionTime = 0;
        }
        totalStrokeDataTime += timeSinceLastDatum;
        if (type == StrokeDatum.GRAPH_STROKE) {
            numberOfGraphStrokes++;
        }
        strokeData.add(new StrokeDatum(type, newDatum, totalStrokeDataTime));
        lastAdditionTime = additionTime;
        repaint();
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        int dataLength = strokeData.size();
        long averageDuration = 0;
        if (dataLength > numberOfGraphStrokes) {
        averageDuration = totalStrokeDataTime/
                          (dataLength-numberOfGraphStrokes);
        }
        double xIncrementor = 1;
        double yIncrementor = 1;
        double width = (double)getWidth();
        double height = (double)getHeight();
        // Draw x and y axis
        int topMargin = 10, bottomMargin = 35;
        int leftMargin = 30, rightMargin = 30;
        int tickLength = 5;
        int yAxisHeight = (int)height - bottomMargin;
        int rightEdge = (int)width - rightMargin;
        double verticalScale = (double)(yAxisHeight-topMargin)/127.0;
        double horizontalScale = (double)(rightEdge-leftMargin)/200;
        g.drawLine(leftMargin, topMargin,
                   leftMargin, yAxisHeight);
        g.drawLine(leftMargin, yAxisHeight,
                   rightEdge, yAxisHeight);
        // Draw ticks
        g.drawLine(leftMargin, yAxisHeight,
                   leftMargin-tickLength, yAxisHeight);
        g.drawLine(leftMargin, topMargin, leftMargin-tickLength, topMargin);
        g.drawLine(leftMargin, yAxisHeight,
                   leftMargin, yAxisHeight+tickLength);
        g.drawLine(rightEdge, yAxisHeight, rightEdge, yAxisHeight+tickLength);
        boolean showGridlines = false;
        if (showGridlines) {
            g.setColor(Color.gray);
            for (int i = 10; i <= 120; i+=10) {
                int tickLocation = yAxisHeight-(int)(i*verticalScale);
                g.drawLine(leftMargin, tickLocation,
                           rightEdge, tickLocation);
            }
            for (int i = 10; i <= 200; i+=10) {
                int tickLocation = leftMargin+(int)(i*horizontalScale);
                g.drawLine(tickLocation, yAxisHeight,
                           tickLocation, topMargin);
            }
            g.setColor(Color.black);
        }

        // Draw Labels
        g.drawString("0", leftMargin-tickLength-10, yAxisHeight+5);
        g.drawString("127", leftMargin-tickLength-25, topMargin+5);
        g.drawString("0", leftMargin-4, yAxisHeight+tickLength+13);
        int centerX = (leftMargin+rightEdge)/2;
        g.drawString("ms", centerX-6, yAxisHeight+tickLength+20);
        g.drawString("" + (totalStrokeDataTime/100),
                     rightEdge-30, yAxisHeight+tickLength+15);
        g.drawString("Average Sample Length = " + averageDuration + " µs",
                     0, (int)height);

        if (dataLength > 0) {
            //xIncrementor = (rightEdge-leftMargin)/(double)dataLength;
            xIncrementor = (rightEdge-leftMargin)/(double)totalStrokeDataTime;
        }
        yIncrementor = (yAxisHeight-topMargin)/(double)maximumY;
        boolean pointWasPicked = false;
        int pointSize = 4;
        for (StrokeDatum datum : strokeData) {
        //for (int i = 0; i < dataLength-1; i++) {
            //int datum = strokeData.get(i);
            int type = datum.getType();
            int value = datum.getValue();
            //int value = strokeData.getValue();
            int x = leftMargin + (int)(datum.getTimePoint()*xIncrementor);
            int y = yAxisHeight - (int)(value*yIncrementor);
            if (type == StrokeDatum.GRAPH_STROKE) {
                pointSize = 8;
                g.setColor(Color.blue);
                int backwardOffset = (int)(4*averageDuration*xIncrementor);
                int forwardOffset = (int)(2*averageDuration*xIncrementor);
                g.drawLine(x - backwardOffset, y,
                           x + forwardOffset, y);
                g.drawLine(x, y-(int)(100*xIncrementor), x, y);
                //g.fillOval(x, y, pointSize, pointSize);
                g.drawString("" + value, x + forwardOffset, y);
                g.setColor(Color.black);
                pointSize = 4;
                pointWasPicked = false;
            } else {
                pointSize = 4;
                x -= pointSize/2;
                y -= pointSize/2;
                g.fillOval(x, y, pointSize, pointSize);
            }
        }
    }
}




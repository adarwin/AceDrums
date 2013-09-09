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
    private ArrayList<Integer> strokeData;
    long lastAdditionTime;


    public GraphPanel() {
        super();
        strokeData = new ArrayList<Integer>();
        setLayout(null);
    }


    private synchronized void resetStrokeData() {
        strokeData = new ArrayList<Integer>();
    }

    protected synchronized void addStrokeDatum(int value) {
        long additionTime = System.nanoTime()/1000;
        if (additionTime-lastAdditionTime > 300000) {
            resetStrokeData();
        }
        strokeData.add(value);
        lastAdditionTime = additionTime;
        repaint();
    }

    @Override
    protected synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        double xIncrementor = 1;
        double yIncrementor = 1;
        double width = (double)getWidth();
        double height = (double)getHeight();
        int dataLength = strokeData.size();
        // Draw x and y axis
        int topMargin = 10, bottomMargin = 35;
        int leftMargin = 40, rightMargin = 30;
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
        g.drawString("µs", centerX-6, yAxisHeight+tickLength+20);

        if (dataLength > 0) {
            xIncrementor = (rightEdge-leftMargin)/(double)dataLength;
        }
        yIncrementor = (yAxisHeight-topMargin)/(double)maximumY;
        for (int i = 0; i < dataLength-1; i++) {
            int x = leftMargin + (int)(i*xIncrementor)-2;
            int y = (int)(strokeData.get(i)*yIncrementor);
            y = yAxisHeight-y-2;
            g.fillOval(x, y, 4, 4);
        }
    }
}




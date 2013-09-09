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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.ArrayList;

class GraphDialog extends JDialog {
    class DrawingPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
            width = (double)getWidth();
            height = (double)getHeight();
            double xIncrementor = 1;
            double yIncrementor = 1;
            if (strokeData.size() > 0) {
                xIncrementor = width/(double)strokeData.size();
            }
            System.out.println("strokeData's size is " + strokeData.size());
            if (maximumValue > 0) {
                yIncrementor = height/(double)maximumValue;
            }
            System.out.println("maximum value = " + maximumValue);
            System.out.println("minimum value = " + minimumValue);
            int length = strokeData.size();
            for (int i = 0; i < length-1; i++) {
                int x = (int)(i*xIncrementor)-2;
                int y = (int)(strokeData.get(i)*yIncrementor);
                y = ((int)height)-y-2;
                g.fillOval(x, y, 4, 4);
            }
        }
    }



    private static final long serialVersionUID = 1L;
    private DrawingPanel drawingPanel;
    private ArrayList<Point> dataPoints;
    double width, height;
    int maximumValue, minimumValue;
    private ArrayList<Integer> strokeData;
    long lastAdditionTime;


    public GraphDialog(JFrame frame) {
        super(frame, "Stroke Graph", false);
        drawingPanel = new DrawingPanel();
        strokeData = new ArrayList<Integer>();
        //drawingPanel.setLayout(null);
        setContentPane(drawingPanel);
        setSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        setVisible(true);
    }


    protected void setStrokeData(ArrayList<Integer> data) {
        strokeData = data;
        maximumValue = getMaximumStroke();
        minimumValue = getMinimumStroke();
    }
    protected int getMaximumStroke() {
        int temp = 0;
        for (int value : strokeData) {
            temp = Math.max(temp, value);
        }
        return temp;
    }
    protected int getMinimumStroke() {
        int temp = 0;
        for (int value : strokeData) {
            temp = Math.min(temp, value);
        }
        return temp;
    }
    protected void resetStrokeData() {
        strokeData = new ArrayList<Integer>();
    }
    protected void addStrokeDatum(int value) {
        System.out.println("Received new value of " + value);
        strokeData.add(value);
        lastAdditionTime = System.nanoTime()/1000;
        maximumValue = getMaximumStroke();
        minimumValue = getMinimumStroke();
        System.out.println("set max to " + maximumValue);
        drawingPanel.repaint();
    }
    protected void draw() {
        drawingPanel.repaint();
    }
}

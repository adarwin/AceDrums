/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import javax.swing.JPanel;
 import java.awt.Graphics;
 import java.awt.Dimension;
 import javax.swing.JButton;
 import java.util.ArrayList;
 import java.util.logging.Logger;
 import java.util.logging.Level;

 class DrumPanel extends JPanel {
     private static final Logger logger = Logger.getLogger(
                                                    DrumPanel.class.getName());
     private ArrayList<DrumWidget> drumWidgets;
     DrumPanel() {
         super();
         drumWidgets = new ArrayList<DrumWidget>();
         setLayout(null);
     }
     void addDrumWidget(DrumWidget drumWidget) {
         drumWidgets.add(drumWidget);
         add(drumWidget);
     }
     @Override
     protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         int centerX = getWidth()>>1; // >> 1 is the same as dividing by 2
         int centerY = getHeight()>>1;
         for (DrumWidget drumWidget : drumWidgets) {
             //Dimension preferredSize = drumWidget.getPreferredSize();
             int width = drumWidget.getImageWidth();//preferredSize.width;
             int height = drumWidget.getImageHeight();//preferredSize.height;
             int xOffsetFromCenter = drumWidget.getXOffsetFromCenter();
             int yOffsetFromCenter = drumWidget.getYOffsetFromCenter();
             drumWidget.setBounds(centerX + xOffsetFromCenter - (width>>1),
                                  centerY + yOffsetFromCenter - (height>>1),
                                  width, height);
         }
     }
 }

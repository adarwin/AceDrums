/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Graphics;
 import java.awt.Dimension;
 import javax.swing.JMenuItem;
 import java.util.logging.Logger;
 import java.util.logging.Level;

 class TomWidget extends DrumWidget {
     private static final Logger logger = Logger.getLogger(SnareDrumWidget.class.getName());
     private String drumName;
     TomWidget(String drumName, String imagePath,
               int xOffsetFromCenter, int yOffsetFromCenter) {
         super(drumName);
         this.drumName = drumName;
         this.xOffsetFromCenter = xOffsetFromCenter;
         this.yOffsetFromCenter = yOffsetFromCenter;
         //setPreferredSize(new Dimension(width, height));
         loadImage(imagePath);
     }
     /*
     @Override
     void buildRightClickMenu() {
         //super.buildRightClickMenu();
         //rightClickMenu.add(new JMenuItem(drumName));
     }
     */
 }


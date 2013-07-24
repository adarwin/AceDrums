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

 class SnareDrumWidget extends DrumWidget {
     private static final Logger logger = Logger.getLogger(SnareDrumWidget.class.getName());
     SnareDrumWidget(int xOffsetFromCenter, int yOffsetFromCenter) {
         super("Snare");
         this.xOffsetFromCenter = xOffsetFromCenter;
         this.yOffsetFromCenter = yOffsetFromCenter;
         //setPreferredSize(new Dimension(140, 140));
         loadImage("img/snare.png");
     }
     /*
     @Override
     void buildRightClickMenu() {
         //super.buildRightClickMenu();
         //rightClickMenu.add(new JMenuItem("Snare"));
     }
     */
 }


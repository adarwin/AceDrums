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
     SnareDrumWidget() {
         super();
         xOffsetFromCenter = -50;
         yOffsetFromCenter = 50;
         setPreferredSize(new Dimension(70, 70));
     }
     @Override
     void buildRightClickMenu() {
         super.buildRightClickMenu();
         rightClickMenu.add(new JMenuItem("Snare"));
     }
 }


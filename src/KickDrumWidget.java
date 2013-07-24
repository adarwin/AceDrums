/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Color;
 import java.awt.Graphics;
 import java.awt.Dimension;
 import javax.swing.JMenuItem;
 import javax.imageio.ImageIO;
 import java.io.File;
 import java.io.IOException;
 import java.util.logging.Logger;
 import java.util.logging.Level;

 class KickDrumWidget extends DrumWidget {
     private static final Logger logger = Logger.getLogger(KickDrumWidget.class.getName());
     KickDrumWidget() {
         super("Kick");
         xOffsetFromCenter = 0;
         yOffsetFromCenter = 0;
         //setPreferredSize(new Dimension(220, 220));
         loadImage("img/kick.png");
     }
     /*
     @Override
     void buildRightClickMenu() {
         super.buildRightClickMenu();
         logger.log(Level.INFO, "Added new JMenuItem, 'kick' to right-click " +
                                "menu");
         rightClickMenu.add(new JMenuItem("kick"));
     }
     */
     /*
     @Override
     protected void paintComponent(Graphics g) {
         //g.setColor(Color.red);
         //super.paintComponent(g);
         //g.fillOval(0, 0, getWidth(), getHeight());
     }
     */
 }


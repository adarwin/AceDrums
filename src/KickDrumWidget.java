/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Graphics;
 import javax.swing.JMenuItem;

 class KickDrumWidget extends DrumWidget {
     KickDrumWidget() {
         super();
     }
     @Override
     void buildRightClickMenu() {
         super.buildRightClickMenu();
         rightClickMenu.add(new JMenuItem("kick"));
     }
 }


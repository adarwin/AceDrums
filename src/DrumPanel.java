/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import javax.swing.JPanel;
 import javax.swing.JButton;
 import java.util.ArrayList;

 class DrumPanel extends JPanel {
     private ArrayList<DrumWidget> drumWidgets;
     DrumPanel() {
         super();
         drumWidgets = new ArrayList<DrumWidget>();
     }
     void addDrumWidget(DrumWidget drumWidget) {
         drumWidgets.add(drumWidget);
         add(drumWidget);
         add(new JButton("test"));
     }
 }

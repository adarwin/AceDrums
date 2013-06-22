/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Dimension;
 import javax.swing.JFrame;
 import javax.swing.SwingUtilities;

 public class AceDrums {
     private static JFrame frame;

     public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 createAndShowGUI();
             }
         });
     }

     public static void createAndShowGUI() {
         initializeGlobalVariables();
         configureContainers();
         configureComponents();
         showGUI();
     }
     private static void initializeGlobalVariables() {
         frame = new JFrame("AceDrums");
     }
     private static void configureContainers() {
         frame.setSize(new Dimension(640, 480));
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }
     private static void configureComponents() {
     }
     private static void showGUI() {
         frame.setVisible(true);
     }
 }


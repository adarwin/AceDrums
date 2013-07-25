/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Dimension;
 import java.awt.event.ActionListener;
 import java.awt.event.ActionEvent;
 import java.util.ArrayList;
 import java.util.logging.Logger;
 import java.util.logging.Level;
 import javax.swing.JPanel;
 import javax.swing.JFrame;
 import javax.swing.JButton;
 import javax.swing.JToolBar;
 import javax.swing.JMenuBar;
 import javax.swing.JMenu;
 import javax.swing.JMenuItem;
 import javax.swing.SwingUtilities;

 public class AceDrums {

     private static final Logger logger = Logger.getLogger(AceDrums.class.getName());
     private static int startingWidth = 800;
     private static int startingHeight = 600;
     private static JFrame frame;
     private static JMenuBar menuBar;
     private static JMenu fileMenu;
     private static JMenu editMenu;
     private static DrumPanel drumPanel;
     private static JToolBar toolbar;
     private static ArrayList<DrumWidget> drumWidgets;
     private static boolean DEBUG = true;

     public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 createAndShowGUI();
             }
         });
     }

     public static void createAndShowGUI() {
         logger.log(Level.INFO, "Beginning to create and show GUI");
         initializeGlobalVariables();
         buildContainers();
         buildComponents();
         addComponentsToContainers();
         addContainersToFrame();
         showGUI();
     }

     private static void initializeGlobalVariables() {
         frame = new JFrame("AceDrums");
     }

     private static void buildContainers() {
         drumPanel = new DrumPanel();
         frame.setSize(new Dimension(startingWidth, startingHeight));
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     }

     private static void buildComponents() {
         buildMenuBar();
         buildToolBar();
         buildDrumWidgets();
     }

     private static void buildMenuBar() {
         menuBar = new JMenuBar();
         JMenu fileMenu = new JMenu("File");
         JMenu editMenu = new JMenu("Edit");
         JMenu setMenu = new JMenu("Set");
         JMenu viewMenu = new JMenu("View");


         // Build file menu
         JMenuItem menuItem = new JMenuItem("Exit");
         menuItem.addActionListener(new ActionListener() {
             public void actionPerformed(ActionEvent e) {
                 System.exit(0);
             }
         });
         fileMenu.add(menuItem);

         // Build set menu
         menuItem = new JMenuItem("Show Mixer");
         setMenu.add(menuItem);

         JMenu menu = new JMenu("Add Drum");
         menuItem = new JMenuItem("Snare");
         menu.add(menuItem);
         menuItem = new JMenuItem("Kick");
         menu.add(menuItem);
         setMenu.add(menu);

         menuItem = new JMenuItem("Manage set...");
         setMenu.add(menuItem);

         menuBar.add(fileMenu);
         menuBar.add(editMenu);
         menuBar.add(setMenu);
         menuBar.add(viewMenu);
     }

     private static void buildToolBar() {
         toolbar = new JToolBar();
         JButton currentToolbarButton = new JButton("1");
         toolbar.add(currentToolbarButton);
         currentToolbarButton = new JButton("2");
         toolbar.add(currentToolbarButton);
     }

     private static void buildDrumWidgets() {
         /*
         drumWidgets = new ArrayList<DrumWidget>();
         drumWidgets.add(new KickDrumWidget());
         */
     }

     private static void addComponentsToContainers() {
         // Assumes all components and containers have been built and
         // configured
         frame.setJMenuBar(menuBar);
         frame.setContentPane(drumPanel);
         //mainContentPanel.add(toolbar);
         //drumPanel.add(new KickDrumWidget());
         /*
         SnareDrumWidget snare = new SnareDrumWidget(-97, -23);
         drumPanel.addDrumWidget(snare);
         */
         drumPanel.addDrumWidget(
            new DrumWidget("Crash", "img/crash.png", -100, -200)
         );
         drumPanel.addDrumWidget(
            new DrumWidget("Hi-Hat", "img/hat.png", -155, -48)
         );
         drumPanel.addDrumWidget(
            new DrumWidget("Snare", "img/snare.png", -99, -21)
         );
         drumPanel.addDrumWidget(
            new DrumWidget("Kick", "img/kick.png", 0, 0)
         );
         //DrumWidget hat = new DrumWidget("Hi-Hat", "img/hat.png", -100, -100);
         //TomWidget hiTom = new TomWidget("Hi Tom", "img/hitom.png", -100, -100);
         //drumPanel.addDrumWidget(hiTom);
         //TomWidget lowTom = new TomWidget("Low Tom", "img/lowtom.png", 110, -110);
         //drumPanel.addDrumWidget(lowTom);
         //TomWidget floorTom = new TomWidget("Floor Tom", "img/floortom.png", 130, 90);
         //drumPanel.addDrumWidget(floorTom);
         //drumPanel.addDrumWidget(new KickDrumWidget());
     }

     private static void addContainersToFrame() {
         //frame.add(mainContentPanel);
     }

     private static void showGUI() {
         frame.setVisible(true);
     }
 }



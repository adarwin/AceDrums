/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import de.humatic.mmj.MidiSystem;
import de.humatic.mmj.MidiOutput;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.beans.PropertyVetoException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;


public class AceDrums {

    protected static SerialConnection serialConnection;

    private static final Logger logger = Logger.getLogger(
                                                    AceDrums.class.getName());
    private static final int startingWidth = 800;
    private static final int startingHeight = 600;
    private static JFrame frame;
    private static JMenuBar menuBar;
    private static JMenu fileMenu;
    private static JMenu editMenu;
    private static DrumPanel drumPanel;
    private static boolean setManagementMode;
    private static JToolBar toolbar;
    private static ArrayList<DrumWidget> drumWidgets;
    private static MidiOutput midiOutput;
    private static MIDIKit midiKit;
    private static GraphDialog graphDialog;
    private static GraphPanel graphPanel;
    private static boolean DEBUG = true;

    public static void main(String[] args) {
        // Set the look and feel
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException ex) {
            logger.log(Level.SEVERE, "Could not set Nimbus look and feel " +
                                     "due to UnsupportedLookAndFeelException");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Could not set Nimbus look and feel " +
                                     "due to ClassNotFoundException");
        } catch (InstantiationException ex) {
            logger.log(Level.SEVERE, "Could not set Nimbus look and feel " +
                                     "due to InstantiationException");
        } catch (IllegalAccessException ex) {
            logger.log(Level.SEVERE, "Could not set Nimbus look and feel " +
                                     "due to IllegalAccessException");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create and show the GUI on the Event Dispatch Thread (EDT)
                createAndShowGUI();
            }
        });
    }


    protected static void requestDrumWidgetRemoval(DrumWidget targetedDrum) {
        drumPanel.removeDrumWidget(targetedDrum);
    }


    protected static void requestTweakDialog(DrumWidget associatedDrumWidget) {
        new TweakDialog(frame, associatedDrumWidget.drumName);
    }

    private static void createAndShowGUI() {
        logger.log(Level.INFO, "Beginning to create and show GUI");
        initializeGlobalVariables();
        buildContainers();
        buildComponents();
        addComponentsToContainers();
        addContainersToFrame();
        showGUI();
    }

    /*
    protected static void setThreshold(int value) {
        if (!serialConnection.setThreshold(value)) {
            logger.log(Level.WARNING, "Failed to set threshold to " + value);
        }
    }
    protected static void setSensitivity(int value) {
        if (!serialConnection.setSensitivity(value)) {
            logger.log(Level.WARNING, "Failed to set sensitivity to " + value);
        }
    }
    protected static void setTimeout(int value) {
        if (!serialConnection.setTimeout(value)) {
            logger.log(Level.WARNING, "Failed to set timeout to " + value);
        }
    }
    */

    private static void initializeGlobalVariables() {
        MidiSystem.initMidiSystem("AceDrums", "SuperiorDrummer");
        midiOutput = MidiSystem.openMidiOutput(0);
        midiKit = new MIDIKit();
        serialConnection = new SerialConnection();
        frame = new JFrame("AceDrums");
    }

    protected static void reportNewDatum(int type, int newDatum,
                                         int timeSinceLastDatum) {
        if (graphDialog != null) {
            graphDialog.addStrokeDatum(type, newDatum, timeSinceLastDatum);
        }
    }

    protected static void reportStroke(byte midiID, byte velocity) {
        // Send note on message
        midiOutput.sendMidi(new byte[] { (byte)144, midiID, velocity });
        // Send note off message
        midiOutput.sendMidi(new byte[] { (byte)128, midiID, velocity });
    }

    private static void buildContainers() {
        drumPanel = new DrumPanel();
        frame.setSize(new Dimension(startingWidth, startingHeight));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    private static void buildComponents() {
        buildMenuBar();
        buildToolBar();
    }

    private static void buildMenuBar() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu setMenu = new JMenu("Set");
        JMenu viewMenu = new JMenu("View");
        JMenu connectionsMenu = new JMenu("Connections");


        // Build file menu
        addMenuItem(fileMenu, "Exit", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                serialConnection.closeSerialPort();
                System.exit(0);
            }
        });

        // Build set menu
        addMenuItem(setMenu, "Show Mixer", null);
        addMenuItem(setMenu, "Add Drum", null);
        addMenuItem(setMenu, "Snare", null);
        addMenuItem(setMenu, "Kick", null);
        addMenuItem(setMenu, "Manage set...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                benchmark(new Benchmark() {
                    public void doWork() {
                        toggleSetManagementMode();
                    }
                    public String getName() {
                        return "toggleSetManagementMode()";
                    }
                });
            }
        });
        addMenuItem(setMenu, "Reset Set", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drumPanel.resetDrumWidgetCoordinates();
                drumPanel.resetDrumWidgetZOrders();
            }
        });

        // Build connections menu
        final JMenu serialPortMenu = new JMenu("Serial Port");
        List<CommPortIdentifier> portList = serialConnection.getPortList();
        for (CommPortIdentifier portID : portList) {
            final String name = portID.getName();
            final JCheckBoxMenuItem checkBoxMenuItem = new JCheckBoxMenuItem(name);
            addMenuItem(serialPortMenu, checkBoxMenuItem, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Set port identifier
                    serialConnection.closeSerialPort();
                    for (int i = serialPortMenu.getItemCount()-1; i >= 0; i--) {
                        JCheckBoxMenuItem currentItem = (JCheckBoxMenuItem)serialPortMenu.getItem(i);
                        if (currentItem != checkBoxMenuItem) {
                            currentItem.setState(false);
                        }
                    }
                    serialConnection.setPortIdentifier(name);
                    checkBoxMenuItem.setState(true);
                }
            });
        }
        connectionsMenu.add(serialPortMenu);

        addMenuItem(connectionsMenu, "Stroke Graph...", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graphDialog = new GraphDialog(frame);
            }
        });

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(setMenu);
        menuBar.add(viewMenu);
        menuBar.add(connectionsMenu);
    }
    /*
    protected static void requestGraphMode(boolean value) {
        serialConnection.requestGraphMode(value);
    }
    */
    private static void addMenuItem(JMenu menu, JMenuItem menuItem, ActionListener listener) {
        menuItem.addActionListener(listener);
        menu.add(menuItem);
    }

    private static void addMenuItem(JMenu menu, String menuItemText, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(menuItemText);
        addMenuItem(menu, menuItem, listener);
    }

    private static void toggleSetManagementMode() {
        setManagementMode = !setManagementMode;
        //setManagementMode ^= true; is another way to toggle a boolean
        int cursorType;
        if (setManagementMode) {
            drumPanel.addMouseMotionListeners();
            cursorType = Cursor.HAND_CURSOR;
        } else {
            drumPanel.removeMouseMotionListeners();
            cursorType = Cursor.DEFAULT_CURSOR;
        }
        frame.setCursor(new Cursor(cursorType));
    }

    protected static void bringForward(DrumWidget target) {
        drumPanel.bringForward(target);
    }
    protected static void bringToFront(DrumWidget target) {
        drumPanel.bringToFront(target);
    }
    protected static void sendBackward(DrumWidget target) {
        drumPanel.sendBackward(target);
    }
    protected static void sendToBack(DrumWidget target) {
        drumPanel.sendToBack(target);
    }
    protected static void benchmark(Benchmark benchmark) {
        final int multiplier = 1;
        long start = System.nanoTime();
        for (int i = 0; i < multiplier; i++) {
            benchmark.doWork();
        }
        long end = System.nanoTime();
        logger.log(Level.INFO, "Completed " + benchmark.getName() + " in " +
                               ((end - start)/(1000*multiplier)) + 
                               " microseconds");
    }

    private static void buildToolBar() {
        toolbar = new JToolBar();
        JButton currentToolbarButton = new JButton("1");
        toolbar.add(currentToolbarButton);
        currentToolbarButton = new JButton("2");
        toolbar.add(currentToolbarButton);
    }


    private static DrumWidget createDrumWidget(String name,
                                               DrumArticulationMap dam,
                                               String imageFilename,
                                               int x, int y) {
        return new DrumWidget(name, dam, "img/sd/" + imageFilename,
                              x, y, drumPanel);
    }


    private static void addComponentsToContainers() {
        /* Assumes all components and containers have been built and
           configured, aside from the drum widgets
         */
        frame.setJMenuBar(menuBar);
        frame.setContentPane(drumPanel);
        drumPanel.addDrumWidget(
           createDrumWidget("Crash 4", midiKit.cymbal4,
                            "crash_1.png", 57, -194)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Crash 5", midiKit.cymbal5,
                             "crash_2.png", 150, -149)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Crash 2", midiKit.cymbal2,
                             "crash_2.png", -184, -171)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Crash 3", midiKit.cymbal3,
                             "crash_2.png", -53, -198)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("China", midiKit.cymbal1,
                             "china.png", -236, -74)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Ride 1", midiKit.ride1,
                             "ride_3.png", -209, 27)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Ride 4", midiKit.ride4,
                             "ride_2.png", 274, -94)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Ride 3", midiKit.ride3,
                            "ride_1.png", 194, -58)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Crash 6", midiKit.cymbal6,
                             "crash_1.png", 215, 54)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Ride 2", midiKit.ride2,
                             "ride_1.png", -158, -148)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Hi-Hat", midiKit.hi_hat,
                            "hat.png", -155, -48)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Floor 2", midiKit.floorTom2,
                             "tom_5.png", 136, 81)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Floor 1", midiKit.floorTom1,
                             "tom_4.png", 114, -13)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Rack 3", midiKit.rackTom3,
                             "tom_3.png", 60, -90)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Rack 2", midiKit.rackTom2,
                            "tom_2.png", -14, -111)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Snare", midiKit.snare,
                            "snare.png", -99, -21)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Rack 1", midiKit.rackTom1,
                            "tom_1.png", -79, -96)
        );
        drumPanel.addDrumWidget(
            createDrumWidget("Cowbell", midiKit.cowbell,
                             "cowbell.png", 31, -18)
        );
        drumPanel.addDrumWidget(
           createDrumWidget("Kick", midiKit.kick,
                            "kick.png", 0, 0)
        );
    }

    private static void addContainersToFrame() {
        //frame.add(mainContentPanel);
    }

    private static void showGUI() {
        frame.setVisible(true);
    }
}



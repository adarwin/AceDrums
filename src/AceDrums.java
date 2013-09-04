/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.util.HashMap;
import de.humatic.mmj.MidiSystem;
import de.humatic.mmj.MidiOutput;
//import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class AceDrums {

    private static final Logger logger = Logger.getLogger(
                                                    AceDrums.class.getName());
    private static int startingWidth = 800;
    private static int startingHeight = 600;
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
    protected static byte BASS_DRUM_2 = 35;
    protected static byte BASS_DRUM_1 = 36;
    protected static byte SIDE_STICK = 37;
    protected static byte SNARE_DRUM_1 = 38;
    protected static byte HAND_CLAP = 39;
    protected static byte SNARE_DRUM_2 = 40;
    protected static byte LOW_TOM_2 = 41;
    protected static byte CLOSED_HIHAT = 42;
    protected static byte LOW_TOM_1 = 43;
    protected static byte PEDAL_HIHAT = 44;
    protected static byte MID_TOM_2 = 45;
    protected static byte OPEN_HIHAT = 46;
    protected static byte MID_TOM_1 = 47;
    protected static byte HIGH_TOM_2 = 48;
    protected static byte CRASH_CYMBAL_1 = 49;
    protected static byte HIGH_TOM_1 = 50;
    protected static byte RIDE_CYMBAL_1 = 51;
    protected static byte CHINESE_CYMBAL = 52;
    protected static byte RIDE_BELL = 53;
    protected static byte TAMBOURINE = 54;
    protected static byte SPLASH_CYMBAL = 55;
    protected static byte COWBELL = 56;
    protected static byte CRASH_CYMBAL_2 = 57;
    protected static byte VIBRA_SLAP = 58;
    protected static byte RIDE_CYMBAL_2 = 59;
    private static boolean DEBUG = true;

    public static void main(String[] args) {
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
                createAndShowGUI();
            }
        });
    }


    public static boolean getSetManagementMode() {
        return setManagementMode;
    }

    public static void requestDrumWidgetRemoval(DrumWidget targetedDrum) {
        drumPanel.removeDrumWidget(targetedDrum);
    }


    public static void requestTweakDialog(DrumWidget associatedDrumWidget) {
        new TweakDialog(frame, associatedDrumWidget.drumName);
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
        MidiSystem.initMidiSystem("AceDrums", "SuperiorDrummer");
        midiOutput = MidiSystem.openMidiOutput(0);
        midiKit = new MIDIKit();
        frame = new JFrame("AceDrums");
    }

    protected static void reportStroke(byte midiID, byte velocity) {
        // Note on
        midiOutput.sendMidi(new byte[] { (byte)144, midiID, velocity });
        // Note off
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
        menuItem.addActionListener(new ActionListener() {
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
        setMenu.add(menuItem);

        menuItem = new JMenuItem("Reset Set");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                drumPanel.resetDrumWidgetCoordinates();
                drumPanel.resetDrumWidgetZOrders();
            }
        });
        setMenu.add(menuItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(setMenu);
        menuBar.add(viewMenu);
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
    static void bringForward(DrumWidget target) {
        drumPanel.bringForward(target);
    }
    static void bringToFront(DrumWidget target) {
        drumPanel.bringToFront(target);
    }
    static void sendBackward(DrumWidget target) {
        drumPanel.sendBackward(target);
    }
    static void sendToBack(DrumWidget target) {
        drumPanel.sendToBack(target);
    }
    public static void benchmark(Benchmark benchmark) {
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
        drumPanel.addDrumWidget(
           new DrumWidget("Crash 4",
                          midiKit.get(midiKit.cymbal4, MIDIKit.crash),
                          "img/sd/crash_1.png", 57, -194, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Crash 5",
                           midiKit.get(midiKit.cymbal5, MIDIKit.crash),
                           "img/sd/crash_2.png", 150, -149, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Crash 2",
                           midiKit.get(midiKit.cymbal2, MIDIKit.crash),
                           "img/sd/crash_2.png", -184, -171, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Crash 3",
                           midiKit.get(midiKit.cymbal3, MIDIKit.crash),
                           "img/sd/crash_2.png", -53, -198, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("China",
                           midiKit.get(midiKit.cymbal1, MIDIKit.crash),
                           "img/sd/china.png", -236, -74, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Ride 1",
                           midiKit.get(midiKit.ride1, MIDIKit.ride),
                           "img/sd/ride_3.png", -209, 27, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Ride 4",
                           midiKit.get(midiKit.ride4, MIDIKit.ride),
                           "img/sd/ride_2.png", 274, -94, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Ride 3",
                          midiKit.get(midiKit.ride3, MIDIKit.ride),
                          "img/sd/ride_1.png", 194, -58, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Crash 6",
                           midiKit.get(midiKit.cymbal6, MIDIKit.crash),
                           "img/sd/crash_1.png", 215, 54, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Ride 2",
                           midiKit.get(midiKit.ride2, MIDIKit.ride),
                           "img/sd/ride_1.png", -158, -148, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Hi-Hat",
                          midiKit.get(midiKit.hi_hat, MIDIKit.closed_edge),
                          "img/sd/hat.png", -155, -48, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Floor 2",
                           midiKit.get(midiKit.floorTom2, MIDIKit.center),
                           "img/sd/tom_5.png", 136, 81, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Floor 1",
                           midiKit.get(midiKit.floorTom1, MIDIKit.center),
                           "img/sd/tom_4.png", 114, -13, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Rack 3",
                           midiKit.get(midiKit.rackTom3, MIDIKit.center),
                           "img/sd/tom_3.png", 60, -90, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Rack 2",
                          midiKit.get(midiKit.rackTom2, MIDIKit.center),
                          "img/sd/tom_2.png", -14, -111, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Snare",
                          midiKit.get(midiKit.snare, MIDIKit.center),
                          "img/sd/snare.png", -99, -21, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Rack 1",
                          midiKit.get(midiKit.rackTom1, MIDIKit.center),
                          "img/sd/tom_1.png", -79, -96, drumPanel)
        );
        drumPanel.addDrumWidget(
            new DrumWidget("Cowbell",
                           midiKit.get(midiKit.cowbell, MIDIKit.hit),
                           "img/sd/cowbell.png", 31, -18, drumPanel)
        );
        drumPanel.addDrumWidget(
           new DrumWidget("Kick",
                          midiKit.get(midiKit.kick, MIDIKit.right),
                          "img/sd/kick.png", 0, 0, drumPanel)
        );
    }

    private static void addContainersToFrame() {
        //frame.add(mainContentPanel);
    }

    private static void showGUI() {
        frame.setVisible(true);
    }
}



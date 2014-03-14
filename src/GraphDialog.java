/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import java.util.logging.Logger;
import java.util.logging.Level;

class GraphDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(
                                             GraphDialog.class.getName());
    private static final long serialVersionUID = 1L;
    private GraphPanel graphPanel;
    private JPanel contentPane;
    private JPanel optionsPane;
    private JPanel eastPane, westPane;
    private JSlider thresholdSlider, sensitivitySlider, timeoutSlider;
    private JButton testButton, resetButton;
    private JComboBox<DrumSettings> drumChooser;
    //private ArrayList<Integer> drums;
    private int currentDrum, previousDrum;

    private static int SENSITIVITY_MAX = 800;
    private static int SENSITIVITY_DEFAULT = 200;
    private static int SENSITIVITY_MIN = 100;
    private static int THRESHOLD_MAX = 100;
    private static int THRESHOLD_DEFAULT = 30;
    private static int THRESHOLD_MIN = 0;
    private static int TIMEOUT_MIN = 0;
    private static int TIMEOUT_DEFAULT = 5;
    private static int TIMEOUT_MAX = 100;
    private static int SNARE = 0, KICK = 1, RACK_TOM = 2, HATS = 3;

    private final int TOP_MARGIN = 30;
    private final int BOTTOM_MARGIN = 10;
    private final int SIDE_MARGIN = 5;
    private final double WEIGHTX_1 = 1.0;
    private final double WEIGHTX_0 = 0.0;
    private final double WEIGHTY_0 = 0.0;
    private final int WIDTH_1 = 1;
    private final int WIDTH_2 = 2;
    private final int HEIGHT_1 = 1;
    private final int HEIGHT_2 = 2;
    private final int ANCHOR_CENTER = GridBagConstraints.CENTER;
    private final int ANCHOR_EAST = GridBagConstraints.EAST;
    private final int ANCHOR_WEST = GridBagConstraints.WEST;
    private final int ANCHOR_NORTH = GridBagConstraints.NORTH;
    private final int ANCHOR_NORTHEAST = GridBagConstraints.NORTHEAST;
    private final int ANCHOR_SOUTHEAST = GridBagConstraints.SOUTHEAST;
    private final int FILL_NONE = GridBagConstraints.NONE;
    private final int FILL_HORIZONTAL = GridBagConstraints.HORIZONTAL;
    private final int FILL_VERTICAL = GridBagConstraints.VERTICAL;
    private final int COL_0 = 0;
    private final int COL_1 = 1;
    private final int COL_2 = 2;
    private final int COL_3 = 3;

    public GraphDialog(JFrame frame) {
        super(frame, "Stroke Graph", false);
        setSize(new Dimension(800, 500));
        setLocationRelativeTo(frame);
        initializeVariables();
        buildContainers();
        configureLayouts();
        buildComponents();
        addComponentsToContainers();
        addWindowListeners();
        setVisible(true);
    }

    private void initializeVariables() {
        //drums = new ArrayList<Integer>();
        currentDrum = SNARE;
        previousDrum = SNARE;
        /*
        drums.add(currentDrum);
        drums.add(KICK);
        drums.add(RACK_TOM);
        drums.add(HATS);
        */
    }

    private void buildContainers() {
        contentPane = new JPanel();
        graphPanel = new GraphPanel();
        optionsPane = new JPanel();
        eastPane = new JPanel();
        westPane = new JPanel();
    }

    protected void addStrokeDatum(int type, int newDatum, int duration) {
        graphPanel.addStrokeDatum(type, newDatum, duration);
    }

    private void configureLayouts() {
        contentPane.setLayout(new BorderLayout());
        optionsPane.setLayout(new GridBagLayout());
        eastPane.setLayout(new FlowLayout());
        westPane.setLayout(new FlowLayout());

    }

    private void buildComponents() {
        testButton = new JButton("Test Button");
        resetButton = new JButton("Reset to Defaults");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                thresholdSlider.setValue(THRESHOLD_DEFAULT);
                sensitivitySlider.setValue(SENSITIVITY_DEFAULT);
                timeoutSlider.setValue(TIMEOUT_DEFAULT);
            }
        });
        thresholdSlider = new JSlider(SwingConstants.HORIZONTAL,
                                      DrumSettings.THRESHOLD_MIN,
                                      DrumSettings.THRESHOLD_MAX,
                                      DrumSettings.THRESHOLD_DEFAULT);
        thresholdSlider.setPaintTicks(true);
        thresholdSlider.setPaintLabels(true);
        thresholdSlider.setSnapToTicks(true);
        thresholdSlider.setMajorTickSpacing(10);
        thresholdSlider.setMinorTickSpacing(5);
        thresholdSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!thresholdSlider.getValueIsAdjusting()) {
                    int value = thresholdSlider.getValue();
                    DrumSettings ds = getSelectedDrumSettings();
                    ds.setThreshold(value);
                    logger.log(Level.INFO, "Sending " + value + " to the " +
                                           "Arduino as the new threshold " +
                                           "value for drum " + currentDrum);
                    AceDrums.serialConnection.setThreshold(currentDrum,
                                                           value);
                }
            }
        });

        sensitivitySlider = new JSlider(SwingConstants.HORIZONTAL,
                                        DrumSettings.SENSITIVITY_MIN,
                                        DrumSettings.SENSITIVITY_MAX,
                                        DrumSettings.SENSITIVITY_DEFAULT);
        sensitivitySlider.setPaintTicks(true);
        sensitivitySlider.setPaintLabels(true);
        sensitivitySlider.setSnapToTicks(true);
        sensitivitySlider.setMajorTickSpacing(100);
        sensitivitySlider.setMinorTickSpacing(20);
        sensitivitySlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!sensitivitySlider.getValueIsAdjusting()) {
                    int value = sensitivitySlider.getValue();
                    DrumSettings ds = getSelectedDrumSettings();
                    ds.setSensitivity(value);
                    logger.log(Level.INFO, "Sending " + value + " to the " +
                                           "Arduino as the new threshold " +
                                           "value for drum " + currentDrum);
                    AceDrums.serialConnection.setSensitivity(currentDrum,
                                                             value);
                }
            }
        });

        timeoutSlider = new JSlider(SwingConstants.HORIZONTAL,
                                    DrumSettings.TIMEOUT_MIN,
                                    DrumSettings.TIMEOUT_MAX,
                                    DrumSettings.TIMEOUT_DEFAULT);
        timeoutSlider.setPaintTicks(true);
        timeoutSlider.setPaintLabels(true);
        timeoutSlider.setSnapToTicks(true);
        timeoutSlider.setMajorTickSpacing(10);
        timeoutSlider.setMinorTickSpacing(5);
        timeoutSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!timeoutSlider.getValueIsAdjusting()) {
                    int value = timeoutSlider.getValue();
                    DrumSettings ds = getSelectedDrumSettings();
                    ds.setTimeout(value);
                    logger.log(Level.INFO, "Sending " + value + " to the " +
                                           "Arduino as the new threshold " +
                                           "value for drum " + currentDrum);
                    AceDrums.serialConnection.setTimeout(currentDrum,
                                                         value);
                    //AceDrums.setTimeout(timeoutSlider.getValue());
                }
            }
        });

        drumChooser = new JComboBox<DrumSettings>();
        drumChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DrumSettings selectedDS = getSelectedDrumSettings();
                previousDrum = currentDrum;
                currentDrum = selectedDS.getDrumIndex();//drumChooser.getSelectedIndex();
                thresholdSlider.setValue(selectedDS.getThreshold());
                sensitivitySlider.setValue(selectedDS.getSensitivity());
                timeoutSlider.setValue(selectedDS.getTimeout());
                AceDrums.serialConnection.requestGraphMode(previousDrum, false);
                AceDrums.serialConnection.requestGraphMode(currentDrum, true);
            }
        });
        drumChooser.addItem(new DrumSettings("Snare", SNARE,
                                             DrumSettings.THRESHOLD_DEFAULT,
                                             DrumSettings.SENSITIVITY_DEFAULT,
                                             DrumSettings.TIMEOUT_DEFAULT));
        drumChooser.addItem(new DrumSettings("Kick", KICK,
                                             DrumSettings.THRESHOLD_DEFAULT,
                                             DrumSettings.SENSITIVITY_DEFAULT+400,
                                             DrumSettings.TIMEOUT_DEFAULT));
        drumChooser.addItem(new DrumSettings("Hi-Hat", HATS,
                                             DrumSettings.THRESHOLD_DEFAULT,
                                             DrumSettings.SENSITIVITY_DEFAULT,
                                             DrumSettings.TIMEOUT_DEFAULT));
        drumChooser.addItem(new DrumSettings("Rack Tom", RACK_TOM,
                                             DrumSettings.THRESHOLD_DEFAULT,
                                             DrumSettings.SENSITIVITY_DEFAULT,
                                             DrumSettings.TIMEOUT_DEFAULT));
    }

    private void addComponentsToContainers() {
        contentPane.add(graphPanel, BorderLayout.CENTER);
        contentPane.add(optionsPane, BorderLayout.SOUTH);
        contentPane.add(eastPane, BorderLayout.EAST);
        contentPane.add(westPane, BorderLayout.WEST);
        //westPane.add(drumChooser);

        int row = 0;
        optionsPane.add(new JLabel("Drum", SwingConstants.RIGHT),
                        new GridBagConstraints(COL_0, row,
                                     WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_EAST,
                                     FILL_NONE,
                                     new Insets(10, 0, 10, 0),
                                     0, 0));
        optionsPane.add(drumChooser, new GridBagConstraints(COL_1, row++,
                                     WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_WEST,
                                     FILL_NONE,
                                     new Insets(10, 10, 10, 0),
                                     0, 0));
        // Add row 1
        row = addSliderToOptionsPane("Threshold", "% of each stroke velocity",
                                     thresholdSlider, row,
                                     DrumSettings.THRESHOLD_MIN + "%",
                                     DrumSettings.THRESHOLD_MAX + "%");

        // Add row 2
        row = addSliderToOptionsPane("Sensitivity", "peak input voltage value",
                                     sensitivitySlider, row,
                                     Integer.toString(DrumSettings.SENSITIVITY_MIN),
                                     Integer.toString(DrumSettings.SENSITIVITY_MAX));

        // Add row 3
        row = addSliderToOptionsPane("Timeout", "ms", timeoutSlider, row,
                                     Integer.toString(DrumSettings.TIMEOUT_MIN),
                                     Integer.toString(DrumSettings.TIMEOUT_MAX));

        // Add row 4
        optionsPane.add(resetButton, new GridBagConstraints(COL_1, row++,
                                     WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_EAST,
                                     FILL_NONE,
                                     new Insets(10, 0, 5, 5),
                                     0, 0));
        //optionsPane.add(resetButton, gbc);

        setContentPane(contentPane);
    }

    private int addSliderToOptionsPane(String name, String units,
                                       JSlider slider, int row,
                                       String minValue, String maxValue) {

        GridBagConstraints gbc;
        /*
        gbc = new GridBagConstraints(COL_1, row++, WIDTH_2, HEIGHT_1,
                                     WEIGHTX_1, WEIGHTY_0,
                                     ANCHOR_CENTER,
                                     FILL_NONE,
                                     new Insets(TOP_MARGIN, 0, 0, 0),
                                     0, 0);
        */
        gbc = new GridBagConstraints(COL_0, row, WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_SOUTHEAST,
                                     FILL_NONE,
                                     new Insets(0, 10, 0, 0),
                                     0, 0);
        optionsPane.add(new JLabel(name, SwingConstants.RIGHT), gbc);

        gbc = new GridBagConstraints(COL_1, row, WIDTH_1, HEIGHT_2,
                                     WEIGHTX_1, WEIGHTY_0,
                                     ANCHOR_CENTER,
                                     FILL_HORIZONTAL,
                                     new Insets(0, 10, 10, 10),
                                     0, 0);
        optionsPane.add(slider, gbc);

        gbc = new GridBagConstraints(COL_0, ++row, WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_NORTHEAST,
                                     FILL_NONE,
                                     new Insets(0, 10, 0, 0),
                                     0, 0);
        optionsPane.add(new JLabel("(" + units + ")", SwingConstants.RIGHT), gbc);
        /*
        gbc = new GridBagConstraints(COL_0, row, WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_EAST,
                                     FILL_NONE,
                                     new Insets(0, 0, 0, SIDE_MARGIN),
                                     10, 0);
        optionsPane.add(new JLabel(minValue, SwingConstants.RIGHT),
                        gbc);
        */


        /*
        gbc = new GridBagConstraints(COL_3, row, WIDTH_1, HEIGHT_1,
                                     WEIGHTX_0, WEIGHTY_0,
                                     ANCHOR_WEST,
                                     FILL_NONE,
                                     new Insets(0, SIDE_MARGIN, 0, 0),
                                     10, 0);
        optionsPane.add(new JLabel(maxValue, SwingConstants.LEFT),
                        gbc);
        */
        return ++row;
    }


    private void addToOptionsPane(JComponent component, int x, int y,
                                  int width, int height, double weightx,
                                  int anchor, int fill, int ipadx, int ipady,
                                  Insets insets) {
        double weighty = 0.0;
        GridBagConstraints gbc = new GridBagConstraints(x, y, width, height,
                                                        weightx, weighty,
                                                        anchor, fill, insets,
                                                        ipadx, ipady);
        optionsPane.add(component, gbc);
    }


    private void constructContentPane() {
    }

    private void addWindowListeners() {
        addWindowListener(new WindowListener() {
            public void windowActivated(WindowEvent e) {
                AceDrums.serialConnection.requestGraphMode(currentDrum, true);
            }
            public void windowClosed(WindowEvent e) { }
            public void windowClosing(WindowEvent e) { }
            public void windowDeactivated(WindowEvent e) {
                AceDrums.serialConnection.requestGraphMode(currentDrum, true);
            }
            public void windowDeiconified(WindowEvent e) { }
            public void windowIconified(WindowEvent e) { }
            public void windowOpened(WindowEvent e) { }
        });
    }

    private DrumSettings getSelectedDrumSettings() {
        return (DrumSettings)drumChooser.getSelectedItem();
    }
}

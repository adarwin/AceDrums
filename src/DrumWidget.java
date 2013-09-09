/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The DrumWidet class defines a custom JComponent to be used for displaying
 * various drums.
 * 
 * @author Andrew Darwin
 */
public class DrumWidget extends JComponent {
    protected static final Logger logger = Logger.getLogger(
                                                  DrumWidget.class.getName());
    private static final long serialVersionUID = 1L;
    private JPopupMenu rightClickMenu;
    private int xOffsetFromCenter, yOffsetFromCenter;
    private int defaultX, defaultY;
    private int originalZOrder;
    private int mouseClickX, mouseClickY;
    private BufferedImage image;
    private int imageWidth, imageHeight;
    private MouseMotionListener mouseMotionListener;
    private DrumPanel drumPanel;
    private Color background;
    private String imageDirectory, imageFileName;
    private byte midiID;
    /** Fild Description */
    final String drumName;

    /**
     * This constructor takes stuff
     *
     * @param drumName This is a string
     * @param imagePath This is a string also
     * @param xOffsetFromCenter This is an integer
     * @param yOffsetFromCenter This is also an integer
     * @param drumPanel This is a reference to the DrumPanel object containing
     *                  this DrumWidget
     */
    public DrumWidget(String drumName,
                      DrumArticulationMap drumArticulationMap,
                      String imagePath, int xOffsetFromCenter,
                      int yOffsetFromCenter, DrumPanel drumPanel) {
        super();
        this.drumName = drumName;
        this.midiID = drumArticulationMap.getArticulationMIDI();
        //this.midiID = MIDIKit.get(drumArticulationMap, defaultArticulation);
        this.xOffsetFromCenter = xOffsetFromCenter;
        this.yOffsetFromCenter = yOffsetFromCenter;
        defaultX = xOffsetFromCenter;
        defaultY = yOffsetFromCenter;
        this.drumPanel = drumPanel;
        imageDirectory = imagePath.substring(0, imagePath.lastIndexOf('/'));
        imageFileName = imagePath.substring(imagePath.lastIndexOf('/') + 1,
                                            imagePath.length());
        loadImage(imagePath);
        background = getBackground();
        buildRightClickMenu(drumArticulationMap);
        setComponentPopupMenu(rightClickMenu);
        addListeners();
    }

    /**
     * This method is intended to be called only once
     *
     * @param zOrder Input Integer
     */
    protected void setZOrder(int zOrder) {
        this.originalZOrder = zOrder;
    }

    /**
     * Returns the original z order
     *
     * @return This method returns the original zOrder
     */
    protected int getOriginalZOrder() { return originalZOrder; }

    /**
     * Resets coordinates to the default x and y that were passed into the
     * constructor
     */
    protected void resetCoordinates() {
        xOffsetFromCenter = defaultX;
        yOffsetFromCenter = defaultY;
    }


    /**
     * Builds the right click menu
     */
    protected void buildRightClickMenu(DrumArticulationMap dam) {
        rightClickMenu = new JPopupMenu();
        JMenuItem drumMenuItem = new JMenuItem("Tweak " + drumName + "...");
        drumMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point location = rightClickMenu.getLocation();
                AceDrums.requestTweakDialog(DrumWidget.this);
            }
        });
        rightClickMenu.add(drumMenuItem);

        JMenuItem bringForward = new JMenuItem("Bring Forward");
        bringForward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AceDrums.bringForward(DrumWidget.this);
            }
        });
        rightClickMenu.add(bringForward);
        JMenuItem bringToFront = new JMenuItem("Bring To Front");
        bringToFront.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AceDrums.bringToFront(DrumWidget.this);
            }
        });
        rightClickMenu.add(bringToFront);
        JMenuItem sendBackward = new JMenuItem("Send Backward");
        sendBackward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AceDrums.sendBackward(DrumWidget.this);
            }
        });
        rightClickMenu.add(sendBackward);
        JMenuItem sendToBack = new JMenuItem("Send To Back");
        sendToBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AceDrums.sendToBack(DrumWidget.this);
            }
        });
        rightClickMenu.add(sendToBack);

        JMenuItem removeMenuItem = new JMenuItem("Remove");
        removeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AceDrums.requestDrumWidgetRemoval(DrumWidget.this);
            }
        });
        rightClickMenu.add(removeMenuItem);
        
        JMenuItem nextMIDI = new JMenuItem("Next MIDI Value");
        nextMIDI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int previous = (int)midiID;
                midiID = ++midiID;
                System.out.println("Changed from " + previous + " to " +
                                   midiID);
            }
        });
        rightClickMenu.add(nextMIDI);
        JMenuItem previousMIDI = new JMenuItem("Previous MIDI Value");
        previousMIDI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int previous = (int)midiID;
                midiID = --midiID;
                System.out.println("Changed from " + previous + " to " +
                                   midiID);
            }
        });
        rightClickMenu.add(previousMIDI);

        if (dam != null) {
            ArticulationMenu articulationMenu = new ArticulationMenu("Articulation", dam);
            rightClickMenu.add(articulationMenu);
        }



        rightClickMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {
                drumPanel.setSelected(DrumWidget.this, false);
            }
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                drumPanel.setSelected(DrumWidget.this, false);
            }
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                drumPanel.setSelected(DrumWidget.this, true);
            }
        });
    }

    /**
     * Adds listeners to this DrumWidget
     */
    protected void addListeners() {
        mouseMotionListener = new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                int newX = e.getX();
                int newY = e.getY();
                int deltaX = newX-mouseClickX;
                int deltaY = newY-mouseClickY;
                xOffsetFromCenter += deltaX;
                yOffsetFromCenter += deltaY;
                repaint();
            }
            public void mouseMoved(MouseEvent e) { }
        };
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                int button = e.getButton();
                mouseClickX = e.getX();
                mouseClickY = e.getY();
                if (button == 1) {
                    drumPanel.setSelected(DrumWidget.this, true);//!isSelected());
                    AceDrums.reportStroke(midiID, (byte)127);
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == 1) {
                    drumPanel.setSelected(DrumWidget.this, false);//!isSelected());
                }
            }
        });
    }

    /**
     * Determines whether this DrumWidget has been selected
     */
    protected boolean isSelected() {
        return imageDirectory.endsWith("selected");
    }

    /**
     * Sets the selection value of this DrumWidget
     *
     * @param shouldBeSelected Indecates whether to set this DrumWidget as
     *                         selected or not
     */
    protected void setSelected(boolean shouldBeSelected) {
        boolean isSelected = isSelected();
        if (shouldBeSelected && !isSelected) {
            imageDirectory += "/selected";
        } else if (!shouldBeSelected && isSelected) {
            imageDirectory = imageDirectory.substring(0,
                                              imageDirectory.lastIndexOf('/'));
        }
        loadImage(imageDirectory + "/" + imageFileName);
        repaint();
    }

    /**
     * Adds the previously defined MouseMotionListener to this DrumWidget
     */
    protected void addMouseMotionListener() {
        addMouseMotionListener(mouseMotionListener);
    }

    /**
     * Removes the predefined MouseMotionListener
     */
    protected void removeMouseMotionListener() {
        removeMouseMotionListener(mouseMotionListener);
    }

    /**
     * Shows the right click menu at the given coordinates
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    protected void showRightClickMenu(int x, int y) {
        System.out.println("Show Rightclick menu");
        rightClickMenu.show(this, x, y);
    }


    /**
     * Overrides JComponent's paintComponent() method
     * @param g A Graphics object
     */
    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
           g.fillOval(0, 0, getWidth(), getHeight());
        } else {
            g.drawImage(image, 0, 0, imageWidth, imageHeight,
                        background, null);
        }
    }

    /**
     * Gets the x offset from center
     */
    protected int getXOffsetFromCenter() { return xOffsetFromCenter; }

    /**
     * Sets the x offset from center
     * @param offset
     */
    protected void setXOffsetFromCenter(int offset) {
        xOffsetFromCenter = offset;
    }

    /**
     * Gets the y offset from center
     */
    protected int getYOffsetFromCenter() { return yOffsetFromCenter; }

    /**
     * Sets the y offset from center
     * @param offset
     */
    protected void setYOffsetFromCenter(int offset) {
        yOffsetFromCenter = offset;
    }

    /**
     * Gets the image width
     */
    protected int getImageWidth() { return imageWidth; }

    /**
     * Gets the image height
     */
    protected int getImageHeight() { return imageHeight; }

    /**
     * Loads the given image
     * @param imagePath Relative path to a drum image
     */
    protected void loadImage(String imagePath) {
        File imageFile = new File(imagePath);
        try {
           image = ImageIO.read(imageFile);
           imageWidth = image.getWidth();
           imageHeight = image.getHeight();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load image file");
        }
    }
}


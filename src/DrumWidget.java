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
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.util.logging.Logger;
import java.util.logging.Level;

class DrumWidget extends JComponent {
    private static final Logger logger = Logger.getLogger(
                                                  DrumWidget.class.getName());
    JPopupMenu rightClickMenu;
    int xOffsetFromCenter, yOffsetFromCenter;
    private int mouseClickX, mouseClickY;
    private BufferedImage image;
    private int imageWidth, imageHeight;
    private MouseMotionListener mouseMotionListener;
    Color background;
    final String drumName;

    DrumWidget(String drumName, String imagePath,
               int xOffsetFromCenter, int yOffsetFromCenter) {
        super();
        this.drumName = drumName;
        this.xOffsetFromCenter = xOffsetFromCenter;
        this.yOffsetFromCenter = yOffsetFromCenter;
        loadImage(imagePath);
        background = getBackground();
        buildRightClickMenu();
        setComponentPopupMenu(rightClickMenu);
        addListeners();
        //setVisible(true);
    }
    void buildRightClickMenu() {
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
    }
    void addListeners() {
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
            public void mouseClicked(MouseEvent e) {
                int button = e.getButton();
                switch (button) {
                    case 1:
                        if (AceDrums.getSetManagementMode()) {
                            logger.log(Level.INFO, "Got set management mode");
                        }
                        break;
                    case 3:
                        showRightClickMenu(e.getX(), e.getY());
                        break;
                }
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {
                mouseClickX = e.getX();
                mouseClickY = e.getY();
            }
            public void mouseReleased(MouseEvent e) {}
        });
    }
    void addMouseMotionListener() {
        addMouseMotionListener(mouseMotionListener);
    }
    void removeMouseMotionListener() {
        removeMouseMotionListener(mouseMotionListener);
    }
    void showRightClickMenu(int x, int y) {
        rightClickMenu.show(this, x, y);
    }


    protected void paintComponent(Graphics g) {
        if (image == null) {
           g.fillOval(0, 0, getWidth(), getHeight());
        } else {
            g.drawImage(image, 0, 0, imageWidth, imageHeight,
                        background, null);
        }
    }
    int getXOffsetFromCenter() { return xOffsetFromCenter; }
    void setXOffsetFromCenter(int offset) { xOffsetFromCenter = offset; }
    int getYOffsetFromCenter() { return yOffsetFromCenter; }
    void setYOffsetFromCenter(int offset) {yOffsetFromCenter = offset; }

    int getImageWidth() { return imageWidth; }
    int getImageHeight() { return imageHeight; }

    void loadImage(String imagePath) {
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


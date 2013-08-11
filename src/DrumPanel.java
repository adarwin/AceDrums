/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

class DrumPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(
                                                   DrumPanel.class.getName());
    BufferedImage image;
    int imageWidth, imageHeight;
    int width, height;
    int originX, originY;
    private ArrayList<DrumWidget> drumWidgets;
    DrumPanel() {
        super();
        File imageFile = new File("img/sd/originals/background.png");
        try {
           image = ImageIO.read(imageFile);
           imageWidth = image.getWidth();
           imageHeight = image.getHeight();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load image file");
        }
        originX = -27;
        originY = 80;
        drumWidgets = new ArrayList<DrumWidget>();
        setLayout(null);
    }
    void addMouseMotionListeners() {
        for (DrumWidget drumWidget : drumWidgets) {
            drumWidget.addMouseMotionListener();
        }
    }
    void removeMouseMotionListeners() {
        for (DrumWidget drumWidget : drumWidgets) {
            drumWidget.removeMouseMotionListener();
        }
    }
    void bringForward(DrumWidget target) {
        int targetIndex = getComponentZOrder(target);
        //int targetIndex = drumWidgets.indexOf(target);
        int newIndex = targetIndex-1;
        if (newIndex < 0) newIndex = 0;
        moveDrumWidgetToZOrder(target, newIndex);
        if (drumWidgets.indexOf(target) < targetIndex) {
            logger.log(Level.INFO, "Successfully brought drumWidget forward");
        }
    }
    void bringToFront(DrumWidget target) {
        moveDrumWidgetToZOrder(target, 0);
        if (drumWidgets.indexOf(target) == 0) {
            logger.log(Level.INFO,
                       "Successfully brought drumWidget to the front");
        }
    }
    void sendBackward(DrumWidget target) {
        int listSize = drumWidgets.size();
        int targetIndex = drumWidgets.indexOf(target);
        int newIndex = targetIndex+1;
        if (newIndex >= listSize) newIndex = listSize - 1;
        moveDrumWidgetToZOrder(target, newIndex);
        if (drumWidgets.indexOf(target) > targetIndex) {
            logger.log(Level.INFO, "Successfully sent drumWidget backward");
        }
    }
    void sendToBack(DrumWidget target) {
        moveDrumWidgetToZOrder(target, drumWidgets.size()-1);
        if (drumWidgets.indexOf(target) == drumWidgets.size()-1) {
            logger.log(Level.INFO, "Successfully sent drumWidget to back");
        }
    }
    private void moveDrumWidgetToZOrder(DrumWidget target, int newIndex) {
        //drumWidgets.remove(target);
        //drumWidgets.add(newIndex, target);
        setComponentZOrder(target, newIndex);
        repaint();
    }
    void addDrumWidget(DrumWidget drumWidget) {
        drumWidgets.add(drumWidget);
        add(drumWidget);
    }
    void removeDrumWidget(DrumWidget targetedDrumWidget) {
        remove(targetedDrumWidget);
        drumWidgets.remove(targetedDrumWidget);
        repaint();
    }
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        width = getWidth();
        height = getHeight();
        g.drawImage(image, 0, 0, width, height, null);
        int centerX = (width>>1)+originX;
        int centerY = (height>>1)+originY;
        for (DrumWidget drumWidget : drumWidgets) {
            int width = drumWidget.getImageWidth();
            int height = drumWidget.getImageHeight();
            int xOffsetFromCenter = drumWidget.getXOffsetFromCenter();
            int yOffsetFromCenter = drumWidget.getYOffsetFromCenter();
            drumWidget.setBounds(centerX+xOffsetFromCenter-(width>>1),
                                 centerY+yOffsetFromCenter-(height>>1),
                                 width, height);
        }
    }
}

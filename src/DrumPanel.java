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

/**
 * The DrumPanel class defines a custom JPanel to be used for displaying
 * various drums.
 * 
 * @author Andrew Darwin
 */
public class DrumPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(
                                                   DrumPanel.class.getName());
    private static final long serialVersionUID = 1L;
    private DrumWidget selectedDrumWidget;
    private BufferedImage image;
    private int imageWidth, imageHeight;
    private int width, height;
    private int originX, originY;
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
        g.drawString("All images copyrighted by Toontrack for use in their " +
                     "Superior Drummer application", centerX-212, height-15);
    }


    protected void resetDrumWidgetCoordinates() {
        for (DrumWidget drumWidget : drumWidgets) {
            drumWidget.resetCoordinates();
        }
        repaint();
    }


    protected void resetDrumWidgetZOrders() {
        for (DrumWidget drumWidget : drumWidgets) {
            setComponentZOrder(drumWidget, drumWidget.getOriginalZOrder());
        }
    }


    protected void setSelected(DrumWidget target, boolean shouldSetSelected) {
        target.setSelected(shouldSetSelected);
        if (selectedDrumWidget != null && selectedDrumWidget != target) {
            selectedDrumWidget.setSelected(false);
        }
        selectedDrumWidget = target;
    }


    protected void addMouseMotionListeners() {
        for (DrumWidget drumWidget : drumWidgets) {
            drumWidget.addMouseMotionListener();
        }
    }


    protected void removeMouseMotionListeners() {
        for (DrumWidget drumWidget : drumWidgets) {
            drumWidget.removeMouseMotionListener();
        }
    }


    protected void bringForward(DrumWidget target) {
        int targetIndex = getComponentZOrder(target);
        //int targetIndex = drumWidgets.indexOf(target);
        int newIndex = targetIndex-1;
        if (newIndex < 0) newIndex = 0;
        moveDrumWidgetToZOrder(target, newIndex);
        if (drumWidgets.indexOf(target) < targetIndex) {
            logger.log(Level.INFO, "Successfully brought drumWidget forward");
        }
    }


    protected void bringToFront(DrumWidget target) {
        moveDrumWidgetToZOrder(target, 0);
        if (drumWidgets.indexOf(target) == 0) {
            logger.log(Level.INFO,
                       "Successfully brought drumWidget to the front");
        }
    }


    protected void sendBackward(DrumWidget target) {
        //int listSize = drumWidgets.size();
        //int targetIndex = drumWidgets.indexOf(target);
        int componentCount = getComponentCount();
        int targetIndex = getComponentZOrder(target);
        int newIndex = targetIndex+1;
        if (newIndex >= componentCount) newIndex = componentCount - 1;
        moveDrumWidgetToZOrder(target, newIndex);
        if (drumWidgets.indexOf(target) > targetIndex) {
            logger.log(Level.INFO, "Successfully sent drumWidget backward");
        }
    }


    protected void sendToBack(DrumWidget target) {
        moveDrumWidgetToZOrder(target, getComponentCount()-1);
        if (drumWidgets.indexOf(target) == drumWidgets.size()-1) {
            logger.log(Level.INFO, "Successfully sent drumWidget to back");
        }
    }



    protected void addDrumWidget(DrumWidget drumWidget) {
        drumWidgets.add(drumWidget);
        add(drumWidget);
        drumWidget.setZOrder(getComponentZOrder(drumWidget));
    }


    protected void removeDrumWidget(DrumWidget targetedDrumWidget) {
        remove(targetedDrumWidget);
        drumWidgets.remove(targetedDrumWidget);
        repaint();
    }



    private void moveDrumWidgetToZOrder(DrumWidget target, int newIndex) {
        setComponentZOrder(target, newIndex);
        repaint();
    }
}

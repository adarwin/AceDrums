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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        width = getWidth();
        height = getHeight();
        g.drawImage(image, 0, 0, width, height, null);
        int centerX = (width>>1)+originX; // >> 1 is the same as dividing by 2
        int centerY = (height>>1)+originY;
        for (DrumWidget drumWidget : drumWidgets) {
            int width = drumWidget.getImageWidth();
            int height = drumWidget.getImageHeight();
            int xOffsetFromCenter = drumWidget.getXOffsetFromCenter();
            int yOffsetFromCenter = drumWidget.getYOffsetFromCenter();
            drumWidget.setBounds(centerX + xOffsetFromCenter - (width>>1),
                                 centerY + yOffsetFromCenter - (height>>1),
                                 width, height);
        }
    }
}

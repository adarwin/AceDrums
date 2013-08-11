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
    private BufferedImage image;
    private int imageWidth, imageHeight;
    Color background;
    String drumName;

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
        rightClickMenu.add(new JMenuItem(drumName));
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
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int button = e.getButton();
                if (button == 3) {
                    showRightClickMenu(e.getX(), e.getY());
                }
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
        });
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


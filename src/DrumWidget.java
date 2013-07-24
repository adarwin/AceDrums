/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Color;
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

 abstract class DrumWidget extends JComponent {
     private static final Logger logger = Logger.getLogger(DrumWidget.class.getName());
     JPopupMenu rightClickMenu;
     int xOffsetFromCenter, yOffsetFromCenter;
     private BufferedImage image;
     private int imageWidth, imageHeight;
     Color background;
     String drumName;

     DrumWidget(String drumName) {
         super();
         this.drumName = drumName;
         background = getBackground();
         buildRightClickMenu();
         setComponentPopupMenu(rightClickMenu);
         addListeners();
         //setVisible(true);
     }
     void buildRightClickMenu() {
         rightClickMenu = new JPopupMenu();
         rightClickMenu.add(new JMenuItem(drumName));
         //rightClickMenu.add(new JMenuItem("test"));
     }
     void addListeners() {
         addMouseListener(new MouseListener() {
             public void mouseClicked(MouseEvent e) {
                 int button = e.getButton();
                 if (button == 3) {
                     showRightClickMenu(e.getX(), e.getY());
                 }
                 System.out.println("Mouse button " + e.getButton() + " clicked!");
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
             //logger.log(Level.INFO, "Drawing Image");
             g.drawImage(image, 0, 0, imageWidth, imageHeight, background, null);
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


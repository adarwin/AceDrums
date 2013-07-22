/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

 package com.adarwin.edrum;

 import java.awt.Color;
 import java.awt.event.MouseEvent;
 import java.awt.event.MouseListener;
 import java.awt.Point;
 import java.awt.Graphics;
 import java.awt.Dimension;
 import javax.swing.JComponent;
 import javax.swing.JPopupMenu;
 import javax.swing.JMenuItem;
 import java.util.logging.Logger;
 import java.util.logging.Level;

 abstract class DrumWidget extends JComponent {
     private static final Logger logger = Logger.getLogger(DrumWidget.class.getName());
     private Point coordinates;
     JPopupMenu rightClickMenu;
     int xOffsetFromCenter, yOffsetFromCenter;

     DrumWidget() {
         super();
         buildRightClickMenu();
         setComponentPopupMenu(rightClickMenu);
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
         setVisible(true);
     }
     void buildRightClickMenu() {
         rightClickMenu = new JPopupMenu();
         //rightClickMenu.add(new JMenuItem("test"));
     }
     void showRightClickMenu(int x, int y) {
         rightClickMenu.show(this, x, y);
     }

     Point getCoordinates()                 { return coordinates; }
     void setCoordinates(Point coordinates) { this.coordinates = coordinates; }
     void setCoordinates(int x, int y)      { coordinates.setLocation(x, y); }

     protected void paintComponent(Graphics g) {
         //super.paintComponent(g);
         //g.setColor(Color.black);
         g.fillOval(0, 0, getWidth(), getHeight());
     }
     int getXOffsetFromCenter() { return xOffsetFromCenter; }
     int getYOffsetFromCenter() { return yOffsetFromCenter; }
 }


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

 abstract class DrumWidget extends JComponent {
     private Point coordinates;
     //private int height, width;
     JPopupMenu rightClickMenu;

     DrumWidget() {
         super();
         buildRightClickMenu();
         setComponentPopupMenu(rightClickMenu);
         System.out.println("Set size to 50, 50");
         setMinimumSize(new Dimension(10, 10));
         setPreferredSize(new Dimension(50, 50));
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
         rightClickMenu.add(new JMenuItem("test"));
     }
     void showRightClickMenu(int x, int y) {
         rightClickMenu.show(this, x, y);
     }

     Point getCoordinates()                 { return coordinates; }
     void setCoordinates(Point coordinates) { this.coordinates = coordinates; }
     void setCoordinates(int x, int y)      { coordinates.setLocation(x, y); }

     protected void paintComponent(Graphics g) {
         //super.paintComponent(g);
         g.setColor(Color.black);
         g.fillOval(0, 0, getWidth(), getHeight());
     }
 }


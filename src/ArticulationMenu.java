/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import java.util.logging.Logger;
import java.util.logging.Level;

class ArticulationMenu extends JMenu {
    protected static final Logger logger = Logger.getLogger(
                                             ArticulationMenu.class.getName());
    private static final long serialVersionUID = 1L;
    JCheckBoxMenuItem currentlySelectedMenuItem;

    ArticulationMenu(String name, DrumArticulationMap dam) {
        super(name);
        Set<String> keySet = dam.keySet();
        for (String key : keySet) {
            final JCheckBoxMenuItem temp = new JCheckBoxMenuItem(key + ": "
                                                   + dam.get(key));
            temp.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    requestSelection(temp);
                }
            });
            if (key == dam.getArticulation()) {
                temp.setState(true);
            }
            add(temp);
        }
    }

    void requestSelection(JCheckBoxMenuItem target) {
        currentlySelectedMenuItem.setState(false);
        target.setState(true);
        currentlySelectedMenuItem = target;
    }
}

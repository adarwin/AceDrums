/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JFrame;

public class TweakDialog extends JDialog {
    public TweakDialog(JFrame frame, String drumName) {
        super(frame, "Drum Tweaker: " + drumName, true);
        setSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

class DrumArticulationMap extends HashMap<String, Byte> {
    private static final long serialVersionUID = 1L;
    protected static final Logger logger = Logger.getLogger(
                                          DrumArticulationMap.class.getName());
    private String currentArticulation;

    DrumArticulationMap() {
        super();
    }
    DrumArticulationMap(String defaultArticulation) {
        super();
        currentArticulation = defaultArticulation;
    }

    String getArticulation() { return currentArticulation; }
    byte getArticulationMIDI() {
        return get(currentArticulation);
    }

    void setArticulation(String articulation) {
        currentArticulation = articulation;
    }
}

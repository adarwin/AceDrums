/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

public class DrumSettings {
    protected static int SENSITIVITY_MAX = 800;
    protected static int SENSITIVITY_DEFAULT = 200;
    protected static int SENSITIVITY_MIN = 100;
    protected static int THRESHOLD_MAX = 100;
    protected static int THRESHOLD_DEFAULT = 30;
    protected static int THRESHOLD_MIN = 0;
    protected static int TIMEOUT_MIN = 0;
    protected static int TIMEOUT_DEFAULT = 5;
    protected static int TIMEOUT_MAX = 100;

    private String name;
    private int threshold, sensitivity, timeout, arduinoArrayIndex;
    private boolean graphMode;

    public DrumSettings(String name, int arduinoArrayIndex, int threshold,
                        int sensitivity, int timeout) {
        this.name = name;
        this.arduinoArrayIndex = arduinoArrayIndex;
        this.threshold = threshold;
        this.sensitivity = sensitivity;
        this.timeout = timeout;
    }
    public String toString() {
        return name;
    }
    public int getThreshold() { return threshold; }
    public int getSensitivity() { return sensitivity; }
    public int getTimeout() { return timeout; }
    public int getDrumIndex() { return arduinoArrayIndex; }
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public void setSensitivity(int sensitivity) { this.sensitivity = sensitivity; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
}

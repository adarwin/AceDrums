/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

public class StrokeDatum {
    private int type, value;
    private long timePoint;
    protected static int GRAPH_STROKE = 129;
    protected static int GRAPH_DATA = 130;
    public StrokeDatum(int type, int value, long timePoint) {
        this.type = type;
        this.value = value;
        this.timePoint = timePoint;
    }
    public int getType() { return type; }
    public int getValue() { return value; }
    public long getTimePoint() { return timePoint; }
}

/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import gnu.io.PortInUseException;
import gnu.io.NoSuchPortException;
import java.util.Enumeration;
import java.io.BufferedReader;
import java.util.TooManyListenersException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

class SerialConnection {
    protected static final Logger logger = Logger.getLogger(
                                             SerialConnection.class.getName());
    private static final String validPortNames[] = {
        "/dev/tty.usbserial-A9007UX1",
        "/dev/ttyUSB0",
        "COM3"
    };
    private int dataRate;
    private SerialPort serialPort;
    private CommPortIdentifier portIdentifier;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean inGraphMode = false;

    private static int NORMAL_STROKE = 128;
    private static int GRAPH_STROKE = 129;
    private static int GRAPH_DATA = 130;


    protected boolean setPortIdentifier(String portName) {
        try {
            logger.log(Level.INFO, "Setting port identifier to " + portName);
            portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            openSerialPort();
        } catch (NoSuchPortException ex) {
            // TODO: Do something here
            logger.log(Level.SEVERE, "No such port exists");
            return false;
        }
        return true;
    }
    protected List<CommPortIdentifier> getPortList() {
        List<CommPortIdentifier> portList = new ArrayList<CommPortIdentifier>();
        @SuppressWarnings("unchecked")
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        // First, find an instance of serial port as set in portNames.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currentPortID = portEnum.nextElement();
            portList.add(currentPortID);
        }
        return portList;
    }
    protected boolean requestGraphMode(boolean value) {
        boolean output = false;
        if (outputStream != null) {
            try {
                if (value) {
                    outputStream.write(0x01);
                } else {
                    outputStream.write(0x00);
                }
                inGraphMode = value;
                output = true;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Failed to write data to serial port");
            }
        }
        return output;
    }
    private boolean openSerialPort() {
        try {
            // Open serial port and use class name for the appName
            serialPort = (SerialPort) portIdentifier.open(
                                                 this.getClass().getName(),
                                                 2000);
            logger.log(Level.INFO, "Made successfully opened portIdentifier");
            // Set port parameters
            serialPort.setSerialPortParams(dataRate, SerialPort.DATABITS_8,
                                           SerialPort.STOPBITS_1,
                                           SerialPort.PARITY_NONE);
            logger.log(Level.INFO, "Successfully set serial port params");
            // Open the streams
            inputStream = serialPort.getInputStream();
            logger.log(Level.INFO, "Got input Stream");
            outputStream = serialPort.getOutputStream();
            logger.log(Level.INFO, "Got output stream");


            // Add event listeners
            serialPort.addEventListener(new SerialPortEventListener() {
                public void serialEvent(SerialPortEvent event) {
                    if (event.getEventType() ==
                        SerialPortEvent.DATA_AVAILABLE) {
                        //logger.log(Level.INFO, "Got serial data");
                        try {
                            if (inGraphMode) {
                                int currentByte;
                                while (inputStream.available() > 0) {
                                    currentByte = inputStream.read();
                                    if (currentByte == GRAPH_STROKE) {
                                        // Read the next 3
                                        int[] data = readSerialData(inputStream, new int[3]);
                                        AceDrums.reportStroke((byte)data[0], (byte)data[1]);
                                    } else if (currentByte == GRAPH_DATA) {
                                        // Read the next 2
                                        int[] data = readSerialData(inputStream, new int[2]);
                                        AceDrums.reportNewDatum((byte)data[0]);
                                    }
                                }
                            } else {
                                int[] serialData = readSerialData(inputStream,
                                                                  new int[4]);
                                if (serialData[0] == NORMAL_STROKE) {
                                    AceDrums.reportStroke((byte)serialData[1],
                                                          (byte)serialData[2]);
                                } else if (serialData[0] == GRAPH_STROKE) {
                                    logger.log(Level.WARNING, "Encountered a "+
                                               "graph stroke when it should "+
                                               "have been a normal stroke");
                                } else if (serialData[0] == GRAPH_DATA) {
                                    logger.log(Level.WARNING, "Encountered " +
                                               "graph data when it should " +
                                               "have been a normal stroke");
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, "Failed to read data " +
                                                     "from serial port");
                        }
                    }
                }
            });
            logger.log(Level.INFO, "Added serial event listener");
            serialPort.notifyOnDataAvailable(true);
            logger.log(Level.INFO, "Set serial port to notify when data is " +
                                   "available");
        } catch (PortInUseException ex) {
            logger.log(Level.SEVERE, "Port is currently in use");
            return false;
        } catch (UnsupportedCommOperationException ex) {
            logger.log(Level.SEVERE, "Unsuppored Comm Operation. Make sure " +
                                     "you are using a valid data rate");
            return false;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception");
            return false;
        } catch (TooManyListenersException ex) {
            logger.log(Level.SEVERE, "Too many listeners");
            return false;
        }
        return true;
    }
    private int[] readSerialData(InputStream inputStream, int[] dataTargetArray) {
        int lastIndex = dataTargetArray.length - 1;
        int dataIndex = 0;
        int currentDatum;
        try {
            while (inputStream.available() > 0 && dataIndex <= lastIndex) {
                currentDatum = inputStream.read();
                if (currentDatum != 0) {
                    dataTargetArray[dataIndex++] = currentDatum;
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to read data from serial port");
        }
        return dataTargetArray;
    }
    protected void closeSerialPort() {
        logger.log(Level.INFO, "Attempting to close serial port: " +
                                serialPort);
        if (serialPort != null) {
            serialPort.removeEventListener();
            logger.log(Level.INFO, "Removed event listener from serial port: "
                                    + serialPort);
            serialPort.close();
            logger.log(Level.INFO, "Closed serial port: " + serialPort);
        } else {
            logger.log(Level.INFO, "Didn't need to close serial port: " +
                                    serialPort + " because it was null");
        }
    }
    SerialConnection() {
        dataRate = 57600;
        System.out.println("Constructing a serial connection...");
        List<CommPortIdentifier> portList = getPortList();
        // Obtain the correct port identifier

        if (portIdentifier == null) {
            // Failed to identify port
        } else {
            openSerialPort();
        }

    }
}

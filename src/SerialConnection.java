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
                        logger.log(Level.INFO, "Got serial data");
                        try {
                            if (inGraphMode) {
                                while (inputStream.available() > 0) {
                                    int datum = inputStream.read();
                                    AceDrums.reportNewDatum(datum);
                                    if (datum == 128) {
                                        AceDrums.reportNewDatum(inputStream.read());
                                    }
                                    int duration = inputStream.read();
                                    System.out.println(duration);
                                }
                            } else {
                                // We are using 3 here because that is the
                                //number of bytes per midi message.
                                byte[] rawMidiData = new byte[3];
                                inputStream.read(rawMidiData, 0, 3);
                                short[] fixedMidiData = new short[3];
                                for (byte i = 0; i < 3; i++) {
                                    if (rawMidiData[i] < 0) {
                                        fixedMidiData[i] = (short)(rawMidiData[i] - Byte.MIN_VALUE + Byte.MAX_VALUE + 1);
                                    } else {
                                        fixedMidiData[i] = rawMidiData[i];
                                    }
                                }
                                for (byte i = 0; i < 3; i++) {
                                    System.out.println(rawMidiData[i] + " --> " + fixedMidiData[i]);
                                }
                                System.out.println();
                                AceDrums.reportStroke(rawMidiData[1], rawMidiData[2]);
                            }
                        } catch (IOException ex) {
                            // TODO: Don't leave this blank
                            logger.log(Level.SEVERE, "Failed to read data from serial port");
                        }
                    }
                }
            });
            logger.log(Level.INFO, "Added serial event listener");
            serialPort.notifyOnDataAvailable(true);
            logger.log(Level.INFO, "Set serial port to notify when data is available");
        } catch (PortInUseException ex) {
            // TODO: Don't leave this blank
            logger.log(Level.SEVERE, "Port is currently in use");
            return false;
        } catch (UnsupportedCommOperationException ex) {
            // TODO: Don't leave this blank
            logger.log(Level.SEVERE, "Unsuppored Comm Operation. Make sure you are using a valid data rate");
            return false;
        } catch (IOException ex) {
            // TODO: Don't leave this blank
            logger.log(Level.SEVERE, "IO Exception");
            return false;
        } catch (TooManyListenersException ex) {
            // TODO: Don't leave this blank
            logger.log(Level.SEVERE, "Too many listeners");
            return false;
        }
        return true;
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

/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 
 #include "Drum.h"
 // Declare macros
 #define LEDPIN 13
 #define PADNUM 1
 
 #define NORMAL_STROKE 128
 #define GRAPH_STROKE 129
 #define GRAPH_DATA 130
 
 #define SET_GRAPH_MODE 131
 #define SET_THRESHOLD 132
 #define SET_SENSITIVITY 133
 #define SET_TIMEOUT 134
 


 // Define various ADC prescaler --> www.marulaberry.co.za/index.php/tutorials/code/arduino-adc/
 const unsigned char PS_16 = (1 << ADPS2);
 const unsigned char PS_32 = (1 << ADPS2) | (1 << ADPS0);
 const unsigned char PS_64 = (1 << ADPS2) | (1 << ADPS1);
 const unsigned char PS_128 = (1 << ADPS2) | (1 << ADPS1) | (1 << ADPS0);

 // Declare global variables
 Drum* snare;
 
 boolean graphMode = false;
 
 unsigned int timeout = 1000;
 
 
 
 void setup()
 {
   pinMode(LEDPIN, OUTPUT);
   Serial.begin(31250); // MIDI Baud Rate
   snare = new Drum(0);
   
  // set up the ADC
  ADCSRA &= ~PS_128;  // remove bits set by Arduino library
  
  // you can choose a prescaler from above.
  // PS_16, PS_32, PS_64 or PS_128
  ADCSRA |= PS_16;    // set our own prescaler to 16 
 }
 
 void loop()
 {
   for(int i = 0; i < PADNUM; i++) {
     snare->readNewValue();
     snare->calculateSlope();
     int currentMax = snare->getCurrentMax();
     if (graphMode &&
         snare->getTimeSinceNonZero() >= 0 &&
         (snare->hasNonZeroValue() || snare->getTimeSinceNonZero() < timeout)) {
       byte data[] = {GRAPH_DATA, snare->currentValue, snare->getDatumDuration()};
       Serial.write(data, 3);
     }
     if (snare->encounteredLegitStroke()) {
       snare->updateStrokeValues();

       digitalWrite(LEDPIN, HIGH);
       /*
       Byte 1 = type of signal
       Byte 2 = MIDI Note
       Byte 3 = Velocity
       Byte 4 = Time since last reading
       */
       if (graphMode) {
         byte data[] = {GRAPH_STROKE, snare->getArticulation(),
                        snare->getCurrentMax(), 1};//snare->getDatumDuration()};
         Serial.write(data, 4);
       } else {
         byte data[] = {NORMAL_STROKE, snare->getArticulation(),
                        snare->getCurrentMax(), snare->getDatumDuration()};
         Serial.write(data, 4);
        }
       digitalWrite(LEDPIN,LOW);
     }
   }
 }
 
 
 void serialEventRun(void) {
  if (Serial.available()) serialEvent();
 }
 
 void serialEvent() {
   byte currentByte;
   byte variable = 0, value = 0;
   /*
   Byte 1 = variable to modify
   Byte 2 = value to set variable
   */
   while (Serial.available() > 0) {
     currentByte = Serial.read();
     if (currentByte == 0 && variable != SET_TIMEOUT) {
       continue;
     }
     if (variable == 0) {
       variable = currentByte;
     } else if (value == 0) {
       value = currentByte;
     } else {
       break;
     }
   }
   if (variable == SET_GRAPH_MODE) {
     graphMode = value == 1;
   } else if (variable == SET_THRESHOLD) {
     snare->setThreshold((double)value/100);
   } else if (variable == SET_SENSITIVITY) {
     snare->setSensitivity(value*10);
   } else if (variable == SET_TIMEOUT) {
     timeout = value * 1000;
   }
 }

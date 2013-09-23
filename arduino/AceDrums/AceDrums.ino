/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 
 #include "Drum.h"
 // Declare macros
 #define LEDPIN 13
 //#define THRESHOLDPERCENTAGE .30
 #define THRESHOLD 10
 #define JUMP_THRESHOLD 50
 #define KICK 0         // pin 0
 #define SNARE_HEAD 1   // pin 1
 #define HIHAT 2        // pin 2
 #define SNARE_RIM 3    // pin 3
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
 
 int currentValue;
 int lastValue;
 int twoValuesAgo;
 int slope;
 unsigned long strokeTime;
 unsigned long previousStrokeTime;
 int strokeValue;
 int previousStrokeValue;
 boolean graphMode = false;
 
 unsigned int timeout = 1000;
 
 int sensorValue;
 int downTime;
 int previousMicros;
 long currentMicros;
 
 int snareHeadMax;
 int kickMax;
 int hiHatMax;
 int lastSnareHead;
 int lastKick;
 int lastHiHat;
 boolean kickTriggered;
 boolean snareHeadTriggered;
 boolean hiHatTriggered;
 boolean shouldTriggerKick;
 
 void setup()
 {
   pinMode(LEDPIN, OUTPUT);
   Serial.begin(31250); // MIDI Baud Rate
   snare = new Drum(0);
   downTime = 0;
   sensorValue = 0;
   kickTriggered = false;
   snareHeadTriggered = false;
   hiHatTriggered = false;
   shouldTriggerKick = false;
   
  // set up the ADC
  //ADCSRA &= ~PS_128;  // remove bits set by Arduino library
  
  // you can choose a prescaler from above.
  // PS_16, PS_32, PS_64 or PS_128
  //ADCSRA |= PS_16;    // set our own prescaler to 16 
 }
 
 void loop()
 {
   for(int i = 0; i < PADNUM; i++) {
     snare->readNewValue();
     snare->calculateSlope();
     int currentMax = snare->getCurrentMax();
     if (snare->encounteredLegitStroke()) {
       snare->updateStrokeValues();

       digitalWrite(LEDPIN, HIGH);
       if (graphMode) {
         byte data[] = {GRAPH_STROKE, snare->getArticulation(),
                        snare->getCurrentMax(), snare->getDatumDuration()};
         Serial.write(data, 4);
       } else {
         byte data[] = {NORMAL_STROKE, snare->getArticulation(),
                        snare->currentValue, snare->getDatumDuration()};
         Serial.write(data, 4);
        }
       digitalWrite(LEDPIN,LOW);
     } else if (graphMode &&
                snare->getTimeSinceNonZero() >= 0 &&
                snare->getTimeSinceNonZero() < timeout) {
       //currentValue = constrain(currentValue, 0, 200);
       //currentValue = map(currentValue, 0, 200, 0, 127);
       digitalWrite(LEDPIN, HIGH);
       byte data[] = {GRAPH_DATA, snare->currentValue, snare->getDatumDuration()};
       Serial.write(data, 3);
       digitalWrite(LEDPIN,LOW);
     }
   }
 }
 
 int report(int pin, int value)
 {
   boolean output = false;
   switch (pin)
   {
     case KICK:
       if (value > kickMax)
       {
         kickMax = value;
         //shouldTriggerKick = true;
       }
       else if (value < lastKick && !kickTriggered)
       {
         output = kickMax;
         kickTriggered = true;
       }
       else if (value < THRESHOLD)
       {
         kickMax = 0;
         kickTriggered = false;
       }
       lastKick = value;
       break;
     case SNARE_HEAD:
       if (value > snareHeadMax)
       {
         snareHeadMax = value;
       }
       else if (value < lastSnareHead && !snareHeadTriggered)
       {
         output = snareHeadMax;
         snareHeadTriggered = true;
       }
       else if (value < THRESHOLD)
       {
         snareHeadMax = 0;
         snareHeadTriggered = false;
       }
       lastSnareHead = value;
       break;
     case HIHAT:
       if (value > hiHatMax)
       {
         hiHatMax = value;
       }
       else if (value < lastHiHat && !hiHatTriggered)
       {
         output = hiHatMax;
         hiHatTriggered = true;
       }
       else if (value < THRESHOLD)
       {
         hiHatMax = 0;
         hiHatTriggered = false;
       }
       lastHiHat = value;
       break;
   }
   return output;
 }
 void serialEventRun(void) {
  if (Serial.available()) serialEvent();
 }
 void serialEvent() {
   byte currentByte;
   byte variable = 0, value = 0;
   while (Serial.available() > 0) {
     currentByte = Serial.read();
     if (currentByte == 0) {
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
     timeout = value * 100;
   }
 }

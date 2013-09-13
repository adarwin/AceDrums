/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 
 #include "Drum.h"
 // Declare macros
 #define LEDPIN 13
 #define THRESHOLDPERCENTAGE .30
 #define THRESHOLD 10
 #define JUMP_THRESHOLD 50
 #define KICK 0         // pin 0
 #define SNARE_HEAD 1   // pin 1
 #define HIHAT 2        // pin 2
 #define SNARE_RIM 3    // pin 3
 #define PADNUM 1

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
         Serial.write(128);
         Serial.write(snare->getCurrentMax());
       } else {
         byte data[] = {144, 38, snare->currentValue};
         Serial.write(data, 3);
        }
       digitalWrite(LEDPIN,LOW);
     } else if (graphMode && snare->currentValue > 0) {
       //currentValue = constrain(currentValue, 0, 200);
       //currentValue = map(currentValue, 0, 200, 0, 127);
       digitalWrite(LEDPIN, HIGH);
       Serial.write(snare->currentValue);
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
   // Put in graph mode
   //if (Serial.available() > 0) {
     if (Serial.read()) {
       graphMode = true;
     } else {
       graphMode = false;
     }
   //}   

   /*
   byte numBytes = Serial.available();
   if (numBytes > 0) {
     byte bytes[] = new byte[numBytes];
     Serial.readBytes(bytes, numBytes);
   }
   */
 }

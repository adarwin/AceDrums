/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 
 #include "Drum.h"
 #include "MIDIDrumData.h"
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
 Drum* kick;
 Drum* rackTom1;
 Drum* drums[2];
 Drum* drumCurrentlyInGraphMode;
 int lastDrumIndex = 2;
 
 boolean graphMode = false;
 
 unsigned int timeout = 1000;
 
 
 
 void setup()
 {
   pinMode(LEDPIN, OUTPUT);
   Serial.begin(31250); // MIDI Baud Rate
   drumCurrentlyInGraphMode = NULL;
   snare = new Drum(0, 10);
   snare->addArticulation(MIDIDrumData::center, 38);
   snare->addArticulation(MIDIDrumData::edge, 33);
   snare->addArticulation(MIDIDrumData::rimshot, 40);
   snare->addArticulation(MIDIDrumData::sidestick, 37);
   snare->addArticulation(MIDIDrumData::rim_only, 71);
   snare->addArticulation(MIDIDrumData::muted, 68);
   snare->addArticulation(MIDIDrumData::flams, 69);
   snare->addArticulation(MIDIDrumData::roll, 70);
   snare->addArticulation(MIDIDrumData::ruffs, 39);
   snare->addArticulation(MIDIDrumData::swirls, 67);
   
   kick = new Drum(1, 1);
   kick->addArticulation(MIDIDrumData::right, 36);
   
   rackTom1 = new Drum(2, 3);
   rackTom1->addArticulation(MIDIDrumData::center, 48);
   rackTom1->addArticulation(MIDIDrumData::rimshot, 82);
   rackTom1->addArticulation(MIDIDrumData::rim_only, 81);
   
   drums[0] = snare;
   drums[1] = kick;
   
  // set up the ADC
  ADCSRA &= ~PS_128;  // remove bits set by Arduino library
  
  // you can choose a prescaler from above.
  // PS_16, PS_32, PS_64 or PS_128
  ADCSRA |= PS_16;    // set our own prescaler to 16 
 }
 
 void loop()
 {
   Drum* drum;
   for(int i = 0; i < lastDrumIndex; i++) {
     drum = drums[i];
     drum->readNewValue();
     drum->calculateSlope();
     int currentMax = drum->getCurrentMax();
     if (drum->getGraphMode() &&
         drum->getTimeSinceNonZero() >= 0 &&
         (drum->hasNonZeroValue() || drum->getTimeSinceNonZero() < timeout)) {
       byte data[] = {GRAPH_DATA, drum->currentValue, drum->getDatumDuration()};
       Serial.write(data, 3);
     }
     if (drum->encounteredLegitStroke()) {
       drum->updateStrokeValues();

       //digitalWrite(LEDPIN, HIGH);
       /*
       Byte 1 = type of signal
       Byte 2 = MIDI Note
       Byte 3 = Velocity
       Byte 4 = Time since last reading
       */
       if (drum->getGraphMode()) {
         byte data[] = {GRAPH_STROKE, drum->getArticulation(),
                        drum->getCurrentMax(), 1};//snare->getDatumDuration()};
         Serial.write(data, 4);
       } else {
         byte data[] = {NORMAL_STROKE, drum->getArticulation(),
                        drum->getCurrentMax(), drum->getDatumDuration()};
         Serial.write(data, 4);
        }
       //digitalWrite(LEDPIN,LOW);
     }
   }
 }
 
 
 void serialEventRun(void) {
  if (Serial.available()) serialEvent();
 }
 
 void serialEvent() {
   byte currentByte;
   byte variable = 0, value = 0;
   Drum* drum = NULL;
   /*
   Byte 1 = variable to modify
   Byte 2 = drum variable applies to
   Byte 3 = value to set variable
   */
   while (Serial.available() > 0) {
     currentByte = Serial.read();
     if (currentByte == 0 && variable != SET_TIMEOUT && variable != SET_GRAPH_MODE) {
       //continue;
     }
     if (variable == 0) {
       variable = currentByte;
     } else if (drum == NULL) {
       drum = drums[currentByte];
     } else if (value == 0) {
       value = currentByte;
     } else {
       break;
     }
   }
   if (variable == SET_GRAPH_MODE) {
     drumCurrentlyInGraphMode->setGraphMode(false);
     drum->setGraphMode(value == 1);
     //graphMode = value == 1;
   } else if (variable == SET_THRESHOLD) {
     drum->setThreshold((double)value/100);
   } else if (variable == SET_SENSITIVITY) {
     drum->setSensitivity(value*10);
   } else if (variable == SET_TIMEOUT) {
     timeout = value * 1000;
   }
 }

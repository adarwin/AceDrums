/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 

// Declare macros
#define THRESHOLD 3
#define KICK 0         // pin 0
#define SNARE_HEAD 1   // pin 1
#define HIHAT 2        // pin 2
#define SNARE_RIM 3    // pin 3

// Declare global variables
int sensorValue;
int downTime;
int previousMicros;
long currentMicros;
 
 void setup()
 {
   Serial.begin(31250); // MIDI Baud Rate
   downTime = 0;
   sensorValue = 0;
 }
 
 void loop()
 {
   analogRead(SNARE_HEAD);
   sensorValue = analogRead(KICK);
   //previousMicros = currentMicros;
   currentMicros = millis();
   if (sensorValue > THRESHOLD)
   {
     Serial.println(sensorValue);
     //Serial.print(",");
     //Serial.println(currentMicros);
     downTime = 0;
   }
   else if (downTime < 50)
   {
     Serial.println(0);
     //Serial.print(",");
     //Serial.println(currentMicros);
     downTime++;
   }
 }

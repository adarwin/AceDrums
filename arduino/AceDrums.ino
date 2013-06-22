/*
  Andrew Darwin
  Senior Capstone
  Semester 1 (CSC 495)
 */
 

 // Declare macros
 #define THRESHOLD 10
 #define JUMP_THRESHOLD 50
 #define KICK 0         // pin 0
 #define SNARE_HEAD 1   // pin 1
 #define HIHAT 2        // pin 2
 #define SNARE_RIM 3    // pin 3

 // Define various ADC prescaler --> www.marulaberry.co.za/index.php/tutorials/code/arduino-adc/
 const unsigned char PS_16 = (1 << ADPS2);
 const unsigned char PS_32 = (1 << ADPS2) | (1 << ADPS0);
 const unsigned char PS_64 = (1 << ADPS2) | (1 << ADPS1);
 const unsigned char PS_128 = (1 << ADPS2) | (1 << ADPS1) | (1 << ADPS0);

 // Declare global variables
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
   Serial.begin(31250); // MIDI Baud Rate
   downTime = 0;
   sensorValue = 0;
   kickTriggered = false;
   snareHeadTriggered = false;
   hiHatTriggered = false;
   shouldTriggerKick = false;
   
  // set up the ADC
  ADCSRA &= ~PS_128;  // remove bits set by Arduino library
  
  // you can choose a prescaler from above.
  // PS_16, PS_32, PS_64 or PS_128
  ADCSRA |= PS_16;    // set our own prescaler to 16 
 }
 
 void loop()
 {
   for (int i = 0; i < 3; i++)
   {
     delayMicroseconds(800);
     sensorValue = analogRead(i);
     //currentMicros = millis();
     //int reportValue = report(i, sensorValue);
     if (sensorValue > THRESHOLD)
     {
       Serial.print(i);
       Serial.print(",");
       //Serial.print(reportValue);
       Serial.print(sensorValue); 
       Serial.println();
     }
       //downTime = 0;
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

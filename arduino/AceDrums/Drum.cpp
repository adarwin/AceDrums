#include "Drum.h"
#include "Arduino.h"

Drum::Drum(int pin) : pin(pin), currentValue(7), lastValue(0),
                      twoValuesAgo(0), threshold(5),
                      thresholdPercentage(.30), 
                      slope(0), strokeTime(0), previousStrokeTime(0),
                      strokeValue(0), previousStrokeValue(0) { }
                      
Drum::~Drum() { }

int Drum::calculateSlope() {
  previousSlope = slope;
  slope = (currentValue-twoValuesAgo)/2;
  return slope;
}

int Drum::getCurrentMax() {
  currentMax = max(twoValuesAgo, lastValue);
  currentMax = max(currentMax, currentValue);
  return currentMax;
}

void Drum::readNewValue() {
  reportNewValue(constrain(map(analogRead(pin), 0, 200, 0, 127),
                           0, 127));
  currentTime = micros();
}

void Drum::reportNewValue(int value) {
  twoValuesAgo = lastValue;
  lastValue = currentValue;
  currentValue = value;
}

bool Drum::encounteredLegitStroke() {
  return currentValue >= strokeValue*thresholdPercentage &&
         previousSlope > 0 &&
         slope <= 0 &&
         !(currentTime-strokeTime < 0 && currentMax < strokeValue );
}

void Drum::updateStrokeValues() {
  previousStrokeTime = strokeTime;
  strokeTime = currentTime;
  previousStrokeValue = strokeValue;
  strokeValue = currentMax;
}

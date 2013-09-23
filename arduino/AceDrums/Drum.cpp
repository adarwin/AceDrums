#include "Drum.h"
#include "Arduino.h"



Drum::Drum(int pin) : pin(pin), currentValue(7), lastValue(0),
                      twoValuesAgo(0), threshold(5),
                      thresholdPercentage(.30), 
                      slope(0), strokeTime(0), previousStrokeTime(0),
                      strokeValue(0), previousStrokeValue(0),
                      sensitivity(200) { }
                      
Drum::~Drum() { }

int Drum::calculateSlope() {
  previousSlope = slope;
  slope = (currentValue-twoValuesAgo)/2;
  return slope;
}

int Drum::getArticulation() {
  return CENTER;
}

int Drum::getCurrentMax() {
  currentMax = max(twoValuesAgo, lastValue);
  currentMax = max(currentMax, currentValue);
  return currentMax;
}

void Drum::readNewValue() {
  reportNewValue(constrain(map(analogRead(pin), 0, sensitivity, 0, 127),
                           0, 127));
  previousTime = currentTime;
  currentTime = micros();
}

void Drum::reportNewValue(int value) {
  twoValuesAgo = lastValue;
  lastValue = currentValue;
  currentValue = value;
  if (currentValue != 0) {
    lastNonZeroTime = currentTime;
  }
}

void Drum::setSensitivity(int value) {
  sensitivity = value;
}
void Drum::setThreshold(double value) {
  thresholdPercentage = value;
}

unsigned long Drum::getTimeSinceEnding() {
  return currentTime - endingTime;
}
unsigned long Drum::getTimeSinceNonZero() {
  return currentTime - lastNonZeroTime;
}

unsigned long Drum::getDatumDuration() {
  return currentTime - previousTime;
}

bool Drum::signalHasEnded() {
  return twoValuesAgo == 0 && lastValue == 0 && currentValue == 0;
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

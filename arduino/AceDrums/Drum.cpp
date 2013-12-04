#include "Drum.h"
#include "Arduino.h"
#include "MIDIDrumData.h"

Drum::Drum(int primaryPin, int secondaryPin,
           byte numArticulations, int dataWindowWidth) :
             primaryPin(primaryPin),
             secondaryPin(secondaryPin),
             recentData(new int[dataWindowWidth]), numberOfDataPointsToKeep(dataWindowWidth),
             indexOfCurrentDatum(numberOfDataPointsToKeep-1),
             currentValue(7), lastValue(0),
             twoValuesAgo(0), //threshold(5),
             thresholdPercentage(.30), 
             slope(0), strokeTime(0), previousStrokeTime(0),
             strokeValue(0), previousStrokeValue(0),
             sensitivity(200),
             midiDrumData(new MIDIDrumData(numArticulations)),
             graphMode(false) {
    //recentData[indexOfCurrentDatum] = 100;
}



Drum::~Drum() { delete midiDrumData; }



int Drum::calculateSlope() {
  previousSlope = slope;
  int oldestIndex = (indexOfCurrentDatum + 1 == numberOfDataPointsToKeep - 1
                                               ? 0 : indexOfCurrentDatum+1);
  slope = (recentData[indexOfCurrentDatum]-recentData[oldestIndex])/(numberOfDataPointsToKeep-1);
  //slope = (currentValue-twoValuesAgo)/2;
  return slope;
}



int Drum::getCurrentValue() { return recentData[indexOfCurrentDatum]; }



byte Drum::getArticulation() {
  return midiDrumData->getFirstMIDINote();
}



bool Drum::addArticulation(byte articulation, byte value) {
  return midiDrumData->addArticulation(articulation, value);
}



int Drum::getCurrentMax() {
  int tempMax = 0;
  for (int i = 0; i < numberOfDataPointsToKeep - 1; i++) {
      if (recentData[i] > currentMax) {
          tempMax = recentData[i];
      }
  }
  currentMax = tempMax;
  return currentMax;
  /*
  currentMax = max(twoValuesAgo, lastValue);
  currentMax = max(currentMax, currentValue);
  return currentMax;
  */
}



void Drum::setGraphMode(bool value) {
  graphMode = value;
}



bool Drum::getGraphMode() const {
  return graphMode;
}



void Drum::readNewValue() {
  reportNewValue(constrain(map(analogRead(primaryPin), 0, sensitivity, 0, 127),
                           0, 127));
  previousTime = currentTime;
  currentTime = micros();
}



void Drum::reportNewValue(int value) {
  if (indexOfCurrentDatum == numberOfDataPointsToKeep - 1) {
      indexOfCurrentDatum = 0;
      recentData[indexOfCurrentDatum] = value;
  } else {
      recentData[++indexOfCurrentDatum] = value;
  }
  /*
  if (value > 0) {
    Serial.print(value);
    Serial.print(",");
    Serial.println(indexOfCurrentDatum);
  }
  */
  /*
  twoValuesAgo = lastValue;
  lastValue = currentValue;
  currentValue = value;
  */
  if (value > 0) {
    lastNonZeroTime = currentTime;
  }
  if (getTimeSinceNonZero() > 80000) { // 80000 = 80 ms
    updateStrokeValues();
  }
}



bool Drum::hasNonZeroValue() {
  bool output = false;
  for (int i = 0; i < numberOfDataPointsToKeep - 1; i++) {
    if (recentData[i] > 0) {
        output = true;
    }
  }
  return output;
  //return twoValuesAgo > 0 || lastValue > 0 || currentValue > 0;
}



void Drum::setSensitivity(int value) {
  sensitivity = value;
}



void Drum::setThreshold(double value) {
  thresholdPercentage = value;
}



/*
unsigned long Drum::getTimeSinceEnding() {
  return currentTime - endingTime;
}
*/
unsigned long Drum::getTimeSinceNonZero() const {
  return currentTime - lastNonZeroTime;
}



unsigned long Drum::getDatumDuration() const {
  return currentTime - previousTime;
}



bool Drum::signalHasEnded() const {
  bool output = true;
  for (int i = 0; i < numberOfDataPointsToKeep - 1; i++) {
      if (recentData[i] != 0) {
          output = false;
      }
  }
  return output;
  //return twoValuesAgo == 0 && lastValue == 0 && currentValue == 0;
}



bool Drum::encounteredLegitStroke() {
  return recentData[indexOfCurrentDatum] >= strokeValue*thresholdPercentage &&
         previousSlope > 0 &&
         slope <= 0 &&
         !(currentTime-strokeTime < 0 && currentMax < strokeValue );
}


bool Drum::encounteredLocalMaximum() {
  return true;
}



void Drum::updateStrokeValues() {
  previousStrokeTime = strokeTime;
  strokeTime = currentTime;
  previousStrokeValue = strokeValue;
  strokeValue = currentMax;
}



void Drum::setDataWindowWidth(int newWidth) {
    numberOfDataPointsToKeep = newWidth;
}



int Drum::getDataWindowWidth() const {
    return numberOfDataPointsToKeep;
}

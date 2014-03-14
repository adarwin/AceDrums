#include "Drum.h"
#include "Arduino.h"
#include "MIDIDrumData.h"

Drum::Drum(int primaryPin, int secondaryPin,
           byte numArticulations, int dataWindowWidth) :
             primaryPin(primaryPin),
             secondaryPin(secondaryPin),
             recentData(new int[dataWindowWidth]), numberOfDataPointsToKeep(dataWindowWidth),
             indexOfCurrentDatum(numberOfDataPointsToKeep-1),
             currentMaxValue(0),
             thresholdPercentage(.30), 
             slope(0), strokeTime(0), previousStrokeTime(0),
             strokeValue(0), previousStrokeValue(0),
             sensitivity(200),
             midiDrumData(new MIDIDrumData(numArticulations)),
             graphMode(false),
             previousLocalMaximum(0), currentLocalMaximum(0),
             timeOfPreviousLocalMaximum(0),
             timeOfCurrentLocalMaximum(0) {
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
  /* Check to see if we're the hi-hat. Note that this is NOT the
     correct way to do this. The hi-hat should have it's own class */
  if (secondaryPin == 4) {
    int openValue = map(analogRead(secondaryPin), 630, 670, 1, 6);
    //Serial.println(openValue);
    if (openValue < 1) {
      return midiDrumData->open_5;
    } else if (openValue > 6) {
      return midiDrumData->tight_edge;
    } else {
      switch (openValue) {
        case 1: return midiDrumData->open_5;
        case 2: return midiDrumData->open_4;
        case 3: return midiDrumData->open_3;
        case 4: return midiDrumData->open_2;
        case 5: return midiDrumData->open_1;
        case 6: return midiDrumData->closed_edge;
      }
    }
  }
  return midiDrumData->getFirstMIDINote();
}



bool Drum::addArticulation(byte articulation, byte value) {
  return midiDrumData->addArticulation(articulation, value);
}



int Drum::calculateCurrentMaxValue() const {
  int tempMax = 0;
  for (int i = 0; i < numberOfDataPointsToKeep - 1; i++) {
      if (recentData[i] > tempMax) {
          tempMax = recentData[i];
      }
  }
  return tempMax;
  /*
  currentMax = max(twoValuesAgo, lastValue);
  currentMax = max(currentMax, currentValue);
  return currentMax;
  */
}


int Drum::getCurrentMax() const {
  return currentMaxValue;
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

}



void Drum::reportNewValue(int value) {
  previousTime = currentTime;
  currentTime = micros();
  int replacedValue;
  if (indexOfCurrentDatum == numberOfDataPointsToKeep - 1) {
      indexOfCurrentDatum = 0;
      replacedValue = recentData[indexOfCurrentDatum];
      recentData[indexOfCurrentDatum] = value;
  } else {
      replacedValue = recentData[++indexOfCurrentDatum];
      recentData[indexOfCurrentDatum] = value;
  }
  // Update current max value
  updateCurrentMaxValue(replacedValue, value);
  
  // Update slopes
  calculateSlope();
  
  // Update local maximum information
  if (encounteredLocalMaximum()) {
    timeOfPreviousLocalMaximum = timeOfCurrentLocalMaximum;
    timeOfCurrentLocalMaximum = currentTime;
    previousLocalMaximum = currentLocalMaximum;
    currentLocalMaximum = currentMaxValue;
    //Serial.print("Set previous local maximum to ");
    //Serial.println(previousLocalMaximum);
  }
  
  if (value > 0) {
    lastNonZeroTime = currentTime;
  }
  if (getTimeSinceNonZero() > 80000) { // 80000 = 80 ms
    updateStrokeValues();
  }
}


void Drum::updateCurrentMaxValue(int replacedValue, int newValue) {
  // Calculate current maximum value
  // First, check to see if we might have just replaced the current maximum
  if (replacedValue == currentMaxValue) {
    // Need to recalculate across the whole data structure
    currentMaxValue = calculateCurrentMaxValue();
    //Serial.print("Recalculated max to be: ");
    //Serial.println(currentMaxValue);
  } else if (newValue > currentMaxValue) {
    currentMaxValue = newValue;
  } // else, currentMax was still the max
}



bool Drum::hasNonZeroValue() {
  bool output = false;
  for (int i = 0; i < numberOfDataPointsToKeep - 1; i++) {
    if (recentData[i] > 0) {
        output = true;
    }
  }
  return output;
}



void Drum::setSensitivity(int value) {
  sensitivity = value;
}



void Drum::setThreshold(double value) {
  thresholdPercentage = value;
}




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
}



bool Drum::encounteredLegitStroke() {
  if (encounteredLocalMaximum()) {
    /* If the previous local maximum happened very recently,
       this may not be a legit stroke */
    if (currentTime - timeOfPreviousLocalMaximum < 80000) { // Some engineering constant needs to go here
      /* Should return true if current maximum is greater than previous maximum */
      if (currentLocalMaximum > previousLocalMaximum + 5) {
        /*
        Serial.print("Higher Maximum Case: ");
        Serial.print(previousLocalMaximum);
        Serial.print(" -> ");
        Serial.println(currentLocalMaximum);
        */
        return true;
      } else {
        return false;
      }
    } else {
      // In this case, the previous maximum was a long time ago, which means this one has to be a legit stroke
      //Serial.println("Legit stroke because previous local maximum was a long time ago");
      return true;
    }
  } else {
    return false;
  }
  /*
  return recentData[indexOfCurrentDatum] >= strokeValue*thresholdPercentage &&
         previousSlope > 0 &&
         slope <= 0 &&
         !(currentTime-strokeTime < 0 && currentMaxValue < strokeValue );
  */
}


bool Drum::encounteredLocalMaximum() {
  if (previousSlope > 0 && slope <= 0) {
    /*
    Serial.print("Previous Slope: ");
    Serial.print(previousSlope);
    Serial.print(", Current Slope: ");
    Serial.println(slope);
    */
    
    //Serial.println("Local Maximum");
    return true;
  } else {
    return false;
  }
}



void Drum::updateStrokeValues() {
  previousStrokeTime = strokeTime;
  strokeTime = currentTime;
  previousStrokeValue = strokeValue;
  strokeValue = currentMaxValue;
}



void Drum::setDataWindowWidth(int newWidth) {
    numberOfDataPointsToKeep = newWidth;
}



int Drum::getDataWindowWidth() const {
    return numberOfDataPointsToKeep;
}

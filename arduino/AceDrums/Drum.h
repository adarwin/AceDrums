 // Articulation values between 1 and 25?
 #define CENTER 38
 #define EDGE 33
 #define RIMSHOT 40
 #define SIDESTICK 37
 #define RIM_ONLY 71
 #define MUTED 68
 #define FLAMS 69
 #define ROLL 70
 #define SWIRLS 67
 #define RIGHT 36
 #define CLOSED_EDGE 22
 #define CLOSED_TIP 42
 #define TIGHT_EDGE 62
 #define TIGHT_TIP 63
 #define SEQ_HARD 64
 #define SEQ_SOFT 65
 #define OPEN_1 24
 #define OPEN_2 25
 #define OPEN_3 26
 #define OPEN_4 60
 #define OPEN_5 17
 #define CLOSED_BELL 119
 #define OPEN_BELL_1 120
 #define OPEN_BELL_2 121
 #define OPEN_PEDAL 23
 #define CLOSED_PEDAL 21
 #import "Arduino.h"
 
class MIDIDrumData;
class Drum {
  public:
    Drum(int primaryPin, int secondaryPin,
         byte numArticulations, int dataWindowWidth);
    ~Drum();
    int currentValue;
    int getCurrentValue();
    int calculateSlope();
    int getCurrentMax();
    void readNewValue();
    void reportNewValue(int);
    bool encounteredLegitStroke();
    bool encounteredLocalMaximum();
    void updateStrokeValues();
    int primaryPin;
    int secondaryPin;
    bool signalHasEnded() const;
    //unsigned long getTimeSinceEnding();
    unsigned long getTimeSinceNonZero() const;
    unsigned long getDatumDuration() const;
    void setSensitivity(int);
    void setThreshold(double);
    byte getArticulation();
    bool addArticulation(byte, byte);
    bool hasNonZeroValue();
    void setGraphMode(bool);
    bool getGraphMode() const;
    void setDataWindowWidth(int);
    int getDataWindowWidth() const;
    
  protected:
    //int threshold;
    double thresholdPercentage;
    int sensitivity;
    
  private:
    MIDIDrumData* midiDrumData;
    unsigned long endingTime;
    unsigned long lastNonZeroTime;
    int* recentData; // Keep this data array as a circular array
    int numberOfDataPointsToKeep;
    int indexOfCurrentDatum;
    int lastValue;
    int twoValuesAgo;
    int slope;
    int previousSlope;
    int currentMax;
    unsigned long currentTime;
    unsigned long previousTime;
    unsigned long strokeTime;
    unsigned long previousStrokeTime;
    int strokeValue;
    int previousStrokeValue;
    bool graphMode;
};

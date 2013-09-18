class Drum {
  public:
    Drum(int);
    ~Drum();
    int currentValue;
    int calculateSlope();
    int getCurrentMax();
    void readNewValue();
    void reportNewValue(int);
    bool encounteredLegitStroke();
    void updateStrokeValues();
    int pin;
    bool signalHasEnded();
    unsigned long getTimeSinceEnding();
    unsigned long getTimeSinceNonZero();
    unsigned long getDatumDuration();
    void setSensitivity(int);
    
  protected:
    int threshold;
    double thresholdPercentage;
    int sensitivity;
    
  private:
    unsigned long endingTime;
    unsigned long lastNonZeroTime;
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

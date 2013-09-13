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
    
  protected:
    int threshold;
    double thresholdPercentage;
    
  private:
    int lastValue;
    int twoValuesAgo;
    int slope;
    int previousSlope;
    int currentMax;
    unsigned long currentTime;
    unsigned long strokeTime;
    unsigned long previousStrokeTime;
    int strokeValue;
    int previousStrokeValue;
    bool graphMode;
};

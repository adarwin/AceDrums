#include <map>
#include "Arduino.h"

class MIDIDrumData {
  public:
    MIDIDrumData(byte);
    bool addArticulation(byte, byte);
    byte getMIDINote(byte);
    byte getFirstMIDINote();
    static const byte center = 1;
    static const byte edge = 2;
    static const byte rimshot = 3;
    static const byte sidestick = 4;
    static const byte rim_only = 5;
    static const byte muted = 6;
    static const byte flams = 7;
    static const byte roll = 8;
    static const byte ruffs = 9;
    static const byte swirls = 10;
    static const byte right = 11;
    static const byte closed_edge = 12;
    static const byte closed_tip = 13;
    static const byte tight_edge = 14;
    static const byte tight_tip = 15;
    static const byte seq_hard = 16;
    static const byte seq_soft = 17;
    static const byte open_1 = 18;
    static const byte open_2 = 19;
    static const byte open_3 = 20;
    static const byte open_4 = 21;
    static const byte open_5 = 22;
    static const byte closed_bell = 23;
    static const byte open_bell_1 = 24;
    static const byte open_bell_2 = 25;
    static const byte open_pedal = 26;
    static const byte closed_pedal = 27;
    static const byte ride = 28;
    static const byte bell = 29;
    static const byte crash = 30;
    static const byte mute_hit = 31;
    static const byte mute_tail = 32;
    static const byte hit = 33;
  private:
    byte numberOfArticulations;
    byte keysIndex, valuesIndex;
    byte* keys;
    byte* values;
    //std::vector<byte> articulations;
};

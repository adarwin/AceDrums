#include "MIDIDrumData.h"

MIDIDrumData::MIDIDrumData(byte numberOfArticulations) : keysIndex(0), valuesIndex(0) {
  this->numberOfArticulations = numberOfArticulations;
  keys = new byte[numberOfArticulations];
  values = new byte[numberOfArticulations];
}
bool MIDIDrumData::addArticulation(byte articulation, byte value) {
  if (keysIndex >= numberOfArticulations ||
      valuesIndex >= numberOfArticulations) {
    return false;
  }
  keys[keysIndex++] = articulation;
  values[valuesIndex++] = value;
  return true;
}

byte MIDIDrumData::getMIDINote(byte articulation) {
  for (int i = 0; i < keysIndex; i++) {
    if (keys[i] == articulation) {
      return values[i];
    }
  }
}

byte MIDIDrumData::getFirstMIDINote() {
  return values[0];
}

/*
 * Andrew Darwin
 * www.adarwin.com
 * github.com/adarwin
 * SUNY Oswego
 */

package com.adarwin.edrum;

class MIDIKit {

    DrumArticulationMap snare;
    static String center = "Center";
    static String edge = "Edge";
    static String rimshot = "Rimshot";
    static String sidestick = "Sidestick";
    static String rim_only = "Rim Only";
    static String muted = "Muted";
    static String flams = "Flams";
    static String roll = "Roll";
    static String ruffs = "Ruffs";
    static String swirls = "*Swirls";
    static String snareTrig = "snareTrig";
    static String snareCtrl = "snareCtrl";

    DrumArticulationMap kick;
    static String right = "Right";

    DrumArticulationMap hi_hat;
    static String closed_edge = "Closed Edge";
    static String closed_tip = "Closed Tip";
    static String tight_edge = "Tight Edge";
    static String tight_tip = "Tight Tip";
    static String seq_hard = "Seq Hard";
    static String seq_soft = "Seq Soft";
    static String open1 = "Open 1";
    static String open2 = "Open 2";
    static String open3 = "Open 3";
    static String open4 = "Open 4";
    static String open5 = "Open 5";
    static String closed_bell = "Closed Bell";
    static String open_bell_1 = "Open Bell 1";
    static String open_bell_2 = "Open Bell 2";
    static String open_pedal = "Open Pedal";
    static String closed_pedal = "Closed Pedal";

    DrumArticulationMap rackTom1;
    DrumArticulationMap rackTom2;
    DrumArticulationMap rackTom3;

    DrumArticulationMap floorTom1;
    DrumArticulationMap floorTom2;
    
    DrumArticulationMap ride1;
    DrumArticulationMap ride2;
    DrumArticulationMap ride3;
    DrumArticulationMap ride4;
    static String ride = "Ride";
    static String bell = "Bell";
    // Edge already defined

    DrumArticulationMap cymbal1;
    DrumArticulationMap cymbal2;
    DrumArticulationMap cymbal3;
    DrumArticulationMap cymbal4;
    DrumArticulationMap cymbal5;
    DrumArticulationMap cymbal6;
    static String crash = "Crash";
    static String mute_hit = "Mute Hit";
    static String mute_tail = "Mute Tail";

    DrumArticulationMap cowbell;
    static String hit = "Hit";

    MIDIKit() {
        snare = new DrumArticulationMap(center);
        hi_hat = new DrumArticulationMap(closed_edge);
        kick = new DrumArticulationMap(right);
        rackTom1 = new DrumArticulationMap(center);
        rackTom2 = new DrumArticulationMap(center);
        rackTom3 = new DrumArticulationMap(center);
        floorTom1 = new DrumArticulationMap(center);
        floorTom2 = new DrumArticulationMap(center);
        ride1 = new DrumArticulationMap(ride);
        ride2 = new DrumArticulationMap(ride);
        ride3 = new DrumArticulationMap(ride);
        ride4 = new DrumArticulationMap(ride);
        cymbal1 = new DrumArticulationMap(crash);
        cymbal2 = new DrumArticulationMap(crash);
        cymbal3 = new DrumArticulationMap(crash);
        cymbal4 = new DrumArticulationMap(crash);
        cymbal5 = new DrumArticulationMap(crash);
        cymbal6 = new DrumArticulationMap(crash);
        cowbell = new DrumArticulationMap(hit);

        buildSnare();
        buildHiHat();
        buildKickAndCowbell();
        buildToms();
        buildRides();
        buildCymbals();
    }

    static byte get(DrumArticulationMap drum,
                    String articulation) {
        byte midiValue = 0;
        if (drum != null && articulation != null) {
            midiValue = drum.get(articulation);
        }
        return midiValue;
    }
        
    void buildSnare() {
        snare.put(center, (byte)38);
        snare.put(edge, (byte)33);
        snare.put(rimshot, (byte)40);
        snare.put(sidestick, (byte)37);
        snare.put(rim_only, (byte)71);
        snare.put(muted, (byte)68);
        snare.put(flams, (byte)69);
        snare.put(roll, (byte)70);
        snare.put(ruffs, (byte)39);
        snare.put(swirls, (byte)67);
    }
    void buildKickAndCowbell() {
        kick.put(right, (byte)36);
        cowbell.put(hit, (byte)56);
    }
    void buildHiHat() {
        hi_hat.put(closed_edge, (byte)22);
        hi_hat.put(closed_tip, (byte)42);
        hi_hat.put(tight_edge, (byte)62);
        hi_hat.put(tight_tip, (byte)63);
        hi_hat.put(seq_hard, (byte)64);
        hi_hat.put(seq_soft, (byte)65);
        hi_hat.put(open1, (byte)24);
        hi_hat.put(open2, (byte)25);
        hi_hat.put(open3, (byte)26);
        hi_hat.put(open4, (byte)60);
        hi_hat.put(open5, (byte)17);
        hi_hat.put(closed_bell, (byte)119);
        hi_hat.put(open_bell_1, (byte)120);
        hi_hat.put(open_bell_2, (byte)121);
        hi_hat.put(open_pedal, (byte)23);
        hi_hat.put(closed_pedal, (byte)21);
    }

    void buildToms() {
        rackTom1.put(center, (byte)48);
        rackTom1.put(rimshot, (byte)82);
        rackTom1.put(rim_only, (byte)81);

        rackTom2.put(center, (byte)47);
        rackTom2.put(rimshot, (byte)80);
        rackTom2.put(rim_only, (byte)79);

        rackTom3.put(center, (byte)45);
        rackTom3.put(rimshot, (byte)78);
        rackTom3.put(rim_only, (byte)77);

        floorTom1.put(center, (byte)43);
        floorTom1.put(rimshot, (byte)75);
        floorTom1.put(rim_only, (byte)74);

        floorTom2.put(center, (byte)41);
        floorTom2.put(rimshot, (byte)73);
        floorTom2.put(rim_only, (byte)72);
    }

    void buildRides() {
        ride1.put(ride, (byte)84);
        ride1.put(bell, (byte)85);
        ride1.put(edge, (byte)86);

        ride2.put(ride, (byte)89);
        ride2.put(bell, (byte)90);
        ride2.put(edge, (byte)91);

        ride3.put(ride, (byte)51);
        ride3.put(bell, (byte)53);
        ride3.put(edge, (byte)59);

        ride4.put(ride, (byte)113);
        ride4.put(bell, (byte)114);
        ride4.put(edge, (byte)115);
    }

    void buildCymbals() {
        cymbal1.put(crash, (byte)52);
        cymbal1.put(mute_hit, (byte)83);

        cymbal2.put(crash, (byte)49);
        cymbal2.put(mute_hit, (byte)50);

        cymbal3.put(crash, (byte)55);
        cymbal3.put(mute_hit, (byte)95);

        cymbal4.put(crash, (byte)30);
        cymbal4.put(mute_hit, (byte)106);

        cymbal5.put(crash, (byte)57);
        cymbal5.put(mute_hit, (byte)58);

        cymbal6.put(crash, (byte)32);
        cymbal6.put(mute_hit, (byte)118);
    }
}

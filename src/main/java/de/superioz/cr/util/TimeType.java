package de.superioz.cr.util;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum TimeType {

    HOURS(0),
    MINUTES(1),
    SECONDS(2);

    int i;

    TimeType(int i){
        this.i = i;
    }

    public int getIndex(){
        return i;
    }

}

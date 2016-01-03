package de.superioz.cr.common.game;

import de.superioz.library.java.util.SimpleStringUtils;

/**
 * This class was created as a part of CastleRush
 *
 * @author Superioz
 */
public enum GamePhase {

    LOBBY("lobby"),
    BUILD("build"),
    CAPTURE("capture"),
    END("end"),
    FINISH("finish");

    String n;

    GamePhase(String name){
        this.n = name;
    }

    // -- Intern methods

    public String getSpecifier(){
        return SimpleStringUtils.upperFirstLetter(n.toLowerCase());
    }

}

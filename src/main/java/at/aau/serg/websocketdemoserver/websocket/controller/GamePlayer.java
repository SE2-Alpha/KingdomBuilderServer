package at.aau.serg.websocketdemoserver.websocket.controller;

import lombok.Getter;

@Getter
public class GamePlayer {

    private final String id;

    public GamePlayer(String id) {
        this.id = id;
    }
}

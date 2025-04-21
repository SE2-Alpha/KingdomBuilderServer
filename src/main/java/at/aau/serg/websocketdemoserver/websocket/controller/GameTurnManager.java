package at.aau.serg.websocketdemoserver.websocket.controller;

import at.aau.serg.websocketdemoserver.websocket.gamedto.PlayerActionDTO;
import at.aau.serg.websocketdemoserver.websocket.state.GameTurnPhase;
import lombok.Getter;

import java.util.List;

public class GameTurnManager {

    private List<String> playerOrder;
    private int currentPlayerIndex;
    @Getter
    private GameTurnPhase currentPhase = GameTurnPhase.DRAW_TERRAIN_CARD;
    private int housesPlacedThisTurn = 0;
    private boolean usedSpecialAction = false;

    public GameTurnManager(List<String> playerIds) {
        this.playerOrder = playerIds;
    }

    public String getCurrentPlayerId(){
        return playerOrder.get(currentPlayerIndex);
    }

    public void drawCard(String playerId){
        if (!playerId.equals(getCurrentPlayerId())) {
            throw new IllegalStateException("Nicht dein Zug!");
        }
        if (currentPhase != GameTurnPhase.DRAW_TERRAIN_CARD) {
            throw new IllegalStateException("Nicht in der Kartenphase!");
        }
        currentPhase = GameTurnPhase.MAIN_PHASE;
    }

    public void performAction(String playerId, PlayerActionDTO action){
        validatePlayerAndPhase(playerId);

        switch (action.getType()) {
            case PLACE_HOUSE -> {
                if (housesPlacedThisTurn >= 3) {
                    throw new IllegalStateException("Maximal 3 Häuser erlaubt.");
                }
                housesPlacedThisTurn++;
                //Postion auf Spielfeld setzen
            }

            case SPECIAL_ACTION -> {
                if (usedSpecialAction) {
                    throw new IllegalStateException("Du hast deine Spezialaktion schon benutzt.");
                }
                usedSpecialAction = true;
                //Spezialaktion ausführen mit action.getSpecialActionId()
            }

            default -> throw new IllegalArgumentException("Unbekannte Aktion.");
        }
    }

    public void endTurn(String playerId){
        if (!playerId.equals(getCurrentPlayerId())) {
            throw new IllegalStateException("Nicht dein Zug!");
        }
        if (currentPhase != GameTurnPhase.MAIN_PHASE) {
            throw new IllegalStateException("Nicht in der Hauptphase!");
        }

        // Zug abschließen → nächster Spieler, zurück zu DRAW_CARD
        housesPlacedThisTurn = 0;
        currentPlayerIndex = (currentPlayerIndex + 1) % playerOrder.size();
        usedSpecialAction = false;
        currentPhase = GameTurnPhase.DRAW_TERRAIN_CARD;
    }

    private void validatePlayerAndPhase(String playerId) {
        if (!playerId.equals(getCurrentPlayerId())) {
            throw new IllegalStateException("Nicht dein Zug!");
        }
        if (currentPhase != GameTurnPhase.MAIN_PHASE) {
            throw new IllegalStateException("Du bist nicht in der Aktionsphase!");
        }
    }
}
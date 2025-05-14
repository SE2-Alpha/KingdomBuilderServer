package at.aau.serg.kingdombuilderserver.game;

import at.aau.serg.kingdombuilderserver.messaging.dtos.PlayerActionDTO;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@Getter
public class GameService {

    private List<String> playerOrder;
    private int currentPlayerIndex;
    private GameTurnPhase currentPhase = GameTurnPhase.DRAW_TERRAIN_CARD;
    private int housesPlacedThisTurn = 0;
    private boolean usedSpecialAction = false;

    public GameService(List<String> playerIds) {
        this.playerOrder = playerIds;
    }

    public String getCurrentPlayerId(){
        return playerOrder.get(currentPlayerIndex);
    }

    public int drawCard(String playerId) {
        if (!isCurrentPlayer(playerId) || currentPhase != GameTurnPhase.DRAW_TERRAIN_CARD) {
            return -1;
        }
        currentPhase = GameTurnPhase.MAIN_PHASE;

        Random random = new Random();
        return random.nextInt(5);
    }

    public boolean performAction(String playerId, PlayerActionDTO action) {
        if (!isCurrentPlayer(playerId) || currentPhase != GameTurnPhase.MAIN_PHASE) {
            return false;
        }
        return switch (action.getType()) {
            case PLACE_HOUSE -> placeHouse();
            case SPECIAL_ACTION -> useSpecialAction();
        };
    }

    public boolean endTurn(String playerId) {
        if (!isCurrentPlayer(playerId) || currentPhase != GameTurnPhase.MAIN_PHASE) {
            return false;
        }
        housesPlacedThisTurn = 0;
        usedSpecialAction = false;
        currentPlayerIndex = (currentPlayerIndex + 1) % playerOrder.size();
        currentPhase = GameTurnPhase.DRAW_TERRAIN_CARD;
        return true;
    }

    private boolean isCurrentPlayer(String playerId) {
        return playerId.equals(getCurrentPlayerId());
    }

    private boolean placeHouse() {
        if (housesPlacedThisTurn >= 3) {
            return false;
        }
        housesPlacedThisTurn++;
        return true;
    }

    private boolean useSpecialAction() {
        if (usedSpecialAction) {
            return false;
        }
        usedSpecialAction = true;
        return true;
    }
}
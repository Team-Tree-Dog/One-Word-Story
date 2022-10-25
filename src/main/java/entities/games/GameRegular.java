package entities.games;

import entities.Player;
import entities.ValidityChecker;
import entities.boundaries.GameEndedBoundary;
import entities.boundaries.OnTimerUpdateBoundary;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class GameRegular extends Game {

    private LinkedList<Player> players = new LinkedList<Player>();
    public GameRegular(List<Player> initialPlayers, OnTimerUpdateBoundary otub,
                       GameEndedBoundary geb) {
        super(initialPlayers, otub, geb, 15, new ValidityChecker() {
            @Override
            public boolean isValid(String word) {
                //TODO: delete this lol
                return true;
            }
        });
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    protected void onTimerUpdate() {}

    @Override
    public Player getPlayerById(String PlayerId) {
        for (Player player : this.getPlayers()) {
            if (Objects.equals(player.getPlayerId(), PlayerId)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public boolean removePlayer(Player playerToRemove) {
        return players.remove(playerToRemove);
    }

    @Override
    public boolean addPlayer(Player playerToAdd) {
        players.add(playerToAdd);
        return true;
    }

    @Override
    public boolean switchTurn() {
        players.add(players.remove());
        return true;
    }

    @Override
    public void modifyTurnTime() {
        this.setSecondsLeftInCurrentTurn(getSecondsPerTurn());
    }

    @Override
    public Player getCurrentTurnPlayer() {
        return players.peek();
    }

    @Override
    protected boolean isGameOver() {
        return players.size() == 1;
    }
}

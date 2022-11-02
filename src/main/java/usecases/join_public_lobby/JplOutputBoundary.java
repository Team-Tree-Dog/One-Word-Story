package usecases.join_public_lobby;

public interface JplOutputBoundary {

    void inPool(JplOutputDataJoinedPool dataJoinedPool);
    void inGame(JplOutputDataJoinedGame dataJoinedGame);

}

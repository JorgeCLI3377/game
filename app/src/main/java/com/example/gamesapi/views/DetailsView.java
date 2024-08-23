package com.example.gamesapi.views;

import com.example.gamesapi.models.Game;

import java.util.List;

public interface DetailsView {
    void loadFavorites(List<Game> favoriteGames);
    boolean isGameFavorite(Game game);
    void addToFavorites(Game game);
    void removeFromFavorites(Game game);
    void saveFavorites();
    void updateFavoriteIcon();
}

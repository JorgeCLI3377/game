package com.example.gamesapi.views;

import com.example.gamesapi.models.Game;
import java.util.List;

public interface FavoriteView {
    void showFavoriteGames(List<Game> favoriteGames);
    void showError(String errorMessage);
}


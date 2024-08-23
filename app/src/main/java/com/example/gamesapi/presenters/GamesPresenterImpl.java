package com.example.gamesapi.presenters;

import com.example.gamesapi.models.Game;
import com.example.gamesapi.models.RetrofitClient;
import com.example.gamesapi.views.MainView;
import java.util.List;

public class GamesPresenterImpl implements GamesPresenter {

    private MainView mainView;

    public GamesPresenterImpl(MainView mainView) {
        this.mainView = mainView;
    }

    @Override
    public void getGames() {
        mainView.showLoading();
        RetrofitClient.fetchGames(new MainView() {
            @Override
            public void showLoading() {
                mainView.showLoading();
            }

            @Override
            public void hideLoading() {
                mainView.hideLoading();
            }

            @Override
            public void showGames(List<Game> games) {
                mainView.hideLoading();
                mainView.showGames(games);
            }

            @Override
            public void showError(String message) {
                mainView.hideLoading();
                mainView.showError(message);
            }
        });
    }
}

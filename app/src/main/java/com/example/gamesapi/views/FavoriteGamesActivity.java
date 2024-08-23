package com.example.gamesapi.views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamesapi.R;
import com.example.gamesapi.models.Game;
import com.example.gamesapi.models.RetrofitClient;

import java.util.List;

public class FavoriteGamesActivity extends AppCompatActivity implements FavoriteView {

    private RecyclerView recyclerView;
    private FavoriteGamesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_games);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Carga los juegos favoritos a través de RetrofitClient
        RetrofitClient.loadFavoriteGames((FavoriteView) this); // Usa el método que acepta FavoriteView
    }

    @Override
    public void showFavoriteGames(List<Game> favoriteGames) {
        // Actualiza el adaptador con los juegos favoritos
        adapter = new FavoriteGamesAdapter(favoriteGames);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showError(String errorMessage) {
        // Muestra un mensaje de error si no se encuentran juegos favoritos
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}

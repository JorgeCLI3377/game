package com.example.gamesapi.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gamesapi.R;
import com.example.gamesapi.models.Game;
import com.example.gamesapi.models.RetrofitClient;
import com.google.gson.Gson;

import java.util.List;

public class GameDetails extends AppCompatActivity implements DetailsView {

    private ImageView gameImageView, addFavoritesImageView;
    private TextView titleTextView;
    private TextView shortDescriptionTextView;
    private TextView gameUrlTextView;
    private TextView genreTextView;
    private TextView platformTextView;
    private TextView publisherTextView;
    private TextView developerTextView;
    private TextView releaseDateTextView;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<Game> favoriteGames;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        // Referencias a las vistas
        gameImageView = findViewById(R.id.gameImageView);
        titleTextView = findViewById(R.id.titleTextView);
        shortDescriptionTextView = findViewById(R.id.shortDescriptionTextView);

        genreTextView = findViewById(R.id.genreTextView);
        platformTextView = findViewById(R.id.platformTextView);
        publisherTextView = findViewById(R.id.publisherTextView);
        developerTextView = findViewById(R.id.developerTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        addFavoritesImageView = findViewById(R.id.addfavorites);

        sharedPreferences = getSharedPreferences("favorites", Context.MODE_PRIVATE);
        gson = new Gson();

        // Cargar la lista de favoritos existente
        RetrofitClient.loadFavorites(this, this);

        Game game = getIntent().getParcelableExtra("game");

        if (game != null) {
            // Mostrar la información del juego
            titleTextView.setText(game.getTitle());
            shortDescriptionTextView.setText("Descripcion: " + game.getShortDescription());

            genreTextView.setText("Genero: " + game.getGenre());
            platformTextView.setText("Plataforma: " + game.getPlatform());
            publisherTextView.setText("Editor: " + game.getPublisher());
            developerTextView.setText("Desarrollador: " + game.getDeveloper());
            releaseDateTextView.setText("Fecha de lanzamiento: " + game.getReleaseDate());

            // Usar Glide para cargar la imagen
            Glide.with(this)
                    .load(game.getThumbnail())
                    .placeholder(R.drawable.images) // Imagen por defecto mientras carga
                    .error(R.drawable.images) // Imagen por defecto en caso de error
                    .into(gameImageView);

            isFavorite = RetrofitClient.isGameFavorite(game, favoriteGames);

            RetrofitClient.updateFavoriteIcon(isFavorite, addFavoritesImageView);

            addFavoritesImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFavorite) {
                        RetrofitClient.removeFromFavorites(game, favoriteGames, GameDetails.this);
                        Toast.makeText(GameDetails.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else {
                        RetrofitClient.addToFavorites(game, favoriteGames, GameDetails.this);
                        Toast.makeText(GameDetails.this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                    }

                    // Actualizar el estado de isFavorite después de la operación
                    isFavorite = !isFavorite;

                    // Actualizar el icono de favoritos
                    RetrofitClient.updateFavoriteIcon(isFavorite, addFavoritesImageView);
                }
            });
        } else {
            Log.e("GameDetails", "Game object is null");
        }
    }

    @Override
    public void loadFavorites(List<Game> favoriteGames) {
        this.favoriteGames = favoriteGames;
    }

    @Override
    public boolean isGameFavorite(Game game) {
        return RetrofitClient.isGameFavorite(game, favoriteGames);
    }

    @Override
    public void addToFavorites(Game game) {
        RetrofitClient.addToFavorites(game, favoriteGames, this);
    }

    @Override
    public void removeFromFavorites(Game game) {
        RetrofitClient.removeFromFavorites(game, favoriteGames, this);
    }

    @Override
    public void saveFavorites() {
        RetrofitClient.saveFavorites(favoriteGames, this);
    }

    @Override
    public void updateFavoriteIcon() {
        RetrofitClient.updateFavoriteIcon(isFavorite, addFavoritesImageView);
    }
}

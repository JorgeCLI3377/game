package com.example.gamesapi.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gamesapi.R;
import com.example.gamesapi.models.Game;
import com.example.gamesapi.models.RetrofitClient;
import com.example.gamesapi.presenters.GamesPresenter;
import com.example.gamesapi.presenters.GamesPresenterImpl;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private RecyclerView recyclerView;
    private RecyclerView carouselRecyclerView;
    private GamesPresenter gamesPresenter;
    private Handler carouselHandler;
    private ImageView img_fav;
    private ImageView img_encima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img_fav = findViewById(R.id.favo_imgv);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        carouselRecyclerView = findViewById(R.id.RecyclerCarrousel);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        if (!RetrofitClient.isInternetAvailable(this)) {
            return;
        }

        gamesPresenter = new GamesPresenterImpl(this);
        gamesPresenter.getGames();

        // Handler para el auto-scroll del carrusel
        carouselHandler = new Handler(Looper.getMainLooper());

        img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteGamesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void showLoading() {
        // Puedes agregar un indicador de carga aquí
    }

    @Override
    public void hideLoading() {
        // Ocultar el indicador de carga aquí
    }

    @Override
    public void showGames(List<Game> games) {
        // Adaptador del Grid de Juegos
        GamesAdapter gamesAdapter = new GamesAdapter(games, this);
        recyclerView.setAdapter(gamesAdapter);

        // Filtrar los primeros 50 juegos para el carrusel
        List<Game> carouselGames = games.subList(0, Math.min(50, games.size()));
        CarouselAdapter carouselAdapter = new CarouselAdapter(carouselGames, this);
        carouselRecyclerView.setAdapter(carouselAdapter);

        // Iniciar auto-scroll
        RetrofitClient.startAutoScroll(carouselRecyclerView, carouselHandler, carouselGames.size());
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

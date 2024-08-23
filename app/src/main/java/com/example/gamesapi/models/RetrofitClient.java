package com.example.gamesapi.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gamesapi.R;
import com.example.gamesapi.views.DetailsView;
import com.example.gamesapi.views.FavoriteGamesActivity;
import com.example.gamesapi.views.FavoriteView;
import com.example.gamesapi.views.MainView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static final String BASE_URL = "https://www.freetogame.com/api/";
    private static final String FAVORITES_PREFS = "favorites";
    private static final String FAVORITES_KEY = "favorite_games";
    private static Gson gson = new Gson();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static GamesApi getGamesApi() {
        return getRetrofitInstance().create(GamesApi.class);
    }


    public static void fetchGames(final MainView mainView) {
        getGamesApi().getGames().enqueue(new retrofit2.Callback<List<Game>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Game>> call, retrofit2.Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mainView.showGames(response.body());
                } else {
                    mainView.showError("Error al cargar los juegos");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Game>> call, Throwable t) {
                mainView.showError(t.getMessage());
            }
        });
    }

    public static void saveFavoriteGames(Context context, List<Game> favoriteGames) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(favoriteGames);
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }

    public static List<Game> loadFavoriteGames(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FAVORITES_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(FAVORITES_KEY, null);
        Type type = new TypeToken<ArrayList<Game>>() {}.getType();
        List<Game> favoriteGames = gson.fromJson(json, type);
        return favoriteGames != null ? favoriteGames : new ArrayList<>();
    }

    public static void loadFavoriteGames(FavoriteView favoriteView) {
        List<Game> favoriteGames = loadFavoriteGames(((FavoriteGamesActivity) favoriteView).getApplicationContext());
        if (favoriteGames != null && !favoriteGames.isEmpty()) {
            favoriteView.showFavoriteGames(favoriteGames);
        } else {
            favoriteView.showError("No se encontraron juegos favoritos");
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // Si no hay conexión a internet
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            // Mostrar un Toast indicando que no hay internet
            Toast.makeText(context, "No hay internet", Toast.LENGTH_LONG).show();

            // Crear un Handler para cerrar la aplicación después de 3 segundos
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Cerrar la aplicación
                    ((Activity) context).finish();
                }
            }, 3000); // 3000 milisegundos = 3 segundos

            return false; // No hay conexión a internet
        }

        return true; // Hay conexión a internet
    }


    public static void startAutoScroll(final RecyclerView carouselRecyclerView, final Handler carouselHandler, final int itemCount) {
        final Runnable carouselRunnable = new Runnable() {
            int carouselPosition = 0;
            @Override
            public void run() {
                if (carouselPosition == itemCount) {
                    carouselPosition = 0; // Reiniciar al primer ítem
                }
                carouselRecyclerView.smoothScrollToPosition(carouselPosition++);
                carouselHandler.postDelayed(this, 3000); // Cambiar cada 3000 ms
            }
        };
        carouselHandler.postDelayed(carouselRunnable, 3000); // Primer cambio en 3000 ms
    }



    public static void loadFavorites(Context context, DetailsView detailsView) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("favorite_games", null);
        Type type = new TypeToken<ArrayList<Game>>() {}.getType();
        List<Game> favoriteGames = gson.fromJson(json, type);

        if (favoriteGames == null) {
            favoriteGames = new ArrayList<>();
        }

        detailsView.loadFavorites(favoriteGames);
    }

    public static boolean isGameFavorite(Game game, List<Game> favoriteGames) {
        for (Game favoriteGame : favoriteGames) {
            if (favoriteGame.getId() == game.getId()) {
                return true;
            }
        }
        return false;
    }

    public static void addToFavorites(Game game, List<Game> favoriteGames, Context context) {
        favoriteGames.add(game);
        saveFavorites(favoriteGames, context);
    }

    public static void removeFromFavorites(Game game, List<Game> favoriteGames, Context context) {
        for (int i = 0; i < favoriteGames.size(); i++) {
            if (favoriteGames.get(i).getId() == game.getId()) {
                favoriteGames.remove(i);
                break;
            }
        }
        saveFavorites(favoriteGames, context);
    }

    public static void saveFavorites(List<Game> favoriteGames, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(favoriteGames);
        editor.putString("favorite_games", json);
        editor.apply();
    }

    public static void updateFavoriteIcon(boolean isFavorite, ImageView addFavoritesImageView) {
        if (isFavorite) {
            addFavoritesImageView.setImageResource(R.drawable.controlrojo); // Cambia esto al ícono de agregado
        } else {
            addFavoritesImageView.setImageResource(R.drawable.controlrojo); // Cambia esto al ícono de eliminado
        }
    }




}

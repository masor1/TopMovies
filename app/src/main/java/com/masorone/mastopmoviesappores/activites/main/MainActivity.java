package com.masorone.mastopmoviesappores.activites.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.masorone.mastopmoviesappores.R;
import com.masorone.mastopmoviesappores.activites.detail.DetailActivity;
import com.masorone.mastopmoviesappores.activites.favourite.FavouriteActivity;
import com.masorone.mastopmoviesappores.adapters.MovieAdapter;
import com.masorone.mastopmoviesappores.data.Movie;
import com.masorone.mastopmoviesappores.utils.JSONUtils;
import com.masorone.mastopmoviesappores.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwitchCompat switchCompat;
    private TextView popularityTextView;
    private TextView ratedTextView;
    private ProgressBar progressBarLoading;

    private MainViewModel mainViewModel;

    private static final int LOADER_ID = 29;
    private LoaderManager loaderManager;

    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false;

    private static String lang;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intentMain = new Intent(this, MainActivity.class);
                startActivity(intentMain);
                break;
            case R.id.itemFavourite:
                Intent intentFavourite = new Intent(this, FavouriteActivity.class);
                startActivity(intentFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return Math.max(width / 185, 2);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lang = Locale.getDefault().getLanguage();
        loaderManager = LoaderManager.getInstance(this);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        progressBarLoading = findViewById(R.id.progress_bar_loading);
        popularityTextView = findViewById(R.id.text_view_popularity);
        ratedTextView = findViewById(R.id.text_view_top_rated);
        switchCompat = findViewById(R.id.switch_sort);
        recyclerView = findViewById(R.id.recycler_view_movies);

        recyclerView.setLayoutManager(new GridLayoutManager(this, getColumnCount()));
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);

        switchCompat.setChecked(true);
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            page = 1;
            setMethodOfSort(isChecked);
        });
        switchCompat.setChecked(false);

        movieAdapter.setOnPosterClickListener(position -> {
            Movie movie = movieAdapter.getMovies().get(position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id", movie.getId());
            startActivity(intent);
        });

        movieAdapter.setOnReachEndListener(() -> {
            if (!isLoading) {
                downloadData(methodOfSort, page);
            }
        });

        LiveData<List<Movie>> moviesFromLiveData = mainViewModel.getMovies();

        moviesFromLiveData.observe(this, movies -> {
            if (page == 1) {
                movieAdapter.setMovies(movies);
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
        switchCompat.setChecked(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
        switchCompat.setChecked(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = NetworkUtils.TOP_RATED;
            popularityTextView.setTextColor(getResources().getColor(R.color.grey_200));
            ratedTextView.setTextColor(getResources().getColor(R.color.purple_200));
        } else {
            methodOfSort = NetworkUtils.POPULARITY;
            popularityTextView.setTextColor(getResources().getColor(R.color.purple_200));
            ratedTextView.setTextColor(getResources().getColor(R.color.grey_200));
        }
        downloadData(methodOfSort, 1);
    }

    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildURL(methodOfSort, page, lang);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @NotNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable @org.jetbrains.annotations.Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(() -> {
            isLoading = true;
            progressBarLoading.setVisibility(View.VISIBLE);
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull @NotNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                mainViewModel.deleteAllMovies();
                movieAdapter.clear();
            }
            for (Movie movie : movies) {
                mainViewModel.insertMovie(movie);
            }
            movieAdapter.addMovies(movies);
            page++;
        }
        isLoading = false;
        progressBarLoading.setVisibility(View.GONE);
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull @NotNull Loader<JSONObject> loader) {
    }
}
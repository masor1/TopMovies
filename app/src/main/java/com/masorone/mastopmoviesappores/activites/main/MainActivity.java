package com.masorone.mastopmoviesappores.activites.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.masorone.mastopmoviesappores.R;
import com.masorone.mastopmoviesappores.activites.detail.DetailActivity;
import com.masorone.mastopmoviesappores.activites.favourite.FavouriteActivity;
import com.masorone.mastopmoviesappores.adapters.MovieAdapter;
import com.masorone.mastopmoviesappores.data.Movie;
import com.masorone.mastopmoviesappores.utils.JSONUtils;
import com.masorone.mastopmoviesappores.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private SwitchCompat switchCompat;
    private TextView popularityTextView;
    private TextView ratedTextView;

    private MainViewModel mainViewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        popularityTextView = findViewById(R.id.text_view_popularity);
        ratedTextView = findViewById(R.id.text_view_top_rated);
        switchCompat = findViewById(R.id.switch_sort);

        recyclerView = findViewById(R.id.recycler_view_movies);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);
        switchCompat.setChecked(true);
        switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
            //TODO
        });
        LiveData<List<Movie>> moviesFromLiveData = mainViewModel.getMovies();
        moviesFromLiveData.observe(this, movies -> {
            movieAdapter.setMovies(movies);
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
        int methodOfSort;
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
        JSONObject jsonObject = NetworkUtils.getJSONFromNetwork(methodOfSort, 1);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies != null && !movies.isEmpty()) {
            mainViewModel.deleteAllMovies();
            for (Movie movie : movies) {
                mainViewModel.insertMovie(movie);
            }
        }
    }
}
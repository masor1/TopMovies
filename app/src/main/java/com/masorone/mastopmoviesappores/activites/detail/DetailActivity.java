package com.masorone.mastopmoviesappores.activites.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.masorone.mastopmoviesappores.R;
import com.masorone.mastopmoviesappores.activites.favourite.FavouriteActivity;
import com.masorone.mastopmoviesappores.activites.main.MainActivity;
import com.masorone.mastopmoviesappores.activites.main.MainViewModel;
import com.masorone.mastopmoviesappores.data.FavouriteMovie;
import com.masorone.mastopmoviesappores.data.Movie;
import com.masorone.mastopmoviesappores.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import static com.masorone.mastopmoviesappores.R.string.add_to_favourite;
import static com.masorone.mastopmoviesappores.R.string.remove_from_favourite;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private MainViewModel viewModel;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(binding.imageViewBigPoster);
        binding.textViewTitle.setText(movie.getTitle());
        binding.textViewOriginalTitle.setText(movie.getOriginalTitle());
        binding.textViewOverview.setText(movie.getOverview());
        binding.textViewReleaseDate.setText(movie.getReleaseDate());
        binding.textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();
    }

    public void onClickChangeFavourite(View view) {
        if (favouriteMovie == null) {
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, add_to_favourite, Toast.LENGTH_SHORT).show();
        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, remove_from_favourite, Toast.LENGTH_SHORT).show();
        }
        setFavourite();
    }

    private void setFavourite() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            binding.imageViewAddToFavorite.setImageResource(android.R.drawable.star_big_off);
        } else {
            binding.imageViewAddToFavorite.setImageResource(android.R.drawable.star_big_on);
        }
    }
}
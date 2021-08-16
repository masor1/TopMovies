package com.masorone.mastopmoviesappores.activites.detail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.masorone.mastopmoviesappores.R;
import com.masorone.mastopmoviesappores.activites.favourite.FavouriteActivity;
import com.masorone.mastopmoviesappores.activites.main.MainActivity;
import com.masorone.mastopmoviesappores.activites.main.MainViewModel;
import com.masorone.mastopmoviesappores.adapters.ReviewAdapter;
import com.masorone.mastopmoviesappores.adapters.TrailerAdapter;
import com.masorone.mastopmoviesappores.data.FavouriteMovie;
import com.masorone.mastopmoviesappores.data.Movie;
import com.masorone.mastopmoviesappores.data.Review;
import com.masorone.mastopmoviesappores.data.Trailer;
import com.masorone.mastopmoviesappores.databinding.ActivityDetailBinding;
import com.masorone.mastopmoviesappores.utils.JSONUtils;
import com.masorone.mastopmoviesappores.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import static com.masorone.mastopmoviesappores.R.string.add_to_favourite;
import static com.masorone.mastopmoviesappores.R.string.remove_from_favourite;

public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;
    private MainViewModel viewModel;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private TextView textViewTitle;
    private TextView textViewOriginalTitle;
    private TextView textViewOverview;
    private TextView textViewReleaseDate;
    private TextView textViewRating;

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

    @SuppressLint({"SetTextI18n", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        lang = Locale.getDefault().getLanguage();

        recyclerViewTrailers = findViewById(R.id.recycler_view_trailers);
        recyclerViewReviews = findViewById(R.id.recycler_view_reviews);
        textViewTitle = findViewById(R.id.text_view_title);
        textViewOriginalTitle = findViewById(R.id.text_view_original_title);
        textViewOverview = findViewById(R.id.text_view_overview);
        textViewReleaseDate = findViewById(R.id.text_view_release_date);
        textViewRating = findViewById(R.id.text_view_rating);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).into(binding.imageViewBigPoster);
        textViewTitle.setText(movie.getTitle());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        textViewOverview.setText(movie.getOverview());
        textViewReleaseDate.setText(movie.getReleaseDate());
        textViewRating.setText(Double.toString(movie.getVoteAverage()));
        setFavourite();
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(url -> {
            Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intentToTrailer);
        });
        reviewAdapter = new ReviewAdapter();
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), lang);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), lang);
        ArrayList<Trailer> trailers = JSONUtils.getTrailerFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);
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
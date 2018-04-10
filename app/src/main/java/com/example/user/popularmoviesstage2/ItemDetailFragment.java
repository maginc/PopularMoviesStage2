package com.example.user.popularmoviesstage2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.popularmoviesstage2.api.APIClient;
import com.example.user.popularmoviesstage2.api.APIMethods;
import com.example.user.popularmoviesstage2.model.Constants;
import com.example.user.popularmoviesstage2.model.Movie;
import com.example.user.popularmoviesstage2.model.Review;
import com.example.user.popularmoviesstage2.model.ReviewList;
import com.example.user.popularmoviesstage2.model.Video;
import com.example.user.popularmoviesstage2.model.VideoList;
import com.example.user.popularmoviesstage2.model.content.MovieContract;
import com.example.user.popularmoviesstage2.view.ReviewAdapter;
import com.example.user.popularmoviesstage2.view.TrailerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_TITLE = "title";
    public static final String ARG_ITEM_ID = "id";
    public boolean favorite;
    private APIMethods apiMethods;
    private Movie movie;
    private List<Video> videoList;
    private List<Review> reviewList;
    private RecyclerView trailerRecyclerView;
    private RecyclerView reviewRecyclerView;
    private LinearLayoutManager trailerLinearLayoutManager;
    private LinearLayoutManager reviewLinearLayoutManager;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private FloatingActionButton fab;


    TextView txtYear;
    TextView txtLength;
    TextView txtRating;
    TextView txtSynopsis;
    ImageView imagePoster;

    private String title;
    public String id;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_TITLE)) {

            title = getArguments().getString(ARG_ITEM_TITLE);
            id = getArguments().getString(ARG_ITEM_ID);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(title);
            }
        }

        apiMethods = APIClient.getClient().create(APIMethods.class);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        fab = this.getActivity().findViewById(R.id.fab);
        txtYear = rootView.findViewById(R.id.txtYear);
        txtLength = rootView.findViewById(R.id.txtLength);
        txtRating = rootView.findViewById(R.id.txtRating);
        txtSynopsis = rootView.findViewById(R.id.txtSynopsis);
        imagePoster = rootView.findViewById(R.id.imagePoster);

        trailerRecyclerView = rootView.findViewById(R.id.trailerRecyclerView);
        reviewRecyclerView = rootView.findViewById(R.id.reviewRecyclerView);

        trailerLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        reviewLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        trailerRecyclerView.setLayoutManager(trailerLinearLayoutManager);
        reviewRecyclerView.setLayoutManager(reviewLinearLayoutManager);



        if(isFavorite()){
            fab.setImageResource(android.R.drawable.star_big_on);
            }else{
            fab.setImageResource(android.R.drawable.star_big_off);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFavorite()){
                    fab.setImageResource(android.R.drawable.star_big_on);
                    addFavorite();
                } else{
                    fab.setImageResource(android.R.drawable.star_big_off);
                    removeFavorite();

                }
            }
        });

        loadData(id);
        loadTrailers(id);
        loadReviews(id);
        return rootView;
    }

    // Load movie data
    private void loadData(String id) {
        this.id = id;
        Call<Movie> call = apiMethods.getMovie(id, Constants.API_KEY);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    movie = response.body();
                    txtYear.setText(movie.getReleaseDate());
                    txtLength.setText(movie.getOriginalTitle());
                    txtRating.setText(String.valueOf(movie.getVoteAverage()) + getString(R.string.out_of_ten));
                    txtSynopsis.setText(movie.getOverview());


                    if (movie.getPosterPath() != null) {

                        Picasso.with(getActivity())
                                .load(Constants.API_POSTER_HEADER_LARGE
                                        + movie.getPosterPath())
                                .into(imagePoster);
                    } else {
                        Toast.makeText(getActivity(), "Nothing found, try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(getActivity(), "No network connection!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Trailer loading function
    private void loadTrailers(String id) {
        this.id = id;
        Call<VideoList> call = apiMethods.getVideo(id, Constants.API_KEY);
        call.enqueue(new Callback<VideoList>() {
            @Override
            public void onResponse(Call<VideoList> call, Response<VideoList> response) {
                if (response.isSuccessful()) {
                    videoList = response.body().getVideos();
                    trailerAdapter = new TrailerAdapter(videoList, getContext());
                    trailerRecyclerView.setAdapter(trailerAdapter);
                }

            }

            @Override
            public void onFailure(Call<VideoList> call, Throwable t) {

            }
        });
    }

    // Review loading function
    private void loadReviews(String id) {
        this.id = id;
        Call<ReviewList> reviewCall = apiMethods.getReviews(id, Constants.API_KEY);
        reviewCall.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(@NonNull Call<ReviewList> reviewCall, @NonNull Response<ReviewList> response) {
                if (response.isSuccessful()) {
                    reviewList = response.body().getReviews();
                    reviewAdapter = new ReviewAdapter(reviewList, getContext());
                    reviewRecyclerView.setAdapter(reviewAdapter);
                }

            }

            @Override
            public void onFailure(Call<ReviewList> reviewCall, Throwable t) {
                Log.w("Review response: ", "NO GOOD");

            }
        });
    }

    // Add to favorite
    @SuppressLint("StaticFieldLeak")
    public void addFavorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.MOVIE_ID,
                            movie.getId());
                    movieValues.put(MovieContract.MovieEntry.TITLE,
                            movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.POSTER,
                            movie.getPosterPath());
                    movieValues.put(MovieContract.MovieEntry.OVERVIEW,
                            movie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.RATING,
                            movie.getVoteAverage());
                    movieValues.put(MovieContract.MovieEntry.DATE,
                            movie.getReleaseDate());
                    Objects.requireNonNull(getContext()).getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            movieValues
                    );

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
              //  updateFavoriteButtons();
                Snackbar snackbar = Snackbar
                        .make(Objects.requireNonNull(getView()),"Added to favorites", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Remove from favorites
    @SuppressLint("StaticFieldLeak")
    public void removeFavorite() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    Objects.requireNonNull(getContext()).getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.MOVIE_ID + " = " + movie.getId(),
                            null
                    );

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
              //  updateFavoriteButtons();
                Snackbar snackbar = Snackbar
                        .make(Objects.requireNonNull(getView()),"Removed from favorites", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

  // Checks database for movie with particular id
   private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.MOVIE_ID},
                MovieContract.MovieEntry.MOVIE_ID + " = " + id,
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }

}
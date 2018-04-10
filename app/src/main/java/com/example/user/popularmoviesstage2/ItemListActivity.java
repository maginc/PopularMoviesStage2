package com.example.user.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.user.popularmoviesstage2.api.APIClient;
import com.example.user.popularmoviesstage2.api.APIMethods;
import com.example.user.popularmoviesstage2.model.Constants;
import com.example.user.popularmoviesstage2.model.Movie;
import com.example.user.popularmoviesstage2.model.MovieList;
import com.example.user.popularmoviesstage2.model.content.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    public List<Movie> data;
    public ArrayList<Movie> res;
    private FloatingActionButton fab;
    public String sortBy;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter adapter;
    private APIMethods apiMethods;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        apiMethods = APIClient.getClient().create(APIMethods.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        gridLayoutManager = new GridLayoutManager(this, 2);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.item_list);


        // Load data sorted by popularity by default
        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getString("sortBy", sortBy);
        } else {
            sortBy = "popular";
        }

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            fab.setVisibility(View.VISIBLE);

        }
        loadData(sortBy);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("sortBy", sortBy);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortBy = savedInstanceState.getString("sortBy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_popular:
                item.setChecked(true);
                sortBy = "popular";
                loadData(sortBy);
                return true;

            case R.id.item_top_rated:
                item.setChecked(true);
                sortBy = "top_rated";
                loadData(sortBy);
                return true;

            case R.id.item_favorite:
                item.setChecked(true);
                sortBy = "favorite";
                loadData(sortBy);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    //Fetch data from favorite database
    private ArrayList<Movie> loadFavorite() {
        ArrayList<Movie> result = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 0) {
                cursor.close();
                return null;
            }
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(null,
                            cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID)),
                            null,
                            cursor.getFloat(cursor.getColumnIndex(MovieContract.MovieEntry.RATING)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TITLE)),
                            null,
                            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.POSTER)),
                            null, null, null, null, null,
                            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.OVERVIEW)),
                            cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.DATE))
                    );
                    result.add(movie);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result;
    }

    //Fetch data from server
    private void loadData(String sortBuy) {
        if (sortBuy.equals("favorite")) {

            dataToRecycler(loadFavorite());

        } else {
            Call<MovieList> call = apiMethods.getMovieList(sortBuy, Constants.API_KEY);
            call.enqueue(new Callback<MovieList>() {
                @Override
                public void onResponse(Call<MovieList> call, Response<MovieList> response) {
                    if (response.isSuccessful() && response.message().equals("OK")) {

                        dataToRecycler(response.body().getResults());

                    }
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<MovieList> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "No network connection!!", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    // Function to reduce boilerplate code
    public void dataToRecycler(List<Movie> data) {
        if (data.size() != 0) {
            adapter = new SimpleItemRecyclerViewAdapter(ItemListActivity.this, data, mTwoPane);
            if (mTwoPane) {
                recyclerView.setLayoutManager(linearLayoutManager);
            } else {
                recyclerView.setLayoutManager(gridLayoutManager);
            }
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getApplicationContext(), "No  movies found!", Toast.LENGTH_SHORT).show();
        }
    }

   // RecyclerView adapter from template
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<Movie> movies;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie item = (Movie) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_TITLE, item.getTitle());
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();

                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_TITLE, item.getTitle());
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
                    context.startActivity(intent);
                }
            }
        };
        private Context context;

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<Movie> items,
                                      boolean twoPane) {

            movies = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Movie movie = movies.get(position);
            Picasso.with(mParentActivity)
                    .load(Constants.API_POSTER_HEADER_LARGE + movie.getPosterPath())
                    .into(holder.imagePoster);
            holder.itemView.setTag(movies.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView imagePoster;


            ViewHolder(View view) {
                super(view);
                imagePoster = view.findViewById(R.id.image_poster);

            }
        }
    }
}

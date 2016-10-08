package org.mefistofele.hikari.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import org.mefistofele.hikari.popularmovies.data.MoviesContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Vector;

import static android.R.attr.id;
import static android.os.Build.VERSION_CODES.M;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static org.mefistofele.hikari.popularmovies.data.MoviesDBHelper.LOG_TAG;

/**
 * Created by seba on 04/10/16.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnTaskCompleted<List<Movie>> {
    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_IMAGE_URL = 1;
    private static final int MOVIE_LOADER = 0;
    // Filter
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_IMAGE_URL
    };

    private MovieCursorAdapter mMoviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView
        mMoviesAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.main_activity_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        gridView.setAdapter(mMoviesAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String detailMovieUri = MoviesContract.MoviesEntry.buildMoviesUri(
                            cursor.getLong(MainActivityFragment.COL_ID)).toString();
                    Intent detailIntent =
                            new Intent(view.getContext(), MovieDetailActivity.class);
                    detailIntent.putExtra("MOVIE_DETAIL_URI", detailMovieUri);
                    startActivity(detailIntent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        FetchMovieAsyncTask fetchMovieAsyncTask = new FetchMovieAsyncTask(getContext());
        fetchMovieAsyncTask.setTaskCompletedListener(this);
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_val_popularity));
        fetchMovieAsyncTask.execute(sortOrder);
    }

    @Override
    public void onStart() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        //Log.d(LOG_TAG, "On start called");
        super.onStart();
        updateMovies();
    }


    public void onResume() {
        //Log.d(LOG_TAG, "On resume called");
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        super.onResume();
        updateMovies();
    }


    @Override
    public void onTaskCompleted(List<Movie> movies) {

        Vector<ContentValues> moviesContentValues = new Vector<ContentValues>(movies.size());
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String timestamp = s.format(new Date());

        for(Movie movie: movies){
            // Create a content value with parsed result
            ContentValues movieCV = new ContentValues();
            movieCV.put(MoviesContract.MoviesEntry._ID, movie.getId());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_RATING, movie.getVoteAvg());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_URL, movie.getPosterPath());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_TIMESTAMP, timestamp);
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, movie.getPopularity());
            moviesContentValues.add(movieCV);
        }

        int inserted = 0;
        // add to database
        if (moviesContentValues.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[moviesContentValues.size()];
            moviesContentValues.toArray(cvArray);
            inserted = getContext().getContentResolver()
                    .bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Record inserted Complete. " + inserted + " Inserted");
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort order choose it with user preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortOrderPreference = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_val_popularity));
        Uri moviesUri = MoviesContract.MoviesEntry.CONTENT_URI;
        String sortOrder = MoviesContract.MoviesEntry.COLUMN_POPULARITY + " desc";
        if (sortOrderPreference.equalsIgnoreCase("Rating")) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_RATING + " desc";
        } else if (sortOrderPreference.equalsIgnoreCase("Favourites")) {
            sortOrder = MoviesContract.MoviesEntry.COLUMN_FAVOURITE + " desc";
            return new CursorLoader(getActivity(),
                    moviesUri,
                    MOVIES_COLUMNS,
                    new String("favourite = 1"),
                    null,
                    sortOrder);
        }
        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMoviesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMoviesAdapter.swapCursor(null);
    }
}

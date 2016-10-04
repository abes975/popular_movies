package org.mefistofele.hikari.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ListView;

import org.mefistofele.hikari.popularmovies.data.MoviesContract;

import static android.R.attr.id;

/**
 * Created by seba on 04/10/16.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_LOADER = 0;
    // Filter
    private static final String[] MOVIES_COLUMNS = {
            MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_IMAGE_URL,
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_IMAGE_URL = 1;


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
        // The CursorAdapter will take data from our cursor and populate the ListView.
        mMoviesAdapter = new MovieCursorAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.main_activity_fragment, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movie_grid_view);
        gridView.setAdapter(mMoviesAdapter);

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
        String sortOrder = prefs.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_val_popularity));
        fetchMovieAsyncTask.execute(sortOrder);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.e("AAAH", "ON CREATE LOADER!");
        Uri movieUri = MoviesContract.MoviesEntry.CONTENT_URI;
        Log.e("AAAH", "movieUri " + movieUri.toString());
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e("CUR FIN", "CURSOR ONLOAD FINISHED");
        mMoviesAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e("CUR FIN", "CURSOR ONLOAD RESET");
        mMoviesAdapter.swapCursor(null);
    }
}

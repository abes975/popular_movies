package org.mefistofele.hikari.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import org.mefistofele.hikari.popularmovies.data.MoviesContract;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.fragment;
import static android.R.attr.windowHideAnimation;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String MOVIEDETAILFRAG_TAG = "MDTAG";
    private Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container)!= null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                MovieDetailFragment detailFragment = new MovieDetailFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container,
                        detailFragment, MOVIEDETAILFRAG_TAG).commit();
            }
        }  else {
            mTwoPane = false;
        }
    }

    @Override
    public void onMovieSelected(Uri detailMovieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable("MOVIE_DETAIL_URI", detailMovieUri);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment, MOVIEDETAILFRAG_TAG)
                    .commit();
        } else {
            Intent detailIntent =
                    new Intent(this, MovieDetailActivity.class);
            detailIntent.putExtra("MOVIE_DETAIL_URI", detailMovieUri);
            startActivity(detailIntent);
        }
    }


}
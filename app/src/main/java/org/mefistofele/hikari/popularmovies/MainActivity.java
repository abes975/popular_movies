package org.mefistofele.hikari.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private Boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container)!= null) {
            mTwoPane = true;
            MovieDetailFragment detailFragment = new MovieDetailFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, detailFragment).commit();
        }  else {
            mTwoPane = false;
        }
    }
}
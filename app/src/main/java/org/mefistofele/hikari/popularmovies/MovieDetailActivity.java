package org.mefistofele.hikari.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.mefistofele.hikari.popularmovies.data.MoviesContract;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static android.R.attr.id;
import static android.R.attr.rating;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.N;
import static java.security.AccessController.getContext;
import static org.mefistofele.hikari.popularmovies.R.drawable.trailer;
import static org.mefistofele.hikari.popularmovies.R.id.text_view_sysnopsis;

public class MovieDetailActivity extends AppCompatActivity  {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //String movieSize = "w500";
        Intent intent = getIntent();
        Uri movielUri = intent.getParcelableExtra("MOVIE_DETAIL_URI");
        if (movielUri == null)
            return;
        Bundle args = new Bundle();
        args.putParcelable("MOVIE_DETAIL_URI", movielUri);
        if (savedInstanceState == null) {
            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().add(R.id.movie_detail_container, detailFragment).commit();
        }
    }


}


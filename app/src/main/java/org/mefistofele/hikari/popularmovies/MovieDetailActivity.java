package org.mefistofele.hikari.popularmovies;

import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.mefistofele.hikari.popularmovies.data.MoviesContract;
import org.w3c.dom.Text;

import static android.R.attr.id;
import static android.R.attr.rating;
import static org.mefistofele.hikari.popularmovies.R.id.text_view_sysnopsis;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    // Filter
    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MoviesEntry.COLUMN_IMAGE_URL,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_TITLE
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_IMAGE_URL = 0;
    static final int COL_OVERVIEW = 1;
    static final int COL_RATING = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_TITLE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        String movieSize = "w500";
        Intent intent = getIntent();
        String movieStringlUri = intent.getStringExtra("MOVIE_DETAIL_URI");
        Log.d(LOG_TAG, "Passed uri " + movieStringlUri);
        Uri detailUri = Uri.parse(movieStringlUri);
        // Get data from Content Provider
        Cursor movieDetailCursor = getContentResolver().query(detailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
        Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(movieDetailCursor));
        if (movieDetailCursor.getCount() != 0) {
            movieDetailCursor.moveToFirst();
            TextView title = (TextView) findViewById(R.id.text_view_title);
            title.setText(movieDetailCursor.getString(COL_TITLE));
            TextView releaseDate = (TextView) findViewById(R.id.text_view_release_date);
            releaseDate.setText(movieDetailCursor.getString(COL_RELEASE_DATE));
            TextView rating = (TextView) findViewById(R.id.text_view_rating);
            rating.setText(new Double(movieDetailCursor.getDouble(COL_RATING)).toString());
            ImageView poster =  (ImageView) findViewById(R.id.image_view_poster);
            String base_url = "http://image.tmdb.org/t/p/w500/";
            Picasso.with(this).load(base_url + movieDetailCursor.getString(COL_IMAGE_URL))
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.error_placeholder)
                    .fit()
                    .centerInside()
                    .into(poster);
            TextView synopsis = (TextView) findViewById(R.id.text_view_sysnopsis);
            synopsis.setText(movieDetailCursor.getString(COL_OVERVIEW));

        }
    }

}


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
    // Filter
    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MoviesEntry.COLUMN_IMAGE_URL,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_FAVOURITE
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_IMAGE_URL = 0;
    static final int COL_OVERVIEW = 1;
    static final int COL_RATING = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_TITLE = 4;
    static final int COL_FAVOURITE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        String movieSize = "w500";
        Intent intent = getIntent();
        String movieStringlUri = intent.getStringExtra("MOVIE_DETAIL_URI");
        //Log.d(LOG_TAG, "Passed uri " + movieStringlUri);
        final Uri detailUri = Uri.parse(movieStringlUri);
        // Get data from Content Provider
        Cursor movieDetailCursor = getContentResolver().query(detailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);

        //Log.d(LOG_TAG, DatabaseUtils.dumpCursorToString(movieDetailCursor));

        if (movieDetailCursor.getCount() != 0) {
            movieDetailCursor.moveToFirst();
            TextView title = (TextView) findViewById(R.id.text_view_title);
            String titleValue = movieDetailCursor.getString(COL_TITLE);
            title.setText(titleValue);
            TextView releaseDate = (TextView) findViewById(R.id.text_view_release_date);
            releaseDate.setText(movieDetailCursor.getString(COL_RELEASE_DATE));
            TextView rating = (TextView) findViewById(R.id.text_view_rating);
            rating.setText(new Double(movieDetailCursor.getDouble(COL_RATING)).toString());
            ImageView poster =  (ImageView) findViewById(R.id.image_view_poster);
            String base_url = "http://image.tmdb.org/t/p/w500/";
            Picasso.with(this).load(base_url + movieDetailCursor.getString(COL_IMAGE_URL))
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(poster);
            TextView synopsis = (TextView) findViewById(R.id.text_view_sysnopsis);
            synopsis.setText(movieDetailCursor.getString(COL_OVERVIEW));
            final int favourite = movieDetailCursor.getInt(COL_FAVOURITE);
            final TextView favourites = (TextView) findViewById(R.id.toggle_favourites);
            if (favourite == 1)
                favourites.setText(getResources().getString(R.string.remove_from_favourites));
            else
                favourites.setText(getResources().getString(R.string.add_to_favourites));

            final ContentValues movieCV = new ContentValues();
            movieCV.put(MoviesContract.MoviesEntry._ID, ContentUris.parseId(detailUri));
            // Let's complement the values of favourite if the button will be clicked :D
            movieCV.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, favourite == 1 ? 0 :1 );
            favourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContentResolver().update(detailUri,
                            movieCV,
                            null,
                            null);
                    movieCV.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, favourite == 1 ? 0 :1 );
                    if (movieCV.getAsInteger(MoviesContract.MoviesEntry.COLUMN_FAVOURITE) == 1) {
                        favourites.setText(getResources().getString(R.string.remove_from_favourites));
                    }
                    else {
                        favourites.setText(getResources().getString(R.string.add_to_favourites));
                    }
                }
            });

        }
    }

}


package org.mefistofele.hikari.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mefistofele.hikari.popularmovies.data.MoviesContract;
import org.mefistofele.hikari.popularmovies.data.MoviesContract.MoviesEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by seba on 11/09/16.
 */

public class FetchMovieAsyncTask extends AsyncTask<String, Void, Void> {
    /* Download data from Movie Db */
    private final String LOG_TAG = FetchMovieAsyncTask.class.getSimpleName();

    private final Context mContext;

    public FetchMovieAsyncTask(Context context) {
        mContext = context;
    }


    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieData = null;
        String popularity = "populrity.desc";
        String rating = "vote_average.desc";
        try {

            // Construct the URL for the Guardian News query
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            //   /discover/movie?sort_by=popularity.desc
            // /discover/movie/?certification_country=US&certification=R&sort_by=vote_average.desc
            final String SORT_BY_PARAM = "sort_by";;
            final String KEY_PARAM = "api_key";

            String sortCriteria = popularity;
           if (params[0].equalsIgnoreCase("Rating"))
                sortCriteria = rating;

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, sortCriteria)
                    .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            int code = urlConnection.getResponseCode();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // REMOVE ME!
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieData = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getMovieDataFromJson(movieData);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }


    private void getMovieDataFromJson(String downloadedData) throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MDB_LIST = "results";

        JSONObject forecastJson = new JSONObject(downloadedData);
        JSONArray moviesArray = forecastJson.getJSONArray(MDB_LIST);


        Vector<ContentValues> moviesContentValues = new Vector<ContentValues>(moviesArray.length());


        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String timestamp = s.format(new Date());

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObj = moviesArray.getJSONObject(i);
            // Parsing network data
            Movie movie = Movie.parseJasonData(movieObj);
            // Create a content value with parsed result
            ContentValues movieCV = new ContentValues();
            movieCV.put(MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            movieCV.put(MoviesEntry.COLUMN_TITLE, movie.getTitle());
            movieCV.put(MoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieCV.put(MoviesEntry.COLUMN_RATING, movie.getVoteAvg());
            movieCV.put(MoviesEntry.COLUMN_IMAGE_URL, movie.getPosterPath());
            movieCV.put(MoviesEntry.COLUMN_TIMESTAMP, timestamp);
            movieCV.put(MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            moviesContentValues.add(movieCV);
        }
        int inserted = 0;
        // add to database
        if (moviesContentValues.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[moviesContentValues.size()];
            moviesContentValues.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
    }
}

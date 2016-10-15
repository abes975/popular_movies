package org.mefistofele.hikari.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by seba on 10/10/16.
 */

public class FetchReviewsAsyncTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = FetchReviewsAsyncTask.class.getSimpleName();
    private final String KEY_REVIEWS = "content";
    private final Context mContext;

    private ArrayAdapter mArrayAdapter;

    public FetchReviewsAsyncTask(Context context, ArrayAdapter<String> arrayAdapter) {
        mContext = context;
        mArrayAdapter = arrayAdapter;
    }


    private List<String> getTrailerFromJSON(String downloadedData) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_LIST = "results";
        JSONObject forecastJson = new JSONObject(downloadedData);
        JSONArray reviewssArray = forecastJson.getJSONArray(MDB_LIST);

        ArrayList<String> reviews = new ArrayList<String>();
        for (int i = 0; i < reviewssArray.length(); i++) {
            JSONObject reviewsObj = reviewssArray.getJSONObject(i);
            String trailer = reviewsObj.getString(KEY_REVIEWS);
            reviews.add(trailer);
        }
        return reviews;
    }


    @Override
    protected List<String> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieData = null;
        try {

            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String MOVIE_PATH = params[0];
            final String REVIEWS_PATH = "reviews";
            final String KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(REVIEWS_PATH)
                    .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            //Log.d("QUERY ", url.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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
            return getTrailerFromJSON(movieData);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<String> reviews) {
        super.onPostExecute(reviews);
        mArrayAdapter.clear();
        if (reviews != null && reviews.size() > 0 ) {
            for (String review : reviews)
                mArrayAdapter.add(review);
        } else {
            mArrayAdapter.add(mContext.getResources().getString(R.string.no_reviews_available));
        }


    }
}

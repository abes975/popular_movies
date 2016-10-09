package org.mefistofele.hikari.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by seba on 08/10/16.
 */

public class FetchMovieTrailerAsyncTask extends AsyncTask<String, Void, List<String>> {

    private final String LOG_TAG = FetchMovieTrailerAsyncTask.class.getSimpleName();
    private final String KEY_TRAILERS = "key";
    private final Context mContext;

    private OnTaskCompleted<List<String>> mOnTaskCompleted;

    public FetchMovieTrailerAsyncTask(Context context) {

        mContext = context;
    }

    /* Sets the listener*/
    public void setTaskCompletedListener(OnTaskCompleted onTaskCompleted){
        mOnTaskCompleted = onTaskCompleted;
    }

    private List<String> getTrailerFromJSON(String downloadedData) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_LIST = "results";

        JSONObject forecastJson = new JSONObject(downloadedData);
        JSONArray trailersArray = forecastJson.getJSONArray(MDB_LIST);

        ArrayList<String> trailers = new ArrayList<String>();
        for (int i = 0; i < trailersArray.length(); i++) {
            JSONObject trailersObj = trailersArray.getJSONObject(i);
            String trailer = trailersObj.getString(KEY_TRAILERS);
            trailers.add(trailer);
        }
        return trailers;
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
            final String VIDEO_PATH = "videos";
            final String KEY_PARAM = "api_key";


            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(MOVIE_PATH)
                    .appendPath(VIDEO_PATH)
                    .appendQueryParameter(KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.d("QUERY ", url.toString());

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
    protected void onPostExecute(List<String> trailers) {
        super.onPostExecute(trailers);
        for (String s : trailers)
            Log.d(LOG_TAG, "results " + s);
        if (trailers != null)
            mOnTaskCompleted.onTaskCompleted(trailers);

    }
}

package org.mefistofele.hikari.popularmovies;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.type;
import static android.provider.Contacts.SettingsColumns.KEY;


/**
 * Created by seba on 11/09/16.
 */

public class Movie implements Parcelable {
//public class Movie {
    // Will use those while parsing json stuff
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_BACKDROP = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_RELEASE_DATE = "release_date";

    static final String KEY_DEFAULT_SIZE = "w185";
    static final String KEY_IMG_POSTER =  "poster";
    static final String KEY_IMG_BACK = "backdrop";

    // That's the information extracted from json
    private long mId;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mPosterPath;
    private String mBackDropPath;
    private double mVoteAvg;
    private long mVoteCnt;

    public Movie(long id, String title, String overview, String releaseDate, String posterPath, String backdropPath, double voteAvg, long voteCnt) {
        mId = id;
        mTitle = title;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mPosterPath = posterPath;
        mBackDropPath = backdropPath;
        mVoteAvg = voteAvg;
        mVoteCnt = voteCnt;
    }

    public long getId() {
        return mId;
    }

    public long getVoteCount() {
        return mVoteCnt;
    }

    public double getVoteAvg() {
        return mVoteAvg;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public String getBackDropPath() {
        return mBackDropPath;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(mId);
        sb.append("\n");
        sb.append(mTitle);
        sb.append("\n");
        sb.append(mPosterPath);
        sb.append("\n");
        sb.append(mReleaseDate);
        sb.append("\n");
        sb.append(mBackDropPath);
        sb.append("\n");
        sb.append(mOverview);
        sb.append("\n");
        sb.append(mVoteAvg);
        sb.append("\n");
        sb.append(mVoteCnt);
        sb.append("\n");
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mPosterPath = in.readString();
        mBackDropPath = in.readString();
        mVoteAvg = in.readDouble();
        mVoteCnt = in.readLong();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterPath);
        dest.writeString(mBackDropPath);
        dest.writeDouble(mVoteAvg);
        dest.writeLong(mVoteCnt);
    }



    // Get data from json string retrieved from network
    public static Movie parseJasonData(JSONObject movieJsonObject) throws JSONException {

        Long id = movieJsonObject.getLong(KEY_ID);
        String title = movieJsonObject.getString(KEY_TITLE);
        String overview = movieJsonObject.getString(KEY_OVERVIEW);
        String releaseDate = movieJsonObject.getString(KEY_RELEASE_DATE);
        String posterPath = movieJsonObject.getString(KEY_POSTER);
        String backdropPath = movieJsonObject.getString(KEY_BACKDROP);
        double voteAvg = movieJsonObject.getDouble(KEY_VOTE_AVERAGE);
        long voteCnt = movieJsonObject.getLong(KEY_VOTE_COUNT);

        Movie movie = new Movie(id, title, overview, releaseDate, posterPath, backdropPath, voteAvg, voteCnt);
        return movie;
    }


    /* This URL will used by picasso library to get the image....think whether to move in another place
    *  it's enough to pass the size of the image and poster or backdrop*/

    public Uri getPosterURI(String size, String type) {
        final String BASE_URL = "http://image.tmdb.org/t/p/";
        String image_path = null;
        if (type.equals(KEY_IMG_POSTER)) {
            image_path = this.getPosterPath();
        } else if (type.equals(KEY_IMG_BACK)) {
            image_path = this.getBackDropPath();
        }
        Uri poster_uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(size)
                .appendEncodedPath(image_path)
                .build();
        return poster_uri;
    }

}

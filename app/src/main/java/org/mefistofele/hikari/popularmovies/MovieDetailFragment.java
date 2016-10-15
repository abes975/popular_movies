package org.mefistofele.hikari.popularmovies;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.mefistofele.hikari.popularmovies.R;
import org.mefistofele.hikari.popularmovies.data.MoviesContract;

import java.util.ArrayList;
import java.util.List;

import static org.mefistofele.hikari.popularmovies.R.drawable.trailer;
import static org.mefistofele.hikari.popularmovies.R.string.synopsis;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailFragment extends Fragment implements OnTaskCompleted<List<String>> {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private Uri movieStringlUri;

    private static class ViewHolder {
        TextView title;
        TextView releaseDate;
        TextView rating;
        ImageView poster;
        TextView synopsis;
        ListView review;
        TextView favourites;
        ImageView trailer;
        TextView noTrailers;
    }

    private ViewHolder viewHolder;

    // Filter
    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MoviesEntry.COLUMN_IMAGE_URL,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_RATING,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_TITLE,
            MoviesContract.MoviesEntry.COLUMN_FAVOURITE
    };

    private static final String[] FAVOURITE_COLUMNS = {
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

    public MovieDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if (getArguments() != null) {
            movieStringlUri = (Uri) getArguments().getParcelable("MOVIE_DETAIL_URI");

            // Inflate the layout for this fragment
            rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rootView.findViewById(R.id.text_view_title);
            viewHolder.releaseDate = (TextView) rootView.findViewById(R.id.text_view_release_date);
            viewHolder.rating = (TextView) rootView.findViewById(R.id.text_view_rating);
            viewHolder.poster = (ImageView) rootView.findViewById(R.id.image_view_poster);
            viewHolder.synopsis = (TextView) rootView.findViewById(R.id.text_view_sysnopsis);
            viewHolder.favourites = (TextView) rootView.findViewById(R.id.toggle_favourites);
            viewHolder.review = (ListView) rootView.findViewById(R.id.review_list_view);
            viewHolder.trailer = (ImageView) rootView.findViewById(R.id.trailer_imageview);
            viewHolder.noTrailers = (TextView) rootView.findViewById(R.id.no_trailer_text_view);

            rootView.setTag(viewHolder);
        } else {
            rootView = inflater.inflate(R.layout.empty_detail, container, false);
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (movieStringlUri == null)
            return;
        final Uri detailUri = movieStringlUri;
        long movieID = ContentUris.parseId(detailUri);
        // Get data from Content Provider
        Cursor movieDetailCursor = getContext().getContentResolver().query(detailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);


        if (movieDetailCursor.getCount() != 0) {
            movieDetailCursor.moveToFirst();
            String titleValue = movieDetailCursor.getString(COL_TITLE);
            viewHolder.title.setText(titleValue);
            viewHolder.releaseDate.setText(movieDetailCursor.getString(COL_RELEASE_DATE));

            viewHolder.rating.setText(new Double(movieDetailCursor.getDouble(COL_RATING)).toString());
            String base_url = "http://image.tmdb.org/t/p/w500/";
            Picasso.with(getContext()).load(base_url + movieDetailCursor.getString(COL_IMAGE_URL))
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(viewHolder.poster);

            viewHolder.synopsis.setText(movieDetailCursor.getString(COL_OVERVIEW));


            FetchMovieTrailerAsyncTask movieTrailer = new FetchMovieTrailerAsyncTask(getContext());
            movieTrailer.setTaskCompletedListener(this);
            movieTrailer.execute(new Long(movieID).toString());

            ArrayList<String> reviews = new ArrayList<String>();
            ArrayAdapter<String> reviewArrayAdapter =new ArrayAdapter<String> (getContext(),R.layout.review_details, reviews);
            FetchReviewsAsyncTask movieReview = new FetchReviewsAsyncTask(getContext(), reviewArrayAdapter);
            movieReview.execute(new Long(movieID).toString());

            viewHolder.review.setAdapter(reviewArrayAdapter);

            int favourite = movieDetailCursor.getInt(COL_FAVOURITE);

            if (favourite == 1) {
                viewHolder.favourites.setText(getResources().getString(R.string.remove_from_favourites));
            } else {
                viewHolder.favourites.setText(getResources().getString(R.string.add_to_favourites));
            }
            final ContentValues movieCV = new ContentValues();
            movieCV.put(MoviesContract.MoviesEntry._ID, movieID);

            viewHolder.favourites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int favourite = 0;
                    Cursor favouriteCursor = getContext().getContentResolver().query(detailUri,
                            FAVOURITE_COLUMNS,
                            null,
                            null,
                            null);

                    if (favouriteCursor.getCount() == 1) {
                        favouriteCursor.moveToFirst();
                        favourite = favouriteCursor.getInt(0);

                    }
                    ContentValues localCV = new ContentValues(movieCV);
                    // Let's complement the values of favourite if the button will be clicked :D
                    favourite = favourite == 1 ? 0 : 1;
                    localCV.put(MoviesContract.MoviesEntry.COLUMN_FAVOURITE, favourite);
                    getContext().getContentResolver().update(detailUri,
                            localCV,
                            null,
                            null);

                    if (favourite == 1) {
                        viewHolder.favourites.setText(getResources().getString(R.string.remove_from_favourites));
                    }
                    else {
                        viewHolder.favourites.setText(getResources().getString(R.string.add_to_favourites));
                    }
                }
            });



        }
    }


    public void onTaskCompleted(List<String> trailers) {
        if (trailers != null && trailers.size() > 0) {
            viewHolder.trailer.setVisibility(View.VISIBLE);
            final List<String> results = trailers;
            viewHolder.trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoPath = "http://www.youtube.com/watch?v=" + results.get(0);
                    Intent viewTrailer = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(videoPath));
                    startActivity(viewTrailer);
                }
            });
        } else {
            viewHolder.trailer.setImageResource(R.drawable.no_trailer);
            viewHolder.noTrailers.setVisibility(View.VISIBLE);
            viewHolder.trailer.setVisibility(View.VISIBLE);

        }
    }


}

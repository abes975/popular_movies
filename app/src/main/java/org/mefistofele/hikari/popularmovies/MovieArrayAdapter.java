package org.mefistofele.hikari.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by seba on 11/09/16.
 */

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();
    Context mContext;

    public MovieArrayAdapter(Activity context, List<Movie> results) {
        super(context, 0, results);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = getItem(position);
        // new View object then inflate the layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_image, parent, false);
        }

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.grid_view_movie_image);
        Picasso.with(mContext)
                .load(movie.getPosterURI(Movie.KEY_DEFAULT_SIZE, Movie.KEY_IMG_POSTER))
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.error_placeholder)
                .fit()
                .centerInside()
                .into(moviePoster);

        moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent =
                        new Intent(view.getContext(), MovieDetailActivity.class);
                detailIntent.putExtra("MOVIE_DETAIL", movie);
                view.getContext().startActivity(detailIntent);
            }
        });
        return convertView;
    }
}

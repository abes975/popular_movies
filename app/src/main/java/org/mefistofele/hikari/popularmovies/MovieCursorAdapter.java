package org.mefistofele.hikari.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by seba on 04/10/16.
 */

public class MovieCursorAdapter extends CursorAdapter {
    public MovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }
    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_image, parent, false);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iv = (ImageView) view;
        String base_url = "http://image.tmdb.org/t/p/w500/";
        Picasso.with(context).load(base_url + cursor.getString(MainActivityFragment.COL_IMAGE_URL))
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.error_placeholder)
                .fit()
                .centerInside()
                .into(iv);
    }
}
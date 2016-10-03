package org.mefistofele.hikari.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.R.attr.id;
import static android.R.attr.rating;
import static org.mefistofele.hikari.popularmovies.R.id.text_view_sysnopsis;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        String movieSize = "w500";
        Intent intent = getIntent();
        Movie movieData = intent.getParcelableExtra("MOVIE_DETAIL");
        if (movieData != null) {
            TextView title = (TextView) findViewById(R.id.text_view_title);
            title.setText(movieData.getTitle());
            TextView releaseDate = (TextView) findViewById(R.id.text_view_release_date);
            releaseDate.setText(movieData.getReleaseDate());
            TextView rating = (TextView) findViewById(R.id.text_view_rating);
            rating.setText(new Double(movieData.getVoteAvg()).toString());
            ImageView poster =  (ImageView) findViewById(R.id.image_view_poster);
            Picasso.with(this).load(movieData.getPosterURI(movieSize, Movie.KEY_IMG_POSTER))
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.error_placeholder)
                    .fit()
                    .centerInside()
                    .into(poster);
            TextView synopsis = (TextView) findViewById(R.id.text_view_sysnopsis);
            synopsis.setText(movieData.getOverview());

        }
    }
}

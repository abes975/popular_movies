package org.mefistofele.hikari.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by seba on 04/10/16.
 */

public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MoviesEntry.TABLE_MOVIES + "(" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MoviesEntry.COLUMN_TIMESTAMP + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL UNIQUE, " +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_RATING + " REAL, " +
                MoviesContract.MoviesEntry.COLUMN_IMAGE_URL + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MoviesEntry.COLUMN_FAVOURITE + " INTEGER DEFAULT 0" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.MoviesEntry.TABLE_MOVIES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}

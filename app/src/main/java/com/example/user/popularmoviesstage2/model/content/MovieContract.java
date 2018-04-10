package com.example.user.popularmoviesstage2.model.content;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Andris on 002 02.04.18.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.user.popularmoviesstage2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movie";
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;


        public static final String TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movies_id";
        public static final String TITLE = "title";
        public static final String POSTER = "poster";
        public static final String OVERVIEW = "overview";
        public static final String RATING = "rating";
        public static final String DATE = "date";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}

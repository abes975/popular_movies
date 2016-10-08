package org.mefistofele.hikari.popularmovies;

/**
 * Created by seba on 07/10/16.
 */
public interface OnTaskCompleted<T> {
    void onTaskCompleted(T result);
}
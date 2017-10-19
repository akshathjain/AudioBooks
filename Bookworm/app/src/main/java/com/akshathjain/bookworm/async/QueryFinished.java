package com.akshathjain.bookworm.async;

/**
 * Created by Akshath on 10/19/2017.
 */

public interface QueryFinished<T> {
    void onQueryFinished(T t);
}
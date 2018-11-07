package com.asuc.asucmobile.infrastructure;

import java.util.List;

/**
 * Standard interface for fetching static data. Intended integration with Firestore or Retrofit
 * @param <T>
 */
public interface Repository<T> {

    public List<T> scanAll(final List<T> list);
}

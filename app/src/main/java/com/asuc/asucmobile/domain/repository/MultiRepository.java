package com.asuc.asucmobile.domain.repository;

import java.util.List;
import java.util.Map;

/**
 * Repository that actually wraps other repositories. Useful for map icons
 * @param <K>
 * @param <V>
 */
public interface MultiRepository<K,V> {

    public Map<K,List<V>> scanAll(final Map<K, List<V>> map, RepositoryCallback<V> callback);
}

package com.asuc.asucmobile.domain.repository;

public interface RepositoryCallback<T> {

    void onSuccess();

    void onFailure();
}

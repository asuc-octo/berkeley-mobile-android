package com.asuc.asucmobile.infrastructure;

public interface RepositoryCallback<T> {

    void onSuccess();

    void onFailure();
}

package com.asuc.asucmobile.infrastructure;

import android.util.Log;

import com.asuc.asucmobile.domain.models.Library;
import com.asuc.asucmobile.domain.models.responses.LibrariesResponse;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class LibraryRetrofitRepository implements Repository<Library> {

    public static final String TAG = "LibraryRetrofit";

    private BMService mService;

    public LibraryRetrofitRepository(BMService service) {
        mService = service;
    }

    @Override
    public List<Library> scanAll(final List<Library> list, final RepositoryCallback<Library> callback) {

        mService.callLibrariesList().enqueue(new retrofit2.Callback<LibrariesResponse>() {
            @Override
            public void onResponse(Call<LibrariesResponse> call, Response<LibrariesResponse> response) {
                if (response != null) {
                    list.clear();
                    list.addAll((List<Library>) response.body().getLibraries());
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<LibrariesResponse> call, Throwable t) {
                Log.d(TAG,"Unable to retrieve dining hall data, please try again");
                callback.onFailure();
            }
        });

        return list;
    }
}

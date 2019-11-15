package com.asuc.asucmobile.infrastructure;

import android.util.Log;

import com.asuc.asucmobile.domain.models.Resource;
import com.asuc.asucmobile.domain.models.responses.ResourcesResponse;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceRetrofitRepository implements Repository<Resource> {

    public static final String TAG = "ResourceRetrofit";

    private BMService mService;

    public ResourceRetrofitRepository(BMService mService) {
        this.mService = mService;
    }

    @Override
    public List<Resource> scanAll(final List<Resource> list, final RepositoryCallback<Resource> callback) {
        mService.callResourcesList().enqueue(new Callback<ResourcesResponse>() {
            @Override
            public void onResponse(Call<ResourcesResponse> call, Response<ResourcesResponse> response) {
                if (response != null) {
                    list.clear();
                    list.addAll(response.body().getResources());
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ResourcesResponse> call, Throwable t) {
                Log.d(TAG, "Unable to retrieve resource data, please try again");
            }
        });
        return list;
    }
}

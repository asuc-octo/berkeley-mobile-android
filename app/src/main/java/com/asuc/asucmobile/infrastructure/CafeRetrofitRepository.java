package com.asuc.asucmobile.infrastructure;

import android.util.Log;

import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.responses.CafesResponse;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeRetrofitRepository implements Repository<Cafe> {

    public static final String TAG = "CafeRetrofit";

    private BMService mService;

    public CafeRetrofitRepository(BMService service) {
        mService = service;
    }

    @Override
    public List<Cafe> scanAll(final List<Cafe> list, final RepositoryCallback<Cafe> callback) {
        mService.callCafeList().enqueue(new Callback<CafesResponse>() {
            @Override
            public void onResponse(Call<CafesResponse> call, Response<CafesResponse> response) {
                if (response != null) {
                    list.clear();
                    list.addAll((List<Cafe>) response.body().getCafes());
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<CafesResponse> call, Throwable t) {
                Log.d(TAG,"Unable to retrieve dining hall data, please try again");
                callback.onFailure();
            }
        });
        return list;
    }
}

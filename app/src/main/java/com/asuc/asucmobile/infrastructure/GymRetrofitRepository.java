package com.asuc.asucmobile.infrastructure;

import android.util.Log;

import com.asuc.asucmobile.domain.models.Gym;
import com.asuc.asucmobile.domain.models.responses.GymsResponse;
import com.asuc.asucmobile.domain.repository.Repository;
import com.asuc.asucmobile.domain.repository.RepositoryCallback;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GymRetrofitRepository implements Repository<Gym> {

    public static final String TAG = "GymRetrofit";

    private BMService mService;


    public GymRetrofitRepository(BMService service) {
        mService = service;
    }

    @Override
    public List<Gym> scanAll(final List<Gym> list, final RepositoryCallback<Gym> callback) {
        mService.callGymsList().enqueue(new Callback<GymsResponse>() {
            @Override
            public void onResponse(Call<GymsResponse> call, Response<GymsResponse> response) {
                if (response != null) {
                    list.clear();
                    list.addAll(response.body().getGyms());
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(Call<GymsResponse> call, Throwable t) {
                Log.d(TAG, "Unable to retrieve gym data, please try again");
            }
        });
        return list;
    }
}

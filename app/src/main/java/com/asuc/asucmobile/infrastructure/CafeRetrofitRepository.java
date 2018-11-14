package com.asuc.asucmobile.infrastructure;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.asuc.asucmobile.domain.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.domain.models.Cafe;
import com.asuc.asucmobile.domain.models.FoodPlace;
import com.asuc.asucmobile.domain.models.responses.CafesResponse;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CafeRetrofitRepository implements Repository<Cafe> {

    public static final String TAG = "CafeRetrofit";

    private Call<CafesResponse> mCafesCall;

    @Inject
    public CafeRetrofitRepository(BMService service) {
        mCafesCall = service.callCafeList();
    }

    @Override
    public List<Cafe> scanAll(final List<Cafe> list, final RepositoryCallback<Cafe> callback) {
        mCafesCall.enqueue(new Callback<CafesResponse>() {
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

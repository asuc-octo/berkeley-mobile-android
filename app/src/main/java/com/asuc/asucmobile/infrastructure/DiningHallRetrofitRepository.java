package com.asuc.asucmobile.infrastructure;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.asuc.asucmobile.domain.adapters.FoodPlaceAdapter;
import com.asuc.asucmobile.domain.models.DiningHall;
import com.asuc.asucmobile.domain.models.FoodPlace;
import com.asuc.asucmobile.domain.models.responses.DiningHallsResponse;
import com.asuc.asucmobile.domain.services.BMService;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

public class DiningHallRetrofitRepository implements Repository<DiningHall> {

    public static final String TAG = "DiningHallRetrofit";

    @Inject
    BMService bmService;

    private Call<DiningHallsResponse> mDiningHallsCall;


    public DiningHallRetrofitRepository() {
        mDiningHallsCall = bmService.callDiningHallList();
    }

    @Override
    public List<DiningHall> scanAll(final List<DiningHall> list) {

        mDiningHallsCall.enqueue(new retrofit2.Callback<DiningHallsResponse>() {
            @Override
            public void onResponse(Call<DiningHallsResponse> call, Response<DiningHallsResponse> response) {
                if (response != null) {
                    list.clear();
                    list.addAll((List<DiningHall>) response.body().getDiningHalls()); // TODO fix this
                }
            }

            @Override
            public void onFailure(Call<DiningHallsResponse> call, Throwable t) {
                Log.d(TAG,"Unable to retrieve dining hall data, please try again");
            }
        });

        return list;
    }
}

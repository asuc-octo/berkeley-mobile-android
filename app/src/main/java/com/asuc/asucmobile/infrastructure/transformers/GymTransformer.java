package com.asuc.asucmobile.infrastructure.transformers;

import android.util.Log;

import com.asuc.asucmobile.infrastructure.models.Gym;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GymTransformer {

    public static final String TAG = "GymTransformer";

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Gym> gymQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Gym> gyms = new ArrayList<>();
        com.asuc.asucmobile.infrastructure.models.Gym infraGym = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            try {
                infraGym = documentSnapshot.toObject(com.asuc.asucmobile.infrastructure.models.Gym.class);
                if (infraGym != null) {
                    gyms.add(gymInfraDomainTransformer(infraGym));
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return gyms;
    }

    public com.asuc.asucmobile.domain.models.Gym gymInfraDomainTransformer(Gym gym) {

        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>();

        for (OpenClose openClose : gym.getOpenCloses()) {
            weeklyOpen.add(new Date(openClose.getOpenTime() * 1000));
            weeklyClose.add(new Date(openClose.getCloseTime() * 1000));
        }

        return com.asuc.asucmobile.domain.models.Gym.builder()
                .address(gym.getAddress())
                .name(gym.getName())
                .opening(weeklyOpen)
                .closing(weeklyClose)
                .imageUrl(gym.getPicture())
                .build();
    }
}

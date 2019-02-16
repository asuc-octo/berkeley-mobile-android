package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.Gym;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GymTransformer {

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Gym> gymQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Gym> gyms = new ArrayList<>();
        com.asuc.asucmobile.infrastructure.models.Gym infraGym = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraGym = documentSnapshot.toObject(com.asuc.asucmobile.infrastructure.models.Gym.class);
            if (infraGym != null) {
                gyms.add(gymInfraDomainTransformer(infraGym));
            }
        }
        return gyms;
    }

    public com.asuc.asucmobile.domain.models.Gym gymInfraDomainTransformer(Gym gym) {

        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>();

        for (OpenClose openClose : gym.getOpenCloses()) {
            weeklyOpen.add(new Date(openClose.getOpenTIme()));
            weeklyClose.add(new Date(openClose.getCloseTime()));
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

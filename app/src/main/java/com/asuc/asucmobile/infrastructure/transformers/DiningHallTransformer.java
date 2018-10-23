package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.DiningHall;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for some data transforms from infra to domain.
 * Probably want to do this from Firebase QuerySnapshots
 */
public class DiningHallTransformer {

    public List<DiningHall> dingingHallQSDomainTransformer(QuerySnapshot qs) {
        List<DiningHall> diningHalls = new ArrayList<>();
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            diningHalls.add(documentSnapshot.toObject(DiningHall.class));
        }
        return diningHalls;
    }

    public List<com.asuc.asucmobile.models.DiningHall> diningHallListInfraDomainTransformer(List<DiningHall> diningHallList) {
        return null;
    }

    // lombok building?
    public com.asuc.asucmobile.models.DiningHall diningHallInfraDomainTransformer(DiningHall diningHall) {
        return com.asuc.asucmobile.models.DiningHall.builder()
                .build();
    }


}

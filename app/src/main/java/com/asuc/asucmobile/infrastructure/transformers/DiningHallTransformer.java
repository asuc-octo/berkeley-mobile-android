package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.DiningHall;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Provides;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * Wrapper for some data transforms from infra to domain.
 * Probably want to do this from Firebase QuerySnapshots
 */
@NoArgsConstructor
public class DiningHallTransformer {

    /**
     * Transform Firestore QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.models.DiningHall> dingingHallQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.models.DiningHall> diningHalls = new ArrayList<>();
        DiningHall infraDiningHall = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraDiningHall = documentSnapshot.toObject(DiningHall.class);
            if (infraDiningHall != null) {
                diningHalls.add(diningHallInfraDomainTransformer(infraDiningHall));
            }
        }
        return diningHalls;
    }

    /**
     * Transform diningHallList from infrastructure to domain model
     * @param diningHallList
     * @return
     */
    public List<com.asuc.asucmobile.models.DiningHall> diningHallListInfraDomainTransformer(List<DiningHall> diningHallList) {
        List<com.asuc.asucmobile.models.DiningHall> retList = new ArrayList<>();
        for (DiningHall diningHall : diningHallList) {
            retList.add(diningHallInfraDomainTransformer(diningHall));
        }
        return retList;
    }

    /**
     * Transform single diningHall from infrastructure to domain model
     * @param diningHall
     * @return
     */
    public com.asuc.asucmobile.models.DiningHall diningHallInfraDomainTransformer(DiningHall diningHall) {
        return com.asuc.asucmobile.models.DiningHall.builder()
                .id(diningHall.getId())
                .name(diningHall.getName())
                .url(diningHall.getImageUrl())
                .build();
    }

}

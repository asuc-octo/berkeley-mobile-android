package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.Cafe;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

public class CafeTransformer {

    @Inject
    public CafeTransformer() {

    }

    /**
     * Transform Firestore QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Cafe> cafeQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.Cafe> cafes = new ArrayList<>();
        Cafe infraCafe = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraCafe = documentSnapshot.toObject(Cafe.class);
            if (infraCafe != null) {
                cafes.add(cafeInfraDomainTransformer(infraCafe));
            }
        }
        return cafes;
    }

    /**
     * Transform cafeList from infrastructure to domain model
     * @param cafeList
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.Cafe> cafeListInfraDomainTransformer(List<Cafe> cafeList) {
        List<com.asuc.asucmobile.domain.models.Cafe> retList = new ArrayList<>();
        for (Cafe cafe : cafeList) {
            retList.add(cafeInfraDomainTransformer(cafe));
        }
        return retList;
    }

    /**
     * Transform single cafe from infrastructure to domain model
     * TODO: process open/close hours obj
     * @param cafe
     * @return
     */
    public com.asuc.asucmobile.domain.models.Cafe cafeInfraDomainTransformer(Cafe cafe) {
        com.asuc.asucmobile.domain.models.Cafe ret = com.asuc.asucmobile.domain.models.Cafe.builder()
                .id(cafe.getId())
                .name(cafe.getName())
                .imageUrl(cafe.getImageUrl())
                .build();
        ret.setMeals(); // processing
        return ret;
    }
}

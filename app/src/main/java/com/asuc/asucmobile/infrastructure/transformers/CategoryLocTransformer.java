package com.asuc.asucmobile.infrastructure.transformers;

import com.asuc.asucmobile.infrastructure.models.CategoryLoc;
import com.asuc.asucmobile.infrastructure.models.OpenClose;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategoryLocTransformer {

    /**
     * Transform Firebase QuerySnapshot to domain model
     * @param qs
     * @return
     */
    public List<com.asuc.asucmobile.domain.models.CategoryLoc> categoryLocQSDomainTransformer(QuerySnapshot qs) {
        List<com.asuc.asucmobile.domain.models.CategoryLoc> categoryLocs = new ArrayList<>();
        com.asuc.asucmobile.infrastructure.models.CategoryLoc infraCategoryLoc = null;
        for (DocumentSnapshot documentSnapshot : qs.getDocuments()) {
            infraCategoryLoc = documentSnapshot.toObject(com.asuc.asucmobile.infrastructure.models.CategoryLoc.class);
            if (infraCategoryLoc != null) {
                categoryLocs.add(categoryLocInfraDomainTransformer(infraCategoryLoc));
            }

        }
        return categoryLocs;
    }

    public com.asuc.asucmobile.domain.models.CategoryLoc categoryLocInfraDomainTransformer(CategoryLoc categoryLoc) {
        ArrayList<Date> weeklyOpen = new ArrayList<>();
        ArrayList<Date> weeklyClose = new ArrayList<>();

        for (OpenClose openClose : categoryLoc.getOpenCloses()) {
            weeklyOpen.add(new Date(openClose.getOpenTIme()));
            weeklyClose.add(new Date(openClose.getCloseTime()));
        }

        return com.asuc.asucmobile.domain.models.CategoryLoc.builder()
                .category(categoryLoc.getCategory())
                .description1(categoryLoc.getName()) //TODO: not sure where to put this
                .description2(categoryLoc.getDetails())
                .imagelink(categoryLoc.getImageLink())
                .lat(categoryLoc.getLatitude())
                .lon(categoryLoc.getLongitude()) // TODO: use the open close
                .build();
    }
    
}
